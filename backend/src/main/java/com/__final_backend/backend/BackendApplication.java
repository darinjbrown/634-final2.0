package com.__final_backend.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the SkyExplorer flight search application backend
 * service.
 * <p>
 * This class initializes and launches the Spring Boot application, setting up
 * the
 * application context, auto-configuration, and component scanning. The
 * application
 * provides the following key functionalities:
 * <ul>
 * <li>Flight search and booking using the Amadeus API</li>
 * <li>User account management and authentication</li>
 * <li>Saved flight preferences storage</li>
 * <li>Booking history tracking</li>
 * </ul>
 * <p>
 * Configuration is provided via application.properties and environment
 * variables.
 * The application uses Spring Security for authentication and PostgreSQL for
 * data storage.
 * <p>
 * To run this application, use either the Spring Boot Maven plugin or build an
 * executable JAR.
 */
@SpringBootApplication(scanBasePackages = { "com.final_backend.backend", "com.__final_backend.backend" })
public class BackendApplication {

	/**
	 * The main method that starts the Spring application context and runs the
	 * embedded web server.
	 * <p>
	 * This method delegates to Spring's {@link SpringApplication} to bootstrap the
	 * application. It configures the embedded web server, sets up the Spring
	 * context,
	 * and starts all auto-configured components.
	 * 
	 * @param args command line arguments passed to the application, which can
	 *             include
	 *             Java system properties and Spring profile configurations
	 */
	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}
}