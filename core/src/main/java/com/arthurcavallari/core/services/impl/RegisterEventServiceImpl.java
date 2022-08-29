package com.arthurcavallari.core.services.impl;

import com.arthurcavallari.core.models.EventRegistrationRequest;
import com.arthurcavallari.core.models.EventRegistrationResponse;
import com.arthurcavallari.core.services.RegisterEventService;
import com.arthurcavallari.core.services.config.RegisterEventServiceConfig;
import com.arthurcavallari.core.utils.JsonUtil;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.WCMException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonSyntaxException;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.sling.api.resource.*;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.propertytypes.ServiceDescription;
import org.osgi.service.component.propertytypes.ServiceVendor;
import org.osgi.service.metatype.annotations.Designate;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.sling.api.resource.ResourceResolverFactory.SUBSERVICE;

@Component(
        immediate = true,
        service = RegisterEventService.class
)
@ServiceDescription("Register Event Service")
@ServiceVendor("Cavallari Events")
@Slf4j
@Designate(ocd = RegisterEventServiceConfig.class)
public class RegisterEventServiceImpl implements RegisterEventService {
    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    private String registrationPath;
    private String apiHost;
    private String registrationApiEndpoint;
    private int timeout;

    public static final String EVENT_CREATION_SERVICE = "event-creation-service";

    @Activate
    protected void activate(RegisterEventServiceConfig config) {
        registrationPath = config.registrationPath();
        apiHost = config.apiHost();
        registrationApiEndpoint = config.registrationApiEndpoint();
        timeout = config.request_timeout();

        log.info("\n\n########### Activating RegisterEventServiceImpl");
    }

    @Override
    public boolean register(EventRegistrationRequest request) {
        EventRegistrationResponse response = postEventRegistration(request);

        return createEventPage(request, response);
    }

    /**
     * Fires off an event registration api call and returns an event registration response
     * @param request   EventRegistrationRequest
     * @return          EventRegistrationResponse
     */
    private EventRegistrationResponse postEventRegistration(EventRegistrationRequest request) {
        String json = JsonUtil.toJson(request);
        StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);

        HttpPost httpPost = new HttpPost(apiHost + registrationApiEndpoint);
        httpPost.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.toString());
        httpPost.setHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.toString());
        httpPost.setEntity(entity);

        log.debug("Executing request {}", httpPost.getRequestLine());

        CloseableHttpClient httpClient = getHttpClient();
        EventRegistrationResponse response;

        try (CloseableHttpResponse httpResponse = httpClient.execute(httpPost)) {
            StatusLine statusLine = httpResponse.getStatusLine();
            int statusCode = statusLine.getStatusCode();

            String responseBody = EntityUtils.toString(httpResponse.getEntity(), UTF_8);
            response = new ObjectMapper().readValue(responseBody, EventRegistrationResponse.class);

            if (statusCode != HttpStatus.SC_OK) {
                log.error("API {} returned client exception, with statusCode = {}, response = {}",
                        httpPost.getRequestLine(), statusCode, response);
            }

        } catch (JsonSyntaxException | IOException e) {
            log.error("\n############# Event registration failed!", e);
            response = new EventRegistrationResponse("ERR-001", e.getMessage());;
        }

        return response;
    }

    /**
     * Creates a new event details page using the event registration details
     */
    private boolean createEventPage(EventRegistrationRequest request, EventRegistrationResponse response) {
        /*
        TODO:
            1. store event id to be processed at a later time by an author (Amazon SQS?)
            2. author will fetch event ids from queue
            3. trigger workflow to prompt author to create event page
            4. once workflow is actioned, then make an api call to fetchDetailsById() (DynamoDB?)
            5. create new event details page using retrieved details from api call
        */

        Map<String, Object> params = Collections.singletonMap(SUBSERVICE, EVENT_CREATION_SERVICE);
        boolean result = false;

        try (ResourceResolver resolver = resourceResolverFactory.getServiceResourceResolver(params)) {
            Session session = resolver.adaptTo(Session.class);
            PageManager pageManager = resolver.adaptTo(PageManager.class);
            String eventPath = registrationPath + "/" + response.getEventId();
            Resource eventResource = resolver.getResource(eventPath);

            if (eventResource != null) {
                throw new RepositoryException("An event with this ID has already been registered");
            }
            if (pageManager == null) {
                throw new RepositoryException("Could not get Page Manager");
            }

            String EVENT_DETAILS_TEMPLATE = "/conf/event-management-challenge/settings/wcm/templates/eventdetails-page";
            Page eventPage = pageManager.create(registrationPath,
                    response.getEventId(),
                    EVENT_DETAILS_TEMPLATE,
                    request.getEventName(), true);
            session.save();

            setEventDetails(request, response, resolver, eventPage);

            result = true;
        } catch (WCMException | PersistenceException | RepositoryException e) {
            log.error("Could not create event page successfully", e);
        } catch (LoginException e) {
            log.error("Could not get service resource resolver", e);
        }

        return result;
    }

    /**
     * Save event details to page property
     */
    private void setEventDetails(EventRegistrationRequest request, EventRegistrationResponse response, ResourceResolver resolver, Page eventPage)
            throws PersistenceException {
        ModifiableValueMap pageProperties = eventPage.getContentResource().adaptTo(ModifiableValueMap.class);
        if (pageProperties == null) {
            throw new PersistenceException("Could not get event page properties");
        }
        pageProperties.put("eventId", response.getEventId());
        pageProperties.put("eventName", request.getEventName());
        pageProperties.put("organiserEmail", request.getOrganiserEmail());
        pageProperties.put("eventNotes", request.getEventNotes());
        resolver.commit();
    }

    private CloseableHttpClient getHttpClient() {
        RequestConfig requestConfig = getRequestConfig();

        return HttpClientBuilder.create()
                .useSystemProperties()
                .setDefaultRequestConfig(requestConfig)
                .build();
    }

    private RequestConfig getRequestConfig() {
        return RequestConfig.custom()
                .setConnectTimeout(timeout * 1000)
                .setConnectionRequestTimeout(timeout * 1000)
                .setSocketTimeout(timeout * 1000)
                .setCookieSpec(CookieSpecs.STANDARD)
                .build();
    }

}
