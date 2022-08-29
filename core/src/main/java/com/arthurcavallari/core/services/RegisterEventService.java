package com.arthurcavallari.core.services;

import com.arthurcavallari.core.models.EventRegistrationRequest;

public interface RegisterEventService {
    /**
     * Registers event with event API
     */
    boolean register(EventRegistrationRequest eventRegistrationRequest);
}
