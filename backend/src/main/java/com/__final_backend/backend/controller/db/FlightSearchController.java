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
 * REST controller for flight search history operations
 */
@RestController
@RequestMapping("/api/flight-searches")
public class FlightSearchController {

  private final FlightSearchService flightSearchService;
  private final UserService userService;

  @Autowired
  public FlightSearchController(FlightSearchService flightSearchService, UserService userService) {
    this.flightSearchService = flightSearchService;
    this.userService = userService;
  }

  @PostMapping
  public ResponseEntity<FlightSearch> saveFlightSearch(@RequestBody FlightSearch flightSearch) {
    FlightSearch savedSearch = flightSearchService.saveFlightSearch(flightSearch);
    return new ResponseEntity<>(savedSearch, HttpStatus.CREATED);
  }

  @GetMapping("/{id}")
  public ResponseEntity<FlightSearch> getFlightSearchById(@PathVariable Long id) {
    Optional<FlightSearch> flightSearch = flightSearchService.getFlightSearchById(id);
    return flightSearch.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
        .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  @GetMapping("/user/{userId}")
  public ResponseEntity<List<FlightSearch>> getFlightSearchesByUser(@PathVariable Long userId) {
    Optional<User> userOptional = userService.getUserById(userId);
    if (!userOptional.isPresent()) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    List<FlightSearch> searches = flightSearchService.getFlightSearchesByUser(userOptional.get());
    return new ResponseEntity<>(searches, HttpStatus.OK);
  }

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

  @GetMapping("/route")
  public ResponseEntity<List<FlightSearch>> getFlightSearchesByRoute(
      @RequestParam String origin,
      @RequestParam String destination) {
    List<FlightSearch> searches = flightSearchService.getFlightSearchesByOriginAndDestination(origin, destination);
    return new ResponseEntity<>(searches, HttpStatus.OK);
  }

  @GetMapping("/date-range")
  public ResponseEntity<List<FlightSearch>> getFlightSearchesByDateRange(
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
    List<FlightSearch> searches = flightSearchService.getFlightSearchesByDepartureDateBetween(startDate, endDate);
    return new ResponseEntity<>(searches, HttpStatus.OK);
  }

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

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteFlightSearch(@PathVariable Long id) {
    if (!flightSearchService.getFlightSearchById(id).isPresent()) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    flightSearchService.deleteFlightSearchById(id);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

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