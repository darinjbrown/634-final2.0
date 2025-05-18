package com.__final_backend.backend.service.db;

import com.__final_backend.backend.entity.SavedFlight;
import com.__final_backend.backend.entity.User;
import com.__final_backend.backend.repository.SavedFlightRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of the SavedFlightService interface for managing saved flight
 * operations.
 * <p>
 * This service provides functionality for users to save, retrieve, and manage
 * flight
 * information they're interested in. It handles the persistence of saved flight
 * data
 * and offers both standard and paginated methods for retrieving flights by
 * user.
 * <p>
 * All operations are performed within transactions to ensure data consistency.
 */
@Service
@Transactional
public class SavedFlightServiceImpl implements SavedFlightService {
  /** Repository for database operations on SavedFlight entities. */
  private final SavedFlightRepository savedFlightRepository;

  /**
   * Constructs a new SavedFlightServiceImpl with the specified repository.
   * <p>
   * Spring automatically injects the appropriate SavedFlightRepository
   * implementation.
   * The @Autowired annotation is optional for constructor injection since Spring
   * 4.3.
   *
   * @param savedFlightRepository the JPA repository for SavedFlight entities
   */
  public SavedFlightServiceImpl(SavedFlightRepository savedFlightRepository) {
    this.savedFlightRepository = savedFlightRepository;
  }

  /**
   * {@inheritDoc}
   * <p>
   * Persists the saved flight entity to the database. If the entity has an ID,
   * this will update the existing record; otherwise, a new record is created.
   * 
   * @param savedFlight the flight to save
   * @return the saved flight with populated ID and metadata
   */
  @Override
  public SavedFlight saveFlight(SavedFlight savedFlight) {
    return savedFlightRepository.save(savedFlight);
  }

  /**
   * {@inheritDoc}
   * <p>
   * Retrieves a saved flight by its unique identifier. This operation is
   * transactional and guarantees consistent data retrieval.
   * 
   * @param id the saved flight ID
   * @return an Optional containing the saved flight if found, or empty if not
   *         found
   */
  @Override
  public Optional<SavedFlight> getSavedFlightById(Long id) {
    return savedFlightRepository.findById(id);
  }

  /**
   * {@inheritDoc}
   * <p>
   * Retrieves all flights saved by a specific user. This method returns all
   * matching
   * records without pagination, which may impact performance if a user has many
   * saved flights.
   * 
   * @param user the user whose saved flights to find
   * @return a list of all saved flights for the user
   */
  @Override
  public List<SavedFlight> getSavedFlightsByUser(User user) {
    return savedFlightRepository.findByUser(user);
  }

  /**
   * {@inheritDoc}
   * <p>
   * Retrieves flights saved by a specific user with pagination support. This
   * method
   * is recommended for users with many saved flights as it improves performance
   * and
   * reduces memory consumption.
   * 
   * @param user     the user whose saved flights to find
   * @param pageable pagination information including page number, size, and
   *                 sorting
   * @return a page of saved flights for the user
   */
  @Override
  public Page<SavedFlight> getSavedFlightsByUser(User user, Pageable pageable) {
    return savedFlightRepository.findByUser(user, pageable);
  }

  /**
   * {@inheritDoc}
   * <p>
   * Deletes a saved flight by its unique identifier. If no flight exists with the
   * given ID, a EmptyResultDataAccessException may be thrown by the underlying
   * repository.
   * <p>
   * This operation is performed within a transaction to ensure database
   * consistency.
   * 
   * @param id the ID of the flight to delete
   */
  @Override
  public void deleteSavedFlightById(Long id) {
    savedFlightRepository.deleteById(id);
  }

  /**
   * {@inheritDoc}
   * <p>
   * Deletes all saved flights associated with a specific user. This is useful
   * when a user wants to clear their saved flight history or when a user account
   * is being removed from the system.
   * <p>
   * This method performs a bulk delete operation which is more efficient than
   * deleting flights individually.
   * 
   * @param user the user whose saved flights to delete
   */
  @Override
  public void deleteSavedFlightsByUser(User user) {
    savedFlightRepository.deleteByUser(user);
  }
}