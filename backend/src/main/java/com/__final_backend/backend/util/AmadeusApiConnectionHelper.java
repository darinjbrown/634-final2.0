package com.__final_backend.backend.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.amadeus.Amadeus;
import com.amadeus.Params;
import com.amadeus.exceptions.ResponseException;
import com.amadeus.resources.Airline;

/**
 * Utility class to check and verify the Amadeus API connectivity on application
 * startup.
 * <p>
 * This component performs a diagnostic check of the Amadeus API connection when
 * the
 * "apicheck" Spring profile is active. It validates that:
 * <ol>
 * <li>The application can create an authenticated Amadeus client</li>
 * <li>API credentials are valid and working</li>
 * <li>Basic API calls can be executed successfully</li>
 * </ol>
 * <p>
 * The class implements CommandLineRunner to execute automatically during
 * application
 * startup. Results of the connection test are logged for diagnostic purposes.
 * <p>
 * This diagnostic tool helps identify API connectivity issues early in the
 * application
 * lifecycle, preventing runtime errors during normal operation.
 */
@Component
@Profile("apicheck") // Only active when "apicheck" profile is enabled
public class AmadeusApiConnectionHelper implements CommandLineRunner {
  private static final Logger logger = LoggerFactory.getLogger(AmadeusApiConnectionHelper.class);

  /** The Amadeus API key loaded from application properties. */
  @Value("${amadeus.api.key}")
  private String apiKey;

  /** The Amadeus API secret loaded from application properties. */
  @Value("${amadeus.api.secret}")
  private String apiSecret;

  /**
   * Executes the Amadeus connection check when the application starts.
   * <p>
   * This method is called automatically by Spring Boot as part of the
   * CommandLineRunner interface implementation. It delegates the actual
   * connection testing to {@link #checkAmadeusConnection()}.
   * 
   * @param args Command line arguments passed to the application (not used in
   *             this implementation)
   */
  @Override
  public void run(String... args) {
    checkAmadeusConnection();
  }

  /**
   * Tests the connection to the Amadeus API by creating an authenticated client
   * and executing a simple airline lookup request.
   * <p>
   * This test validates:
   * <ul>
   * <li>API credentials can successfully authenticate</li>
   * <li>The connection to Amadeus servers is working</li>
   * <li>API responses are properly received and parsed</li>
   * </ul>
   * <p>
   * Results and any errors are logged at appropriate levels. The method is public
   * to allow manual invocation of the connectivity check when needed, not just at
   * startup.
   * <p>
   * Note: This method uses the test environment of the Amadeus API.
   */
  public void checkAmadeusConnection() {
    logger.info("Checking Amadeus API connection");
    // Log only first few characters of API key for security reasons
    logger.info("Using API Key starting with: {}",
        apiKey != null ? apiKey.substring(0, Math.min(4, apiKey.length())) + "..." : "null");

    try {
      // Create Amadeus client with test environment configuration
      Amadeus amadeus = Amadeus.builder(apiKey, apiSecret)
          .setLogLevel("error") // Only log errors from the Amadeus client itself
          .setHostname("test") // Use the test environment
          .setSsl(true) // Use secure connection
          .setPort(443) // Standard HTTPS port
          .build();

      logger.info("Amadeus client created successfully");

      // Test a simple API call
      try {
        // Try to get information about an airline (British Airways) as a simple
        // authenticated API call
        Airline[] airlines = amadeus.referenceData.airlines.get(
            Params.with("airlineCodes", "BA"));

        // Log the results of the API call
        logger.info("API call successful: {} airlines found", airlines != null ? airlines.length : 0);
        if (airlines != null && airlines.length > 0) {
          logger.info("First airline: {}", airlines[0].getCommonName());
        } else {
          logger.warn("API call returned no airlines, but did not error");
        }
      } catch (ResponseException e) {
        // Log specific Amadeus API errors with their code and message
        logger.error("Amadeus API call error: {} - {}", e.getCode(), e.getMessage());
      }
    } catch (Exception e) {
      // Log any unexpected errors that might occur during client creation or other
      // operations
      logger.error("Unexpected error connecting to Amadeus API: {}", e.getMessage());
      logger.debug("Full exception details", e); // Log stack trace at debug level
    }
  }
}
