package com.__final_backend.backend.controller.db;

import com.__final_backend.backend.entity.FlightSearch;
import com.__final_backend.backend.entity.User;
import com.__final_backend.backend.service.db.FlightSearchService;
import com.__final_backend.backend.service.db.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for flight search history operations.
 * 
 * <p>
 * This controller provides endpoints for storing, retrieving, and managing
 * user flight search history. It supports operations for individual searches as
 * well as
 * search history for specific users, routes, and date ranges.
 */
@RestController
@RequestMapping("/api/flight-searches")
public class FlightSearchController {

  private final FlightSearchService flightSearchService;
  private final UserService userService;

  public FlightSearchController(FlightSearchService flightSearchService, UserService userService) {
    this.flightSearchService = flightSearchService;
    this.userService = userService;
  }

  /**
   * Saves a new flight search record.
   * <p>
   * This endpoint allows creating a new flight search history entry in the
   * database.
   * </p>
   *
   * @param flightSearch The flight search details to be saved
   * @return The created flight search entity with HTTP status 201 (Created)
   */
  @PostMapping
  public ResponseEntity<FlightSearch> saveFlightSearch(@RequestBody FlightSearch flightSearch) {
    FlightSearch savedSearch = flightSearchService.saveFlightSearch(flightSearch);
    return new ResponseEntity<>(savedSearch, HttpStatus.CREATED);
  }

  /**
   * Retrieves a flight search record by its ID.
   * <p>
   * This endpoint fetches a specific flight search history entry identified by
   * its unique ID.
   * </p>
   *
   * @param id The unique identifier of the flight search
   * @return The flight search entity with HTTP status 200 (OK) if found,
   *         or HTTP status 404 (Not Found) if not found
   */
  @GetMapping("/{id}")
  public ResponseEntity<FlightSearch> getFlightSearchById(@PathVariable Long id) {
    Optional<FlightSearch> flightSearch = flightSearchService.getFlightSearchById(id);
    return flightSearch.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
        .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  /**
   * Retrieves all flight searches made by a specific user.
   * <p>
   * This endpoint returns a list of all flight search history entries associated
   * with
   * the specified user ID.
   * </p>
   *
   * @param userId The unique identifier of the user
   * @return A list of flight searches with HTTP status 200 (OK) if the user
   *         exists,
   *         or HTTP status 404 (Not Found) if the user doesn't exist
   */
  @GetMapping("/user/{userId}")
  public ResponseEntity<List<FlightSearch>> getFlightSearchesByUser(@PathVariable Long userId) {
    Optional<User> userOptional = userService.getUserById(userId);
    if (!userOptional.isPresent()) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    List<FlightSearch> searches = flightSearchService.getFlightSearchesByUser(userOptional.get());
    return new ResponseEntity<>(searches, HttpStatus.OK);
  }

  /**
   * Retrieves a paginated list of flight searches made by a specific user.
   * <p>
   * This endpoint returns a page of flight search history entries associated with
   * the specified user ID, with pagination support.
   * </p>
   *
   * @param userId The unique identifier of the user
   * @param page   The page number (zero-based) to retrieve
   * @param size   The size of each page
   * @return A page of flight searches with HTTP status 200 (OK) if the user
   *         exists,
   *         or HTTP status 404 (Not Found) if the user doesn't exist
   */
  @GetMapping("/user/{userId}/paged")
  public ResponseEntity<Page<FlightSearch>> getFlightSearchesByUserPaged(
      @PathVariable Long userId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    Optional<User> userOptional = userService.getUserById(userId);
    if (!userOptional.isPresent()) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    Page<FlightSearch> searches = flightSearchService.getFlightSearchesByUser(
        userOptional.get(), PageRequest.of(page, size));
    return new ResponseEntity<>(searches, HttpStatus.OK);
  }

  /**
   * Retrieves flight searches by route (origin and destination).
   * <p>
   * This endpoint returns flight search history entries filtered by the specified
   * origin and destination airports.
   * </p>
   *
   * @param origin      The IATA code of the origin airport
   * @param destination The IATA code of the destination airport
   * @return A list of flight searches matching the route with HTTP status 200
   *         (OK)
   */
  @GetMapping("/route")
  public ResponseEntity<List<FlightSearch>> getFlightSearchesByRoute(
      @RequestParam String origin,
      @RequestParam String destination) {
    List<FlightSearch> searches = flightSearchService.getFlightSearchesByOriginAndDestination(origin, destination);
    return new ResponseEntity<>(searches, HttpStatus.OK);
  }

  /**
   * Retrieves flight searches within a specified date range.
   * <p>
   * This endpoint returns flight search history entries with departure dates
   * falling between the specified start and end dates.
   * </p>
   *
   * @param startDate The inclusive start date for the search range (format: ISO
   *                  date)
   * @param endDate   The inclusive end date for the search range (format: ISO
   *                  date)
   * @return A list of flight searches within the date range with HTTP status 200
   *         (OK)
   */
  @GetMapping("/date-range")
  public ResponseEntity<List<FlightSearch>> getFlightSearchesByDateRange(
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
    List<FlightSearch> searches = flightSearchService.getFlightSearchesByDepartureDateBetween(startDate, endDate);
    return new ResponseEntity<>(searches, HttpStatus.OK);
  }

  /**
   * Retrieves flight searches by a specific user and route.
   * <p>
   * This endpoint returns flight search history entries filtered by user ID and
   * the specified origin and destination airports.
   * </p>
   *
   * @param userId      The unique identifier of the user
   * @param origin      The IATA code of the origin airport
   * @param destination The IATA code of the destination airport
   * @return A list of flight searches matching the user and route with HTTP
   *         status 200 (OK),
   *         or HTTP status 404 (Not Found) if the user doesn't exist
   */
  @GetMapping("/user/{userId}/route")
  public ResponseEntity<List<FlightSearch>> getFlightSearchesByUserAndRoute(
      @PathVariable Long userId,
      @RequestParam String origin,
      @RequestParam String destination) {
    Optional<User> userOptional = userService.getUserById(userId);
    if (!userOptional.isPresent()) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    List<FlightSearch> searches = flightSearchService.getFlightSearchesByUserAndOriginAndDestination(
        userOptional.get(), origin, destination);
    return new ResponseEntity<>(searches, HttpStatus.OK);
  }

  /**
   * Deletes a flight search record by its ID.
   * <p>
   * This endpoint removes a specific flight search history entry identified by
   * its unique ID.
   * </p>
   *
   * @param id The unique identifier of the flight search to delete
   * @return HTTP status 204 (No Content) if successfully deleted,
   *         or HTTP status 404 (Not Found) if the record doesn't exist
   */
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteFlightSearch(@PathVariable Long id) {
    if (!flightSearchService.getFlightSearchById(id).isPresent()) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    flightSearchService.deleteFlightSearchById(id);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  /**
   * Deletes all flight searches made by a specific user.
   * <p>
   * This endpoint removes all flight search history entries associated with
   * the specified user ID.
   * </p>
   *
   * @param userId The unique identifier of the user whose searches should be
   *               deleted
   * @return HTTP status 204 (No Content) if successfully deleted,
   *         or HTTP status 404 (Not Found) if the user doesn't exist
   */
  @DeleteMapping("/user/{userId}")
  public ResponseEntity<Void> deleteFlightSearchesByUser(@PathVariable Long userId) {
    Optional<User> userOptional = userService.getUserById(userId);
    if (!userOptional.isPresent()) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    flightSearchService.deleteFlightSearchesByUser(userOptional.get());
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }
}