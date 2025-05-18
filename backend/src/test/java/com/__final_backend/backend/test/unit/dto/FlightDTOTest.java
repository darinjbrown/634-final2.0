package com.__final_backend.backend.test.unit.dto;

import com.__final_backend.backend.dto.FlightDTO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the FlightDTO class.
 * Tests basic property getters and setters.
 */
public class FlightDTOTest {

  /**
   * Test setting and getting properties of FlightDTO.
   * Verifies that all properties are correctly stored and retrieved.
   */
  @Test
  void testPropertiesGettersAndSetters() {
    // Arrange - Create test data
    String airline = "American Airlines";
    String flightNumber = "AA123";
    String departure = "BOS";
    String arrival = "LAX";
    String departureTime = "10:00 AM";
    String arrivalTime = "1:30 PM";
    Double price = 299.99;

    // Act - Create DTO and set properties
    FlightDTO flightDTO = new FlightDTO();
    flightDTO.setAirline(airline);
    flightDTO.setFlightNumber(flightNumber);
    flightDTO.setDeparture(departure);
    flightDTO.setArrival(arrival);
    flightDTO.setDepartureTime(departureTime);
    flightDTO.setArrivalTime(arrivalTime);
    flightDTO.setPrice(price);

    // Assert - Verify that properties were correctly set and can be retrieved
    assertEquals(airline, flightDTO.getAirline(), "Airline should match");
    assertEquals(flightNumber, flightDTO.getFlightNumber(), "Flight number should match");
    assertEquals(departure, flightDTO.getDeparture(), "Departure should match");
    assertEquals(arrival, flightDTO.getArrival(), "Arrival should match");
    assertEquals(departureTime, flightDTO.getDepartureTime(), "Departure time should match");
    assertEquals(arrivalTime, flightDTO.getArrivalTime(), "Arrival time should match");
    assertEquals(price, flightDTO.getPrice(), "Price should match");
  }

  /**
   * Test the toString method of FlightDTO.
   * Verifies that the toString output contains all property values.
   */
  @Test
  void testToString() {
    // Arrange - Create DTO with test data
    FlightDTO flightDTO = new FlightDTO();
    flightDTO.setAirline("Delta");
    flightDTO.setFlightNumber("DL456");
    flightDTO.setDeparture("JFK");
    flightDTO.setArrival("SFO");
    flightDTO.setDepartureTime("9:00 AM");
    flightDTO.setArrivalTime("12:30 PM");
    flightDTO.setPrice(349.99);

    // Act - Get the string representation
    String toString = flightDTO.toString();

    // Assert - Verify that the string contains all property values
    assertTrue(toString.contains("Delta"), "toString should contain airline name");
    assertTrue(toString.contains("DL456"), "toString should contain flight number");
    assertTrue(toString.contains("JFK"), "toString should contain departure");
    assertTrue(toString.contains("SFO"), "toString should contain arrival");
    assertTrue(toString.contains("9:00 AM"), "toString should contain departure time");
    assertTrue(toString.contains("12:30 PM"), "toString should contain arrival time");
    assertTrue(toString.contains("349.99"), "toString should contain price");
  }

  /**
   * Test properties with null values.
   * Verifies that the DTO can handle null values without throwing exceptions.
   */
  @Test
  void testNullProperties() {
    // Act - Create DTO without setting any properties
    FlightDTO flightDTO = new FlightDTO();

    // Assert - Verify that getters return null without exceptions
    assertNull(flightDTO.getAirline(), "Airline should be null");
    assertNull(flightDTO.getFlightNumber(), "Flight number should be null");
    assertNull(flightDTO.getDeparture(), "Departure should be null");
    assertNull(flightDTO.getArrival(), "Arrival should be null");
    assertNull(flightDTO.getDepartureTime(), "Departure time should be null");
    assertNull(flightDTO.getArrivalTime(), "Arrival time should be null");
    assertNull(flightDTO.getPrice(), "Price should be null");
  }
}
