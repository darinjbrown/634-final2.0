package com.__final_backend.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC configuration class that customizes cross-origin resource sharing (CORS) settings.
 * This configuration allows the frontend application to make requests to the backend API.
 */
@Configuration
public class WebConfig {

    /**
     * Creates a WebMvcConfigurer bean that configures CORS mappings.
     *
     * @return A WebMvcConfigurer with custom CORS configuration
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            /**
             * Configures CORS mappings for API endpoints.
             *
             * @param registry The CorsRegistry to add mappings to
             */
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**") // Allow CORS for API endpoints
                        .allowedOrigins("http://localhost:3000", "http://localhost:3001") // Allow requests from the frontend
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Allow specific HTTP methods
                        .allowedHeaders("*") // Allow all headers
                        .allowCredentials(true); // Allow cookies if needed
            }
        };
    }
}