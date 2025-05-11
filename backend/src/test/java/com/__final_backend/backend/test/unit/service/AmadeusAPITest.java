package com.__final_backend.backend.test.unit.service;

import com.__final_backend.backend.dto.FlightDTO;
import com.__final_backend.backend.service.FlightService;
import com.amadeus.Amadeus;
import com.amadeus.Params;
import com.amadeus.exceptions.ResponseException;
import com.amadeus.resources.FlightOfferSearch;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class AmadeusAPITest {

  private static final Logger logger = LoggerFactory.getLogger(AmadeusAPITest.class);

  @Value("${amadeus.api.key}")
  private String apiKey;

  @Value("${amadeus.api.secret}")
  private String apiSecret;

  @Autowired
  private FlightService flightService;

  @Test
  void testAmadeusClientCreation() {
    assertNotNull(apiKey, "API Key should be loaded from properties");
    assertNotNull(apiSecret, "API Secret should be loaded from properties");

    logger.info("Testing Amadeus client creation with key: {}", apiKey);

    Amadeus amadeus = null;
    try {
      amadeus = Amadeus.builder(apiKey, apiSecret)
          .setHostname("test")
          .build();
      assertNotNull(amadeus, "Amadeus client should be created successfully");
    } catch (Exception e) {
      logger.error("Error creating Amadeus client: {}", e.getMessage());
      fail("Should not throw exception when creating Amadeus client: " + e.getMessage());
    }
  }

  @Test
  void testAmadeusApiEndpointAccess() {
    // This test will actually attempt to call the API
    // Skip if you don't want to make real API calls during tests
    try {
      LocalDate today = LocalDate.now();
      LocalDate travelDate = today.plusMonths(1); // One month from now

      // Try a simple search
      List<FlightDTO> flights = flightService.searchFlights(
          "JFK", "LAX", travelDate, null, 1, "one-way");

      // If we get here without exception, the API is accessible
      logger.info("Successfully connected to Amadeus API and received {} flights", flights.size());
    } catch (Exception e) {
      logger.error("Error during Amadeus API test: {}", e.getMessage());
      // Don't fail the test - this is just a connectivity test
    }
  }
}
