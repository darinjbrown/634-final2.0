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
 * Repository interface for FlightSearch entity operations
 */
@Repository
public interface FlightSearchRepository extends JpaRepository<FlightSearch, Long> {

  /**
   * Find all flight searches by user
   * 
   * @param user the user whose searches to find
   * @return a list of flight searches
   */
  List<FlightSearch> findByUser(User user);

  /**
   * Find all flight searches by user with pagination
   * 
   * @param user     the user whose searches to find
   * @param pageable pagination information
   * @return a page of flight searches
   */
  Page<FlightSearch> findByUser(User user, Pageable pageable);

  /**
   * Find flight searches by origin and destination
   * 
   * @param origin      the origin airport code
   * @param destination the destination airport code
   * @return a list of flight searches
   */
  List<FlightSearch> findByOriginAndDestination(String origin, String destination);

  /**
   * Find flight searches by departure date range
   * 
   * @param startDate the start of the date range
   * @param endDate   the end of the date range
   * @return a list of flight searches
   */
  List<FlightSearch> findByDepartureDateBetween(LocalDate startDate, LocalDate endDate);

  /**
   * Find flight searches by user, origin and destination
   * 
   * @param user        the user whose searches to find
   * @param origin      the origin airport code
   * @param destination the destination airport code
   * @return a list of flight searches
   */
  List<FlightSearch> findByUserAndOriginAndDestination(User user, String origin, String destination);
}