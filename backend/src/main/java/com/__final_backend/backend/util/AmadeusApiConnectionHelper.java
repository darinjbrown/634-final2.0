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
 * Utility class to check the Amadeus API connectivity.
 * Can be enabled by activating the "apicheck" profile.
 */
@Component
@Profile("apicheck") // Only active when "apicheck" profile is enabled
public class AmadeusApiConnectionHelper implements CommandLineRunner {

  private static final Logger logger = LoggerFactory.getLogger(AmadeusApiConnectionHelper.class);

  @Value("${amadeus.api.key}")
  private String apiKey;

  @Value("${amadeus.api.secret}")
  private String apiSecret;

  @Override
  public void run(String... args) {
    checkAmadeusConnection();
  }

  /**
   * Tests the connection to the Amadeus API
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
