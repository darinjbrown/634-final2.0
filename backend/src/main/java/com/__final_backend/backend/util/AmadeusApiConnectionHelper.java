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
 * 
 * This component performs a diagnostic check of the Amadeus API connection when
 * the
 * "apicheck" Spring profile is active. It validates that:
 * 1. The application can create an authenticated Amadeus client
 * 2. API credentials are valid and working
 * 3. Basic API calls can be executed successfully
 * 
 * The class implements CommandLineRunner to execute automatically during
 * application startup.
 * Results of the connection test are logged for diagnostic purposes.
 */
@Component
@Profile("apicheck") // Only active when "apicheck" profile is enabled
public class AmadeusApiConnectionHelper implements CommandLineRunner {

  private static final Logger logger = LoggerFactory.getLogger(AmadeusApiConnectionHelper.class);

  @Value("${amadeus.api.key}")
  private String apiKey;

  @Value("${amadeus.api.secret}")
  private String apiSecret;

  /**
   * Executes the Amadeus connection check when the application starts.
   * This method is called automatically by Spring Boot as part of the
   * CommandLineRunner interface implementation.
   * 
   * @param args Command line arguments passed to the application
   */
  @Override
  public void run(String... args) {
    checkAmadeusConnection();
  }

  /**
   * Tests the connection to the Amadeus API by creating an authenticated client
   * and executing a simple airline lookup request.
   * 
   * This test validates:
   * - API credentials can successfully authenticate
   * - The connection to Amadeus servers is working
   * - API responses are properly received and parsed
   * 
   * Results and any errors are logged at appropriate levels.
   */
  public void checkAmadeusConnection() {
    logger.info("Checking Amadeus API connection");
    logger.info("Using API Key starting with: {}",
        apiKey != null ? apiKey.substring(0, Math.min(4, apiKey.length())) : "null");

    try {
      // Create Amadeus client
      Amadeus amadeus = Amadeus.builder(apiKey, apiSecret)
          .setLogLevel("error")
          .setHostname("test")
          .setSsl(true)
          .setPort(443)
          .build();

      logger.info("Amadeus client created successfully");

      // Test a simple API call
      try {
        // Try to get information about an airline as a simple authenticated API call
        Airline[] airlines = amadeus.referenceData.airlines.get(
            Params.with("airlineCodes", "BA"));

        logger.info("API call successful: {} airlines found", airlines != null ? airlines.length : 0);
        if (airlines != null && airlines.length > 0) {
          logger.info("First airline: {}", airlines[0].getCommonName());
        }
      } catch (ResponseException e) {
        logger.error("Amadeus API call error: {} - {}", e.getCode(), e.getMessage());
      }

    } catch (Exception e) {
      logger.error("Unexpected error connecting to Amadeus API: {}", e.getMessage());
    }
  }
}
