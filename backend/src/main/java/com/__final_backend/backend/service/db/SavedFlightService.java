package com.__final_backend.backend.service.db;

import com.__final_backend.backend.entity.SavedFlight;
import com.__final_backend.backend.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for managing SavedFlight entity operations.
 * <p>
 * This service defines the contract for saving, retrieving, and deleting flight
 * information
 * that users have saved to their accounts. It provides both standard retrieval
 * methods and
 * pagination support for efficient data access.
 * <p>
 * Implementations of this interface should handle transactional boundaries and
 * ensure
 * data consistency when performing operations.
 */
public interface SavedFlightService {
  /**
   * Saves a flight to the user's saved flights collection.
   * <p>
   * This method persists the provided flight information to the database,
   * associating
   * it with the user specified in the SavedFlight entity. If the entity has an
   * ID,
   * this will update the existing record; otherwise, a new record is created.
   * 
   * @param savedFlight the flight entity to save, must not be null and must have
   *                    a valid user
   * @return the saved flight with populated ID and metadata
   * @throws IllegalArgumentException if the savedFlight is null or has invalid
   *                                  data
   */
  SavedFlight saveFlight(SavedFlight savedFlight);

  /**
   * Retrieves a saved flight by its unique identifier.
   * <p>
   * This method attempts to find a saved flight with the specified ID. If no
   * record
   * exists with the given ID, an empty Optional is returned instead of null.
   * 
   * @param id the unique identifier of the saved flight to retrieve, must not be
   *           null
   * @return an Optional containing the saved flight if found, or an empty
   *         Optional if not found
   * @throws IllegalArgumentException if id is null
   */
  Optional<SavedFlight> getSavedFlightById(Long id);

  /**
   * Retrieves all flights saved by a specific user.
   * <p>
   * This method returns all matching records without pagination. For users with
   * many
   * saved flights, consider using the paginated version
   * {@link #getSavedFlightsByUser(User, Pageable)}
   * instead to improve performance.
   * 
   * @param user the user whose saved flights to find, must not be null
   * @return a list of all saved flights for the user; empty list if none found
   * @throws IllegalArgumentException if user is null
   * @see #getSavedFlightsByUser(User, Pageable)
   */
  List<SavedFlight> getSavedFlightsByUser(User user);

  /**
   * Retrieves flights saved by a specific user with pagination support.
   * <p>
   * This method is recommended for users with many saved flights as it improves
   * performance
   * and reduces memory consumption. The pageable parameter allows controlling
   * page size,
   * page number, and sorting options.
   * 
   * @param user     the user whose saved flights to find, must not be null
   * @param pageable pagination information including page number, size, and
   *                 sorting, must not be null
   * @return a page of saved flights for the user
   * @throws IllegalArgumentException if user or pageable is null
   */
  Page<SavedFlight> getSavedFlightsByUser(User user, Pageable pageable);

  /**
   * Deletes a saved flight by its unique identifier.
   * <p>
   * This method removes a saved flight with the specified ID from the database.
   * If no flight exists with the given ID, the implementation may choose to
   * either throw an exception or silently ignore the request.
   * 
   * @param id the unique identifier of the flight to delete, must not be null
   * @throws IllegalArgumentException                               if id is null
   * @throws org.springframework.dao.EmptyResultDataAccessException if a record
   *                                                                with the
   *                                                                specified ID
   *                                                                does not exist
   *                                                                and the
   *                                                                implementation
   *                                                                doesn't
   *                                                                tolerate this
   */
  void deleteSavedFlightById(Long id);

  /**
   * Deletes all saved flights associated with a specific user.
   * <p>
   * This method is useful when a user wants to clear their saved flight history
   * or when a user account is being removed from the system. It performs a bulk
   * delete operation which is more efficient than deleting flights individually.
   * 
   * @param user the user whose saved flights to delete, must not be null
   * @throws IllegalArgumentException if user is null
   */
  void deleteSavedFlightsByUser(User user);
}