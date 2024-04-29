package com.arthurcavallari.core.services.config;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "Register Event Service Configuration")
public @interface RegisterEventServiceConfig {

    @AttributeDefinition(
            name = "Registration path",
            description = "Path where registration ids will be stored",
            type = AttributeType.STRING
    )
    String registrationPath() default "/content/event-management-challenge/us/en/events";

    @AttributeDefinition(
            name = "API host",
            type = AttributeType.STRING
    )
    String apiHost() default "http://admin:admin@localhost:4502";

    @AttributeDefinition(
            name = "Registration API endpoint",
            type = AttributeType.STRING
    )
    String registrationApiEndpoint() default "/bin/api/v1/register";

    @AttributeDefinition(
            name = "Request timeout",
            description = "Set request timeout in seconds. Default: 5",
            type = AttributeType.INTEGER
    )
    int request_timeout() default 5;
}

