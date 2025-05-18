package com.__final_backend.backend.controller.db;

import com.__final_backend.backend.dto.SavedFlightDTO;
import com.__final_backend.backend.entity.SavedFlight;
import com.__final_backend.backend.entity.User;
import com.__final_backend.backend.service.db.SavedFlightService;
import com.__final_backend.backend.service.db.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * REST controller for saved flight operations.
 *
 * <p>
 * This controller provides endpoints for managing saved flights, including
 * creation,
 * retrieval, and deletion. All operations are authenticated and restricted to
 * the
 * owner of the saved flights.
 */
@RestController
@RequestMapping("/api/saved-flights")
public class SavedFlightController {

  private static final Logger logger = LoggerFactory.getLogger(SavedFlightController.class);

  private final SavedFlightService savedFlightService;
  private final UserService userService;

  /**
   * Constructs a new SavedFlightController with required dependencies.
   *
   * @param savedFlightService service for saved flight operations
   * @param userService        service for user management operations
   */
  public SavedFlightController(SavedFlightService savedFlightService, UserService userService) {
    this.savedFlightService = savedFlightService;
    this.userService = userService;
  }

  /**
   * Saves a flight for the authenticated user.
   *
   * <p>
   * This endpoint allows users to save flight information to their account for
   * future reference.
   * It requires authentication and associates the saved flight with the current
   * user.
   *
   * @param flightDTO the flight data transfer object containing flight details to
   *                  save
   * @return ResponseEntity with saved flight data or error information
   */
  @PostMapping
  public ResponseEntity<?> saveFlight(@RequestBody SavedFlightDTO flightDTO) {
    try {
      // Get the authenticated user
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      String username = authentication.getName();

      Optional<User> userOpt = userService.getUserByUsername(username);
      if (!userOpt.isPresent()) {
        logger.error("User not found: {}", username);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body("User not authenticated or not found");
      }

      User user = userOpt.get();

      // Convert DTO to entity
      SavedFlight savedFlight = new SavedFlight();
      savedFlight.setUser(user);
      savedFlight.setAirlineCode(flightDTO.getAirlineCode());
      savedFlight.setAirlineName(flightDTO.getAirlineName());
      savedFlight.setFlightNumber(flightDTO.getFlightNumber());
      savedFlight.setOrigin(flightDTO.getOrigin());
      savedFlight.setDestination(flightDTO.getDestination());
      savedFlight.setDepartureTime(flightDTO.getDepartureTime());
      savedFlight.setArrivalTime(flightDTO.getArrivalTime());
      savedFlight.setPrice(flightDTO.getPrice());
      savedFlight.setCurrency(flightDTO.getCurrency() != null ? flightDTO.getCurrency() : "USD");
      savedFlight.setSavedAt(LocalDateTime.now());

      // Save the flight
      savedFlight = savedFlightService.saveFlight(savedFlight);

      logger.info("Flight saved successfully for user {}: {}", username, savedFlight.getFlightNumber());

      // Convert entity back to DTO for response
      SavedFlightDTO responseDTO = convertToDTO(savedFlight);

      return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    } catch (DateTimeParseException e) {
      logger.error("Invalid date format in flight data", e);
      return ResponseEntity.badRequest().body("Invalid date format");
    } catch (Exception e) {
      logger.error("Error saving flight", e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("An error occurred while saving the flight");
    }
  }

  /**
   * Retrieves all saved flights for the authenticated user.
   *
   * <p>
   * This endpoint returns all flights that the current user has saved.
   * It requires authentication and only returns flights owned by the current
   * user.
   *
   * @return ResponseEntity with list of saved flights or error information
   */
  @GetMapping
  public ResponseEntity<?> getSavedFlightsForCurrentUser() {
    try {
      // Get the authenticated user
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      String username = authentication.getName();

      Optional<User> userOpt = userService.getUserByUsername(username);
      if (!userOpt.isPresent()) {
        logger.error("User not found: {}", username);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body("User not authenticated or not found");
      }

      User user = userOpt.get();

      // Get saved flights for the user
      List<SavedFlight> savedFlights = savedFlightService.getSavedFlightsByUser(user);

      // Convert to DTOs
      List<SavedFlightDTO> flightDTOs = savedFlights.stream()
          .map(this::convertToDTO)
          .collect(Collectors.toList());

      logger.info("Retrieved {} saved flights for user {}", flightDTOs.size(), username);

      return ResponseEntity.ok(flightDTOs);
    } catch (Exception e) {
      logger.error("Error retrieving saved flights", e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("An error occurred while retrieving saved flights");
    }
  }

  /**
   * Retrieves saved flights for the authenticated user with pagination.
   *
   * <p>
   * This endpoint returns a paginated list of saved flights for the current user.
   * It requires authentication and only returns flights owned by the current
   * user.
   *
   * @param page page number (zero-based)
   * @param size number of items per page
   * @return ResponseEntity with paginated list of saved flights or error
   *         information
   */
  @GetMapping("/paged")
  public ResponseEntity<?> getPagedSavedFlightsForCurrentUser(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    try {
      // Get the authenticated user
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      String username = authentication.getName();

      Optional<User> userOpt = userService.getUserByUsername(username);
      if (!userOpt.isPresent()) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body("User not authenticated or not found");
      }

      User user = userOpt.get();

      // Get paged saved flights for the user
      Page<SavedFlight> savedFlightsPage = savedFlightService.getSavedFlightsByUser(
          user, PageRequest.of(page, size));

      // Convert to DTOs
      List<SavedFlightDTO> flightDTOs = savedFlightsPage.getContent().stream()
          .map(this::convertToDTO)
          .collect(Collectors.toList());

      return ResponseEntity.ok(flightDTOs);
    } catch (Exception e) {
      logger.error("Error retrieving paged saved flights", e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("An error occurred while retrieving saved flights");
    }
  }

  /**
   * Retrieves a specific saved flight by its ID.
   *
   * <p>
   * This endpoint returns details of a specific saved flight. It requires
   * authentication
   * and performs an ownership check to ensure users can only access their own
   * saved flights.
   *
   * @param id the saved flight ID to retrieve
   * @return ResponseEntity with saved flight details or error information
   */
  @GetMapping("/{id}")
  public ResponseEntity<?> getSavedFlightById(@PathVariable Long id) {
    try {
      // Get the authenticated user
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      String username = authentication.getName();

      Optional<User> userOpt = userService.getUserByUsername(username);
      if (!userOpt.isPresent()) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body("User not authenticated or not found");
      }

      // Get the saved flight
      Optional<SavedFlight> savedFlightOpt = savedFlightService.getSavedFlightById(id);

      if (!savedFlightOpt.isPresent()) {
        return ResponseEntity.notFound().build();
      }

      SavedFlight savedFlight = savedFlightOpt.get();

      // Security check - ensure the flight belongs to the authenticated user
      if (!savedFlight.getUser().getId().equals(userOpt.get().getId())) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body("You don't have permission to access this flight");
      }

      // Convert to DTO
      SavedFlightDTO flightDTO = convertToDTO(savedFlight);

      return ResponseEntity.ok(flightDTO);
    } catch (Exception e) {
      logger.error("Error retrieving saved flight by ID", e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("An error occurred while retrieving the saved flight");
    }
  }

  /**
   * Deletes a specific saved flight by its ID.
   *
   * <p>
   * This endpoint removes a previously saved flight. It requires authentication
   * and performs an ownership check to ensure users can only delete their own
   * saved flights.
   *
   * @param id the saved flight ID to delete
   * @return ResponseEntity with no content on success or error information
   */
  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteSavedFlight(@PathVariable Long id) {
    try {
      // Get the authenticated user
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      String username = authentication.getName();

      Optional<User> userOpt = userService.getUserByUsername(username);
      if (!userOpt.isPresent()) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body("User not authenticated or not found");
      }

      // Get the saved flight
      Optional<SavedFlight> savedFlightOpt = savedFlightService.getSavedFlightById(id);

      if (!savedFlightOpt.isPresent()) {
        return ResponseEntity.notFound().build();
      }

      SavedFlight savedFlight = savedFlightOpt.get();

      // Security check - ensure the flight belongs to the authenticated user
      if (!savedFlight.getUser().getId().equals(userOpt.get().getId())) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body("You don't have permission to delete this flight");
      }

      // Delete the flight
      savedFlightService.deleteSavedFlightById(id);

      return ResponseEntity.noContent().build();
    } catch (Exception e) {
      logger.error("Error deleting saved flight", e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("An error occurred while deleting the saved flight");
    }
  }

