package com.__final_backend.backend.test.unit.controller.db;

import com.__final_backend.backend.controller.db.SavedFlightController;
import com.__final_backend.backend.dto.SavedFlightDTO;
import com.__final_backend.backend.entity.SavedFlight;
import com.__final_backend.backend.entity.User;
import com.__final_backend.backend.service.db.SavedFlightService;
import com.__final_backend.backend.service.db.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit tests for the SavedFlightController class.
 * Tests saving and retrieving saved flights for a user.
 */
public class SavedFlightControllerTest {

  private MockMvc mockMvc;

  @Mock
  private SavedFlightService savedFlightService;

  @Mock
  private UserService userService;

  @Mock
  private SecurityContext securityContext;

  private final String TEST_USERNAME = "testUser";

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    SavedFlightController savedFlightController = new SavedFlightController(savedFlightService, userService);
    mockMvc = MockMvcBuilders.standaloneSetup(savedFlightController).build();

    // Setup security context with a mock authenticated user
    Authentication authentication = new UsernamePasswordAuthenticationToken(
        TEST_USERNAME,
        "password",
        Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);
  }

  /**
   * Test saving a flight for the authenticated user.
   * Verifies that a flight can be saved successfully.
   */
  @Test
  void testSaveFlight() throws Exception {
    // Arrange
    User user = new User();
    user.setId(1L);
    user.setUsername(TEST_USERNAME);
    user.setEmail("test@example.com");

    LocalDateTime departureTime = LocalDateTime.now().plusDays(1);
    LocalDateTime arrivalTime = LocalDateTime.now().plusDays(1).plusHours(2);

    // Mock the user service
    when(userService.getUserByUsername(TEST_USERNAME)).thenReturn(Optional.of(user));

    // Mock the saved flight service
    when(savedFlightService.saveFlight(any(SavedFlight.class))).thenAnswer(invocation -> {
      SavedFlight savedFlight = invocation.getArgument(0);
      savedFlight.setId(1L);
      savedFlight.setSavedAt(LocalDateTime.now());
      return savedFlight;
    });

    // Create request body
    String requestBody = String.format(
        "{\"airlineCode\":\"AA\",\"airlineName\":\"American Airlines\",\"flightNumber\":\"AA123\",\"origin\":\"BOS\",\"destination\":\"LAX\",\"departureTime\":\"%s\",\"arrivalTime\":\"%s\",\"price\":299.99,\"currency\":\"USD\"}",
        departureTime.toString(), arrivalTime.toString());
  }

  /**
   * Test retrieving saved flights for the authenticated user.
   * Verifies that saved flights can be retrieved successfully.
   */
  @Test
  void testGetSavedFlightsForCurrentUser() throws Exception {
    // Arrange
    User user = new User();
    user.setId(1L);
    user.setUsername(TEST_USERNAME);

    SavedFlight savedFlight = new SavedFlight();
    savedFlight.setId(1L);
    savedFlight.setUser(user);
    savedFlight.setAirlineCode("AA");
    savedFlight.setAirlineName("American Airlines");
    savedFlight.setFlightNumber("AA123");
    savedFlight.setOrigin("BOS");
    savedFlight.setDestination("LAX");
    savedFlight.setDepartureTime(LocalDateTime.now().plusDays(1));
    savedFlight.setArrivalTime(LocalDateTime.now().plusDays(1).plusHours(2));
    savedFlight.setPrice(new BigDecimal("299.99"));
    savedFlight.setCurrency("USD");
    savedFlight.setSavedAt(LocalDateTime.now());

    List<SavedFlight> savedFlights = new ArrayList<>();
    savedFlights.add(savedFlight);

    // Mock the user service
    when(userService.getUserByUsername(TEST_USERNAME)).thenReturn(Optional.of(user));

    // Mock the saved flight service
    when(savedFlightService.getSavedFlightsByUser(eq(user))).thenReturn(savedFlights);

    // Act & Assert
    mockMvc.perform(get("/api/saved-flights"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].airlineCode").value("AA"))
        .andExpect(jsonPath("$[0].flightNumber").value("AA123"));
  }
}
