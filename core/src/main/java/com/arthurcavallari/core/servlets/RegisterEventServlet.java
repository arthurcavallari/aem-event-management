package com.arthurcavallari.core.servlets;

import com.arthurcavallari.core.models.EventRegistrationRequest;
import com.arthurcavallari.core.services.RegisterEventService;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletPaths;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.propertytypes.ServiceDescription;
import org.osgi.service.component.propertytypes.ServiceVendor;

import javax.servlet.Servlet;

/**
 * Internal servlet to be used to send off a request to the backend and process the event registration
 */
@Component(service = {Servlet.class})
@SlingServletPaths("/bin/eventRegistration")
@ServiceDescription("Register Event Servlet")
@ServiceVendor("Cavallari Events")
@Slf4j
public class RegisterEventServlet extends SlingAllMethodsServlet {
    
    @Reference
    private RegisterEventService registerEventService;

    @Override
    protected void doPost(@NotNull SlingHttpServletRequest request, @NotNull SlingHttpServletResponse response) {
        try {
            String jsonString = IOUtils.toString(request.getInputStream(), request.getCharacterEncoding());
            EventRegistrationRequest eventRegistrationRequest = new Gson().fromJson(jsonString, EventRegistrationRequest.class);

            boolean registrationStatus = registerEventService.register(eventRegistrationRequest);

            if (registrationStatus) {
                response.setStatus(SlingHttpServletResponse.SC_OK);
            } else {
                response.setStatus(SlingHttpServletResponse.SC_BAD_REQUEST);
            }
        } catch (Exception ex) {
            log.error("Could not register event", ex);
            response.setStatus(SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
