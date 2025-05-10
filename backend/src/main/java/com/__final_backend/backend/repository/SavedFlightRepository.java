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
 * Repository interface for SavedFlight entity operations
 */
@Repository
public interface SavedFlightRepository extends JpaRepository<SavedFlight, Long> {

  /**
   * Find all saved flights for a specific user
   * 
   * @param user the user whose saved flights to find
   * @return a list of saved flights
   */
  List<SavedFlight> findByUser(User user);

  /**
   * Find all saved flights for a specific user with pagination
   * 
   * @param user     the user whose saved flights to find
   * @param pageable pagination information
   * @return a page of saved flights
   */
  Page<SavedFlight> findByUser(User user, Pageable pageable);

  /**
   * Find saved flights by origin and destination
   * 
   * @param origin      the origin airport code
   * @param destination the destination airport code
   * @return a list of saved flights
   */
  List<SavedFlight> findByOriginAndDestination(String origin, String destination);

  /**
   * Find saved flights by airline code
   * 
   * @param airlineCode the airline code
   * @return a list of saved flights
   */
  List<SavedFlight> findByAirlineCode(String airlineCode);

  /**
   * Find saved flights by departure time range
   * 
   * @param startTime the start of the time range
   * @param endTime   the end of the time range
   * @return a list of saved flights
   */
  List<SavedFlight> findByDepartureTimeBetween(LocalDateTime startTime, LocalDateTime endTime);

  /**
   * Find saved flights by user, origin and destination
   * 
   * @param user        the user whose saved flights to find
   * @param origin      the origin airport code
   * @param destination the destination airport code
   * @return a list of saved flights
   */
  List<SavedFlight> findByUserAndOriginAndDestination(User user, String origin, String destination);

  /**
   * Delete all saved flights for a specific user
   * 
   * @param user the user whose saved flights to delete
   */
  void deleteByUser(User user);
}