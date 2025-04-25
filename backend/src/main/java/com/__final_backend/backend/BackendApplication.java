package com.__final_backend.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the travel application backend service.
 * This class initializes and launches the Spring Boot application.
 * <p>
 * The {@code @SpringBootApplication} annotation enables auto-configuration,
 * component scanning, and defines this class as a configuration class.
 * The scanBasePackages attribute defines where Spring should look for
 * components, repositories, services, and other Spring-managed classes.
 */
@SpringBootApplication(scanBasePackages = {"com.final_backend.backend", "com.__final_backend.backend"})
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