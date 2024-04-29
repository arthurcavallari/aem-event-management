package com.arthurcavallari.core.models;

import lombok.Getter;
import lombok.Setter;

public class EventRegistrationResponse {
    /**
     * Event ID
     *
     * @return Unique event ID
     */
    @Getter @Setter
    private String eventId;

    /**
     * Error ID
     *
     * @return An error id, should the api call fail
     */
    @Getter @Setter
    private String errorId;

    /**
     * Error message
     *
     * @return An error message, should the api call fail
     */
    @Getter @Setter
    private String errorMessage;

    @SuppressWarnings("unused")
    public EventRegistrationResponse() {
        this(null, null, null);
    }

    public EventRegistrationResponse(String eventId) {
        this(eventId, null, null);
    }

    public EventRegistrationResponse(String errorId, String errorMessage) {
        this(null, errorId, errorMessage);
    }

    public EventRegistrationResponse(String eventId, String errorId, String errorMessage) {
        this.eventId = eventId;
        this.errorId = errorId;
        this.errorMessage = errorMessage;
    }

    @Override
    public String toString() {
        return "EventRegistrationResponse{" +
                "eventId='" + eventId + '\'' +
                ", errorId='" + errorId + '\'' +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }
}
