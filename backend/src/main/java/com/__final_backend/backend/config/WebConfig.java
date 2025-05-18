package com.__final_backend.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC configuration class that customizes cross-origin resource sharing
 * (CORS) settings.
 *
 * <p>
 * This configuration allows the frontend application to make secure requests to
 * the backend API
 * by defining appropriate CORS policies for different API endpoints.
 */
@Configuration
public class WebConfig {

        /**
         * Creates a WebMvcConfigurer bean that configures CORS mappings and resource
         * handlers.
         *
         * <p>
         * This configuration establishes security boundaries for cross-origin requests
         * and
         * defines how static resources are served.
         *
         * @return A WebMvcConfigurer with custom CORS configuration
         */
        @Bean
        public WebMvcConfigurer corsConfigurer() {
                return new WebMvcConfigurer() {
                        /**
                         * Configures CORS mappings for API endpoints.
                         *
                         * <p>
                         * Defines security policies for cross-origin requests to different API paths,
                         * including allowed origins, methods, headers, and credentials.
                         *
                         * @param registry The CorsRegistry to add mappings to
                         */
                        @Override
                        public void addCorsMappings(CorsRegistry registry) {
                                // Secure CORS configuration for API endpoints
                                registry.addMapping("/api/**")
                                                // Explicitly specify allowed origins (no wildcards)
                                                .allowedOrigins("http://localhost:3000", "http://localhost:3001",
                                                                "http://localhost:8080")
                                                // Explicitly specify allowed methods
                                                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD")
                                                // Explicitly specify allowed headers
                                                .allowedHeaders(
                                                                HttpHeaders.AUTHORIZATION,
                                                                HttpHeaders.CONTENT_TYPE,
                                                                HttpHeaders.ACCEPT,
                                                                "X-CSRF-TOKEN",
                                                                "X-Requested-With")
                                                // Expose CSRF token header to JavaScript
                                                .exposedHeaders(
                                                                HttpHeaders.AUTHORIZATION,
                                                                "X-CSRF-TOKEN")
                                                .allowCredentials(true) // Enable cookies for authentication
                                                .maxAge(3600L); // Cache preflight response for 1 hour // More
                                                                // restrictive CORS for admin endpoints
                                registry.addMapping("/api/admin/**")
                                                .allowedOrigins("http://localhost:3000") // Only admin portal
                                                .allowedMethods("GET", "POST", "PUT", "DELETE")
                                                .allowedHeaders(
                                                                HttpHeaders.AUTHORIZATION,
                                                                HttpHeaders.CONTENT_TYPE,
                                                                HttpHeaders.ACCEPT,
                                                                "X-CSRF-TOKEN")
                                                .exposedHeaders("X-CSRF-TOKEN")
                                                .allowCredentials(true)
                                                .maxAge(1800L); // Cache preflight for 30 minutes
                        }

                        /**
                         * Configures resource handlers for serving static resources.
                         *
                         * <p>
                         * Defines how static resources like CSS, JavaScript, and images are served
                         * from the classpath with appropriate caching settings.
                         *
                         * @param registry The ResourceHandlerRegistry to add resource handlers to
                         */
                        @Override
                        public void addResourceHandlers(ResourceHandlerRegistry registry) {
                                registry.addResourceHandler("/static/**")
                                                .addResourceLocations("classpath:/static/")
                                                .setCachePeriod(3600);
                        }
                };
        }
}