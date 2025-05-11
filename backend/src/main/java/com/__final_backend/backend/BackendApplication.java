package com.__final_backend.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the SkyExplorer flight search application backend
 * service.
 * 
 * This class initializes and launches the Spring Boot application, setting up
 * the
 * application context, auto-configuration, and component scanning. The
 * application
 * provides flight search functionality, user management, and integration with
 * the
 * Amadeus API for accessing flight data.
 * 
 * Configuration is provided via application.properties and environment
 * variables.
 */
@SpringBootApplication(scanBasePackages = { "com.final_backend.backend", "com.__final_backend.backend" })
public class BackendApplication {

	/**
	 * The main method that starts the Spring application context and runs the
	 * embedded web server.
	 * 
	 * @param args Command line arguments passed to the application
	 */
	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}
}