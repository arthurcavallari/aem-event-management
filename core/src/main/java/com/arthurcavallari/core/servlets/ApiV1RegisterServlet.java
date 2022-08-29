package com.arthurcavallari.core.servlets;

import com.arthurcavallari.core.models.EventRegistrationResponse;
import com.arthurcavallari.core.utils.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletPaths;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.propertytypes.ServiceDescription;
import org.osgi.service.component.propertytypes.ServiceVendor;

import javax.servlet.Servlet;
import java.io.IOException;
import java.util.UUID;

/**
 * This servlet represents a backend service that would handle some kind of processing of the event details and return an id
 */
@Component(service = {Servlet.class})
@SlingServletPaths("/bin/api/v1/register")
@ServiceDescription("Register Event Servlet")
@ServiceVendor("Cavallari Events")
@Slf4j
public class ApiV1RegisterServlet extends SlingAllMethodsServlet {

    @Override
    protected void doPost(@NotNull SlingHttpServletRequest request, @NotNull SlingHttpServletResponse response) {
        response.setContentType("application/json");

        try {
            UUID uuid = UUID.randomUUID();
            EventRegistrationResponse apiResponse = new EventRegistrationResponse(uuid.toString());

            response.getWriter().write(JsonUtil.toJson(apiResponse));
            response.setStatus(SlingHttpServletResponse.SC_OK);
        } catch (Exception ex) {
            log.error("Could not register event", ex);
            response.setStatus(SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
