package com.__final_backend.backend.service.db;

import com.__final_backend.backend.entity.FlightSearch;
import com.__final_backend.backend.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Service interface for managing FlightSearch entity operations.
 * <p>
 * This service defines the contract for saving, retrieving, and deleting flight
 * search history.
 * It provides methods to access flight searches by various criteria including
 * user, route,
 * and date range. The interface includes both standard retrieval methods and
 * pagination
 * support for efficient data access.
 * <p>
 * Implementations of this interface should handle transactional boundaries and
 * ensure
 * data consistency when performing operations.
 */
public interface FlightSearchService {
  /**
   * Saves a flight search to the user's search history.
   * <p>
   * This method persists the provided flight search information to the database,
   * associating
   * it with the user specified in the FlightSearch entity. If the entity has an
   * ID,
   * this will update the existing record; otherwise, a new record is created.
   * 
   * @param flightSearch the flight search entity to save, must not be null and
   *                     must have a valid user
   * @return the saved flight search with populated ID and metadata
   * @throws IllegalArgumentException if the flightSearch is null or has invalid
   *                                  data
   */
  FlightSearch saveFlightSearch(FlightSearch flightSearch);

  /**
   * Retrieves a flight search by its unique identifier.
   * <p>
   * This method attempts to find a flight search with the specified ID. If no
   * record
   * exists with the given ID, an empty Optional is returned instead of null.
   * 
   * @param id the unique identifier of the flight search to retrieve, must not be
   *           null
   * @return an Optional containing the flight search if found, or an empty
   *         Optional if not found
   * @throws IllegalArgumentException if id is null
   */
  Optional<FlightSearch> getFlightSearchById(Long id);

  /**
   * Retrieves all flight searches performed by a specific user.
   * <p>
   * This method returns all matching records without pagination. For users with
   * many
   * flight searches, consider using the paginated version
   * {@link #getFlightSearchesByUser(User, Pageable)}
   * instead to improve performance and reduce memory usage.
   * 
   * @param user the user whose flight searches to find, must not be null
   * @return a list of all flight searches for the user; empty list if none found
   * @throws IllegalArgumentException if user is null
   * @see #getFlightSearchesByUser(User, Pageable)
   */
  List<FlightSearch> getFlightSearchesByUser(User user);

  /**
   * Retrieves flight searches performed by a specific user with pagination
   * support.
   * <p>
   * This method is recommended for users with many flight searches as it improves
   * performance
   * and reduces memory consumption. The pageable parameter allows controlling
   * page size,
   * page number, and sorting options.
   * 
   * @param user     the user whose flight searches to find, must not be null
   * @param pageable pagination information including page number, size, and
   *                 sorting, must not be null
   * @return a page of flight searches for the user
   * @throws IllegalArgumentException if user or pageable is null
   */
  Page<FlightSearch> getFlightSearchesByUser(User user, Pageable pageable);

  /**
   * Retrieves flight searches by origin and destination airports.
   * <p>
   * This method returns searches for a specific route regardless of the user who
   * performed
   * the search or when it was performed. It can be useful for analytics on
   * popular routes
   * or for providing recommendations.
   * 
   * @param origin      the origin airport code, must not be null or empty
   * @param destination the destination airport code, must not be null or empty
   * @return a list of flight searches matching the specified route; empty list if
   *         none found
   * @throws IllegalArgumentException if origin or destination is null or empty
   */
  List<FlightSearch> getFlightSearchesByOriginAndDestination(String origin, String destination);

  /**
   * Retrieves flight searches within a specified departure date range.
   * <p>
   * This method allows for temporal analysis of search patterns, finding searches
   * planned for specific travel periods, or determining peak travel seasons based
   * on user interest. Both start and end dates are inclusive in the search.
   * 
   * @param startDate the start of the date range (inclusive), must not be null
   * @param endDate   the end of the date range (inclusive), must not be null
   * @return a list of flight searches within the specified date range; empty list
   *         if none found
   * @throws IllegalArgumentException if startDate or endDate is null
   * @throws IllegalArgumentException if startDate is after endDate
   */
  List<FlightSearch> getFlightSearchesByDepartureDateBetween(LocalDate startDate, LocalDate endDate);

  /**
   * Retrieves flight searches by user, origin, and destination.
   * <p>
   * This method combines multiple search criteria to provide targeted results for
   * specific
   * user route preferences. It can be useful for understanding a user's interest
   * in
   * particular routes or for providing personalized recommendations.
   * 
   * @param user        the user whose searches to find, must not be null
   * @param origin      the origin airport code, must not be null or empty
   * @param destination the destination airport code, must not be null or empty
   * @return a list of flight searches matching all specified criteria; empty list
   *         if none found
   * @throws IllegalArgumentException if user is null or if origin or destination
   *                                  is null or empty
   */
  List<FlightSearch> getFlightSearchesByUserAndOriginAndDestination(User user, String origin, String destination);

  /**
   * Deletes a flight search by its unique identifier.
   * <p>
   * This method removes a flight search with the specified ID from the database.
   * If no search exists with the given ID, the implementation may choose to
   * either throw an exception or silently ignore the request.
   * 
   * @param id the unique identifier of the flight search to delete, must not be
   *           null
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
  void deleteFlightSearchById(Long id);

  /**
   * Deletes all flight searches associated with a specific user.
   * <p>
   * This method is useful when a user wants to clear their search history
   * or when a user account is being removed from the system. Implementations
   * should perform a bulk delete operation when possible for efficiency.
   * 
   * @param user the user whose flight searches to delete, must not be null
   * @throws IllegalArgumentException if user is null
   */
  void deleteFlightSearchesByUser(User user);
}