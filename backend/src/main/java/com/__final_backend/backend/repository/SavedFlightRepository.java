package com.__final_backend.backend.repository;

import com.__final_backend.backend.entity.SavedFlight;
import com.__final_backend.backend.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for SavedFlight entity operations.
 * <p>
 * Provides methods for managing saved flights in the database, including CRUD
 * operations
 * inherited from JpaRepository and custom query methods for retrieving saved
 * flights
 * based on various criteria such as user, origin/destination, airline, and date
 * ranges.
 * <p>
 * The SavedFlight entity represents flight options that users have saved for
 * future reference.
 */
@Repository
public interface SavedFlightRepository extends JpaRepository<SavedFlight, Long> {
  /**
   * Finds all saved flights for a specific user.
   * <p>
   * This method retrieves all flights saved by a particular user, ordered by the
   * default
   * repository ordering (typically by ID). Use this method when pagination is not
   * required.
   *
   * @param user the user whose saved flights to find
   * @return a list of all saved flights belonging to the specified user, which
   *         may be empty if
   *         the user has no saved flights
   */
  List<SavedFlight> findByUser(User user);

  /**
   * Finds all saved flights for a specific user with pagination support.
   * <p>
   * This method retrieves saved flights for a user with support for pagination,
   * sorting,
   * and filtering. It's particularly useful for displaying saved flights in a
   * paginated view
   * or when dealing with users who have many saved flights.
   *
   * @param user     the user whose saved flights to find
   * @param pageable pagination and sorting information (page number, page size,
   *                 sort criteria)
   * @return a page of saved flights belonging to the specified user, which may be
   *         empty if
   *         the user has no saved flights or the pageable parameters go beyond
   *         available results
   */
  Page<SavedFlight> findByUser(User user, Pageable pageable);

  /**
   * Finds saved flights by origin and destination airports.
   * <p>
   * This method retrieves all saved flights matching a specific route
   * (origin-destination pair),
   * regardless of which users saved them. Useful for route analysis or for
   * finding popular
   * routes across all users.
   *
   * @param origin      the origin airport code (3-letter IATA code, e.g., "JFK")
   * @param destination the destination airport code (3-letter IATA code, e.g.,
   *                    "LAX")
   * @return a list of saved flights matching the specified route, which may be
   *         empty if
   *         no saved flights match the criteria
   */
  List<SavedFlight> findByOriginAndDestination(String origin, String destination);

  /**
   * Finds saved flights by airline code.
   * <p>
   * This method retrieves all saved flights operated by a specific airline,
   * regardless of
   * which users saved them. Useful for airline-specific analysis or for finding
   * popular
   * airlines across all users.
   *
   * @param airlineCode the airline's IATA code (3-letter code, e.g., "AAL" for
   *                    American Airlines)
   * @return a list of saved flights operated by the specified airline, which may
   *         be empty if
   *         no saved flights match the criteria
   */
  List<SavedFlight> findByAirlineCode(String airlineCode);

  /**
   * Finds saved flights by departure time range.
   * <p>
   * This method retrieves all saved flights scheduled to depart within a specific
   * time window,
   * regardless of which users saved them. Useful for analyzing popular travel
   * periods or
   * for finding flights during specific timeframes.
   *
   * @param startTime the start of the time range (inclusive)
   * @param endTime   the end of the time range (inclusive)
   * @return a list of saved flights with departure times within the specified
   *         range, which may
   *         be empty if no saved flights match the criteria
   */
  List<SavedFlight> findByDepartureTimeBetween(LocalDateTime startTime, LocalDateTime endTime);

  /**
   * Finds saved flights by user, origin, and destination.
   * <p>
   * This method combines user-specific filtering with route-specific filtering,
   * retrieving
   * all flights saved by a particular user for a specific route
   * (origin-destination pair).
   * Useful for showing a user's saved options for a particular journey.
   *
   * @param user        the user whose saved flights to find
   * @param origin      the origin airport code (3-letter IATA code, e.g., "JFK")
   * @param destination the destination airport code (3-letter IATA code, e.g.,
   *                    "LAX")
   * @return a list of saved flights matching the specified user and route, which
   *         may be empty
   *         if no saved flights match the criteria
   */
  List<SavedFlight> findByUserAndOriginAndDestination(User user, String origin, String destination);

  /**
   * Deletes all saved flights for a specific user.
   * <p>
   * This method removes all flights saved by the specified user from the
   * database.
   * Useful for when a user requests to remove all their saved flights at once, or
   * as
   * part of a user account deletion process to ensure data cleanup.
   * <p>
   * This operation is transactional and will either delete all matching records
   * or none.
   *
   * @param user the user whose saved flights to delete
   */
  void deleteByUser(User user);
}