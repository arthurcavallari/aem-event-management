package com.arthurcavallari.core.models;

import com.day.cq.wcm.api.Page;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

@Model(adaptables = {Resource.class, SlingHttpServletRequest.class}, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Slf4j
public class EventDetailsModel {
    @Inject
    private Page currentPage;
    @Getter
    private String eventId;
    @Getter
    private String eventName;
    @Getter
    private String organiserEmail;
    @Getter
    private String eventNotes;

    @PostConstruct
    protected void init() {
        ValueMap pageProps = currentPage.getProperties();
        eventId = pageProps.get("eventId", "");
        eventName = pageProps.get("eventName", "");
        organiserEmail = pageProps.get("organiserEmail", "");
        eventNotes = pageProps.get("eventNotes", "");
    }
}
