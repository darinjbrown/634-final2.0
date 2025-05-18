package com.__final_backend.backend.repository;

import com.__final_backend.backend.entity.FlightSearch;
import com.__final_backend.backend.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository interface for FlightSearch entity operations.
 * <p>
 * Provides methods for managing flight search history in the database,
 * including CRUD operations
 * inherited from JpaRepository and custom query methods for retrieving search
 * history based on
 * various criteria such as user, route, and dates.
 * <p>
 * The FlightSearch entity represents individual flight search operations
 * performed by users,
 * which can be analyzed to understand user search patterns and preferences.
 */
@Repository
public interface FlightSearchRepository extends JpaRepository<FlightSearch, Long> {
  /**
   * Finds all flight searches performed by a specific user.
   * <p>
   * This method retrieves the complete search history for a particular user,
   * ordered by the
   * default repository ordering (typically by ID or timestamp). Use this method
   * when pagination
   * is not required and you need the user's full search history.
   *
   * @param user the user whose search history to retrieve
   * @return a list of all flight searches performed by the specified user, which
   *         may be empty
   *         if the user has not performed any searches
   */
  List<FlightSearch> findByUser(User user);

  /**
   * Finds all flight searches performed by a specific user with pagination
   * support.
   * <p>
   * This method retrieves the search history for a user with support for
   * pagination, sorting,
   * and filtering. It's particularly useful for displaying search history in a
   * paginated view
   * or when dealing with users who have an extensive search history.
   *
   * @param user     the user whose search history to retrieve
   * @param pageable pagination and sorting information (page number, page size,
   *                 sort criteria)
   * @return a page of flight searches performed by the specified user, which may
   *         be empty if
   *         the user has not performed any searches or the pageable parameters go
   *         beyond
   *         available results
   */
  Page<FlightSearch> findByUser(User user, Pageable pageable);

  /**
   * Finds flight searches by origin and destination airports.
   * <p>
   * This method retrieves all flight searches for a specific route
   * (origin-destination pair),
   * regardless of which users performed them. Useful for route analytics or for
   * identifying
   * frequently searched routes across all users.
   *
   * @param origin      the origin airport code (3-letter IATA code, e.g., "JFK")
   * @param destination the destination airport code (3-letter IATA code, e.g.,
   *                    "LAX")
   * @return a list of flight searches matching the specified route, which may be
   *         empty if
   *         no searches match the criteria
   */
  List<FlightSearch> findByOriginAndDestination(String origin, String destination);

  /**
   * Finds flight searches by departure date range.
   * <p>
   * This method retrieves all flight searches where the requested departure date
   * falls within
   * a specific date range, regardless of which users performed them. Useful for
   * analyzing
   * search patterns for specific travel periods or seasons.
   *
   * @param startDate the start of the date range (inclusive)
   * @param endDate   the end of the date range (inclusive)
   * @return a list of flight searches with departure dates within the specified
   *         range,
   *         which may be empty if no searches match the criteria
   */
  List<FlightSearch> findByDepartureDateBetween(LocalDate startDate, LocalDate endDate);

  /**
   * Finds flight searches by user, origin, and destination.
   * <p>
   * This method combines user-specific filtering with route-specific filtering,
   * retrieving
   * all searches performed by a particular user for a specific route
   * (origin-destination pair).
   * Useful for analyzing a user's repeated interest in a particular route or for
   * providing
   * personalized suggestions based on previous search patterns.
   *
   * @param user        the user whose search history to retrieve
   * @param origin      the origin airport code (3-letter IATA code, e.g., "JFK")
   * @param destination the destination airport code (3-letter IATA code, e.g.,
   *                    "LAX")
   * @return a list of flight searches matching the specified user and route,
   *         which may be
   *         empty if no searches match the criteria
   */
  List<FlightSearch> findByUserAndOriginAndDestination(User user, String origin, String destination);
}