  /**
   * Deletes all saved flights for the authenticated user.
   *
   * <p>
   * This endpoint removes all saved flights associated with the current user.
   * It requires authentication and only affects flights owned by the current
   * user.
   *
   * @return ResponseEntity with no content on success or error information
   */
  @DeleteMapping
  public ResponseEntity<?> deleteAllSavedFlights() {
    try {
      // Get the authenticated user
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      String username = authentication.getName();

      Optional<User> userOpt = userService.getUserByUsername(username);
      if (!userOpt.isPresent()) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body("User not authenticated or not found");
      }

      User user = userOpt.get();

      // Delete all saved flights for the user
      savedFlightService.deleteSavedFlightsByUser(user);

      return ResponseEntity.noContent().build();
    } catch (Exception e) {
      logger.error("Error deleting all saved flights", e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("An error occurred while deleting saved flights");
    }
  }

  /**
   * Converts a SavedFlight entity to a SavedFlightDTO.
   *
   * <p>
   * This private utility method transforms the internal entity representation
   * to a data transfer object suitable for API responses.
   *
   * @param savedFlight the entity to convert
   * @return the corresponding DTO with flight details
   */
  private SavedFlightDTO convertToDTO(SavedFlight savedFlight) {
    SavedFlightDTO dto = new SavedFlightDTO();
    dto.setId(savedFlight.getId());
    dto.setAirlineCode(savedFlight.getAirlineCode());
    dto.setAirlineName(savedFlight.getAirlineName());
    dto.setFlightNumber(savedFlight.getFlightNumber());
    dto.setOrigin(savedFlight.getOrigin());
    dto.setDestination(savedFlight.getDestination());
    dto.setDepartureTime(savedFlight.getDepartureTime());
    dto.setArrivalTime(savedFlight.getArrivalTime());
    dto.setPrice(savedFlight.getPrice());
    dto.setCurrency(savedFlight.getCurrency());
    return dto;
  }
}