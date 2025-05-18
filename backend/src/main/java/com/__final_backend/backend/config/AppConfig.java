package com.__final_backend.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Application configuration class for defining Spring beans.
 *
 * <p>
 * This class provides configuration for various application components and
 * services.
 */
@Configuration
public class AppConfig {

    /**
     * Creates a RestTemplate bean for making HTTP requests.
     *
     * <p>
     * The RestTemplate is used for client-side HTTP access and provides a template
     * method API.
     * It handles HTTP connections and implements RESTful principles.
     *
     * @return A new RestTemplate instance configured for making HTTP requests
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}