package com.__final_backend.backend.controller.db;

import com.__final_backend.backend.dto.SavedFlightDTO;
import com.__final_backend.backend.entity.SavedFlight;
import com.__final_backend.backend.entity.User;
import com.__final_backend.backend.service.db.SavedFlightService;
import com.__final_backend.backend.service.db.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
 * REST controller for saved flight operations
 */
@RestController
@RequestMapping("/api/saved-flights")
public class SavedFlightController {

  private static final Logger logger = LoggerFactory.getLogger(SavedFlightController.class);

  private final SavedFlightService savedFlightService;
  private final UserService userService;

  @Autowired
  public SavedFlightController(SavedFlightService savedFlightService, UserService userService) {
    this.savedFlightService = savedFlightService;
    this.userService = userService;
  }

  /**
   * Save a flight for the authenticated user
   * 
   * @param flightDTO the flight data to save
   * @return the saved flight data
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
   * Get all saved flights for the authenticated user
   * 
   * @return list of saved flights
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
   * Get saved flights for the authenticated user with pagination
   * 
   * @param page page number
   * @param size page size
   * @return paged list of saved flights
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
   * Get a specific saved flight by ID
   * 
   * @param id the flight ID
   * @return the saved flight
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
   * Delete a saved flight
   * 
   * @param id the flight ID to delete
   * @return response with no content
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
   * Delete all saved flights for the authenticated user
   * 
   * @return response with no content
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
   * Convert SavedFlight entity to SavedFlightDTO
   * 
   * @param savedFlight the entity to convert
   * @return the DTO
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