package com.__final_backend.backend.test.unit.service.db;

import com.__final_backend.backend.entity.SavedFlight;
import com.__final_backend.backend.entity.User;
import com.__final_backend.backend.repository.SavedFlightRepository;
import com.__final_backend.backend.service.db.SavedFlightService;
import com.__final_backend.backend.service.db.SavedFlightServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for SavedFlightServiceImpl
 * Tests basic saved flight operations
 */
class SavedFlightServiceImplTest {

  @Mock
  private SavedFlightRepository savedFlightRepository;

  private SavedFlightService savedFlightService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    savedFlightService = new SavedFlightServiceImpl(savedFlightRepository);
  }

  /**
   * Test saving a flight and retrieving it by user
   * Verifies that a flight can be saved and then retrieved for a specific user
   */
  @Test
  void testSaveAndRetrieveFlightByUser() {
    // Arrange
    User user = new User();
    user.setId(1L);
    user.setUsername("testUser");

    SavedFlight savedFlight = new SavedFlight();
    savedFlight.setUser(user);
    savedFlight.setFlightNumber("AA123");
    savedFlight.setOrigin("JFK");
    savedFlight.setDestination("LAX");
    savedFlight.setDepartureTime(LocalDateTime.now());
    savedFlight.setArrivalTime(LocalDateTime.now().plusHours(6));

    // Configure repository mock behaviors
    when(savedFlightRepository.save(any(SavedFlight.class))).thenAnswer(invocation -> {
      SavedFlight flight = invocation.getArgument(0);
      flight.setId(1L);
      return flight;
    });

    when(savedFlightRepository.findByUser(user)).thenReturn(Arrays.asList(savedFlight));

    // Act
    // Save the flight
    SavedFlight result = savedFlightService.saveFlight(savedFlight);

    // Retrieve flights for the user
    List<SavedFlight> userFlights = savedFlightService.getSavedFlightsByUser(user);

    // Assert
    assertNotNull(result, "Saved flight should not be null");
    assertEquals(1L, result.getId(), "Flight ID should be assigned");

    assertNotNull(userFlights, "Retrieved flights should not be null");
    assertFalse(userFlights.isEmpty(), "User should have saved flights");
    assertEquals(1, userFlights.size(), "User should have exactly one saved flight");
    assertEquals("AA123", userFlights.get(0).getFlightNumber(), "Flight number should match");
    assertEquals("JFK", userFlights.get(0).getOrigin(), "Origin should match");
    assertEquals("LAX", userFlights.get(0).getDestination(), "Destination should match");

    // Verify repository interactions
    verify(savedFlightRepository).save(any(SavedFlight.class));
    verify(savedFlightRepository).findByUser(user);
  }
}
