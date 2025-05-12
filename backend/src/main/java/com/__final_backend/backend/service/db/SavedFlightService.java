package com.__final_backend.backend.service.db;

import com.__final_backend.backend.entity.SavedFlight;
import com.__final_backend.backend.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service interface for SavedFlight entity operations
 */
public interface SavedFlightService {

  /**
   * Save a flight
   * 
   * @param savedFlight the flight to save
   * @return the saved flight
   */
  SavedFlight saveFlight(SavedFlight savedFlight);

  /**
   * Find a saved flight by ID
   * 
   * @param id the saved flight ID
   * @return an Optional containing the saved flight if found
   */
  Optional<SavedFlight> getSavedFlightById(Long id);

  /**
   * Get all saved flights for a specific user
   * 
   * @param user the user whose saved flights to find
   * @return a list of saved flights
   */
  List<SavedFlight> getSavedFlightsByUser(User user);

  /**
   * Get all saved flights for a specific user with pagination
   * 
   * @param user     the user whose saved flights to find
   * @param pageable pagination information
   * @return a page of saved flights
   */
  Page<SavedFlight> getSavedFlightsByUser(User user, Pageable pageable);

  /**
   * Delete a saved flight by ID
   * 
   * @param id the ID of the flight to delete
   */
  void deleteSavedFlightById(Long id);

  /**
   * Delete all saved flights for a specific user
   * 
   * @param user the user whose saved flights to delete
   */
  void deleteSavedFlightsByUser(User user);
}