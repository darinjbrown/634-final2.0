package com.__final_backend.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = { "com.final_backend.backend", "com.__final_backend.backend" })
/**
 * Main entry point for the travel application backend service.
 * This class initializes and launches the Spring Boot application.
 */
public class BackendApplication {
	/**
	 * The main method that starts the Spring application context and runs the
	 * embedded web server.
	 */
	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}
}