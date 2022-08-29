package com.arthurcavallari.core.models;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class EventRegistrationRequest {
    /**
     * Event name
     */
    private String eventName;

    /**
     * Event organiser email address
     */
    private String organiserEmail;

    /**
     * Event notes
     */
    private String eventNotes;

    public EventRegistrationRequest(String eventName, String organiserEmail, String eventNotes) {
        this.eventName = eventName;
        this.organiserEmail = organiserEmail;
        this.eventNotes = eventNotes;
    }
}
