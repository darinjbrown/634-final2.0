package com.__final_backend.backend.service.db;

import com.__final_backend.backend.entity.FlightSearch;
import com.__final_backend.backend.entity.User;
import com.__final_backend.backend.repository.FlightSearchRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of the FlightSearchService interface for managing flight
 * search operations.
 * <p>
 * This service provides functionality for saving, retrieving, and managing
 * users' flight search history.
 * It supports searching by various criteria including user, origin/destination,
 * and date ranges.
 * <p>
 * All operations are performed within transactions to ensure data consistency.
 */
@Service
@Transactional
public class FlightSearchServiceImpl implements FlightSearchService {
  /** Repository for database operations on FlightSearch entities. */
  private final FlightSearchRepository flightSearchRepository;

  /**
   * Constructs a new FlightSearchServiceImpl with the specified repository.
   * <p>
   * Spring automatically injects the appropriate FlightSearchRepository
   * implementation.
   * The @Autowired annotation is optional for constructor injection since Spring
   * 4.3.
   *
   * @param flightSearchRepository the JPA repository for FlightSearch entities
   */
  public FlightSearchServiceImpl(FlightSearchRepository flightSearchRepository) {
    this.flightSearchRepository = flightSearchRepository;
  }

  /**
   * {@inheritDoc}
   * <p>
   * Persists the flight search entity to the database. If the entity has an ID,
   * this will update the existing record; otherwise, a new record is created.
   * 
   * @param flightSearch the flight search to save
   * @return the saved flight search with populated ID and metadata
   */
  @Override
  public FlightSearch saveFlightSearch(FlightSearch flightSearch) {
    return flightSearchRepository.save(flightSearch);
  }

  /**
   * {@inheritDoc}
   * <p>
   * Retrieves a flight search by its unique identifier. This operation is
   * transactional and guarantees consistent data retrieval.
   * 
   * @param id the flight search ID
   * @return an Optional containing the flight search if found, or empty if not
   *         found
   */
  @Override
  public Optional<FlightSearch> getFlightSearchById(Long id) {
    return flightSearchRepository.findById(id);
  }

  /**
   * {@inheritDoc}
   * <p>
   * Retrieves all flight searches performed by a specific user. This method
   * returns all matching
   * records without pagination, which may impact performance if a user has many
   * flight searches.
   * 
   * @param user the user whose flight searches to find
   * @return a list of all flight searches for the user
   */
  @Override
  public List<FlightSearch> getFlightSearchesByUser(User user) {
    return flightSearchRepository.findByUser(user);
  }

  /**
   * {@inheritDoc}
   * <p>
   * Retrieves flight searches performed by a specific user with pagination
   * support. This method
   * is recommended for users with many flight searches as it improves performance
   * and
   * reduces memory consumption.
   * 
   * @param user     the user whose flight searches to find
   * @param pageable pagination information including page number, size, and
   *                 sorting
   * @return a page of flight searches for the user
   */
  @Override
  public Page<FlightSearch> getFlightSearchesByUser(User user, Pageable pageable) {
    return flightSearchRepository.findByUser(user, pageable);
  }

  /**
   * {@inheritDoc}
   * <p>
   * Retrieves flight searches by origin and destination airports. This method is
   * useful
   * for analyzing popular routes across all users.
   * 
   * @param origin      the origin airport code
   * @param destination the destination airport code
   * @return a list of flight searches matching the specified route
   */
  @Override
  public List<FlightSearch> getFlightSearchesByOriginAndDestination(String origin, String destination) {
    return flightSearchRepository.findByOriginAndDestination(origin, destination);
  }

  /**
   * {@inheritDoc}
   * <p>
   * Retrieves flight searches within a specified departure date range. This
   * method allows
   * for temporal analysis of search patterns.
   * 
   * @param startDate the start of the date range (inclusive)
   * @param endDate   the end of the date range (inclusive)
   * @return a list of flight searches within the specified date range
   */
  @Override
  public List<FlightSearch> getFlightSearchesByDepartureDateBetween(LocalDate startDate, LocalDate endDate) {
    return flightSearchRepository.findByDepartureDateBetween(startDate, endDate);
  }

  /**
   * {@inheritDoc}
   * <p>
   * Retrieves flight searches by user, origin, and destination. This method
   * combines
   * multiple search criteria to provide targeted results for specific user route
   * preferences.
   * 
   * @param user        the user whose searches to find
   * @param origin      the origin airport code
   * @param destination the destination airport code
   * @return a list of flight searches matching all specified criteria
   */
  @Override
  public List<FlightSearch> getFlightSearchesByUserAndOriginAndDestination(User user, String origin,
      String destination) {
    return flightSearchRepository.findByUserAndOriginAndDestination(user, origin, destination);
  }

  /**
   * {@inheritDoc}
   * <p>
   * Deletes a flight search by its unique identifier. If no search exists with
   * the
   * given ID, a EmptyResultDataAccessException may be thrown by the underlying
   * repository.
   * <p>
   * This operation is performed within a transaction to ensure database
   * consistency.
   * 
   * @param id the ID of the flight search to delete
   */
  @Override
  public void deleteFlightSearchById(Long id) {
    flightSearchRepository.deleteById(id);
  }

  /**
   * {@inheritDoc}
   * <p>
   * Deletes all flight searches associated with a specific user. This is useful
   * when a user wants to clear their search history or when a user account
   * is being removed from the system.
   * <p>
   * This method first retrieves all searches for the user and then performs a
   * bulk delete
   * operation which is more efficient than deleting searches individually.
   * 
   * @param user the user whose flight searches to delete
   */
  @Override
  public void deleteFlightSearchesByUser(User user) {
    List<FlightSearch> searches = flightSearchRepository.findByUser(user);
    flightSearchRepository.deleteAll(searches);
  }
}