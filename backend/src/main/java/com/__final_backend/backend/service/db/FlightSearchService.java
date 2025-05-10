package com.__final_backend.backend.service.db;

import com.__final_backend.backend.entity.FlightSearch;
import com.__final_backend.backend.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Service interface for FlightSearch entity operations
 */
public interface FlightSearchService {

  /**
   * Save a flight search
   * 
   * @param flightSearch the flight search to save
   * @return the saved flight search
   */
  FlightSearch saveFlightSearch(FlightSearch flightSearch);

  /**
   * Find a flight search by ID
   * 
   * @param id the flight search ID
   * @return an Optional containing the flight search if found
   */
  Optional<FlightSearch> getFlightSearchById(Long id);

  /**
   * Get all flight searches for a specific user
   * 
   * @param user the user whose searches to find
   * @return a list of flight searches
   */
  List<FlightSearch> getFlightSearchesByUser(User user);

  /**
   * Get all flight searches for a specific user with pagination
   * 
   * @param user     the user whose searches to find
   * @param pageable pagination information
   * @return a page of flight searches
   */
  Page<FlightSearch> getFlightSearchesByUser(User user, Pageable pageable);

  /**
   * Get flight searches by origin and destination
   * 
   * @param origin      the origin airport code
   * @param destination the destination airport code
   * @return a list of flight searches
   */
  List<FlightSearch> getFlightSearchesByOriginAndDestination(String origin, String destination);

  /**
   * Get flight searches by departure date range
   * 
   * @param startDate the start of the date range
   * @param endDate   the end of the date range
   * @return a list of flight searches
   */
  List<FlightSearch> getFlightSearchesByDepartureDateBetween(LocalDate startDate, LocalDate endDate);

  /**
   * Get flight searches by user, origin, and destination
   * 
   * @param user        the user whose searches to find
   * @param origin      the origin airport code
   * @param destination the destination airport code
   * @return a list of flight searches
   */
  List<FlightSearch> getFlightSearchesByUserAndOriginAndDestination(User user, String origin, String destination);

  /**
   * Delete a flight search by ID
   * 
   * @param id the ID of the flight search to delete
   */
  void deleteFlightSearchById(Long id);

  /**
   * Delete all flight searches for a user
   * 
   * @param user the user whose searches to delete
   */
  void deleteFlightSearchesByUser(User user);
}