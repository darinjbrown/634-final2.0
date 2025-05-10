package com.__final_backend.backend.repository;

import com.__final_backend.backend.entity.BookingRecord;
import com.__final_backend.backend.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for BookingRecord entity operations
 */
@Repository
public interface BookingRecordRepository extends JpaRepository<BookingRecord, Long> {

  /**
   * Find a booking record by its reference number
   * 
   * @param bookingReference the booking reference to search for
   * @return an Optional containing the booking record if found
   */
  Optional<BookingRecord> findByBookingReference(String bookingReference);

  /**
   * Find all booking records for a specific user
   * 
   * @param user the user whose booking records to find
   * @return a list of booking records
   */
  List<BookingRecord> findByUser(User user);

  /**
   * Find all booking records for a specific user with pagination
   * 
   * @param user     the user whose booking records to find
   * @param pageable pagination information
   * @return a page of booking records
   */
  Page<BookingRecord> findByUser(User user, Pageable pageable);

  /**
   * Find booking records by booking status
   * 
   * @param bookingStatus the booking status to search for
   * @return a list of booking records
   */
  List<BookingRecord> findByBookingStatus(String bookingStatus);

  /**
   * Find booking records by origin and destination
   * 
   * @param origin      the origin airport code
   * @param destination the destination airport code
   * @return a list of booking records
   */
  List<BookingRecord> findByOriginAndDestination(String origin, String destination);

  /**
   * Find booking records by departure time range
   * 
   * @param startTime the start of the time range
   * @param endTime   the end of the time range
   * @return a list of booking records
   */
  List<BookingRecord> findByDepartureTimeBetween(LocalDateTime startTime, LocalDateTime endTime);

  /**
   * Find booking records by user and booking status
   * 
   * @param user          the user whose booking records to find
   * @param bookingStatus the booking status to search for
   * @return a list of booking records
   */
  List<BookingRecord> findByUserAndBookingStatus(User user, String bookingStatus);

  /**
   * Check if a booking reference already exists
   * 
   * @param bookingReference the booking reference to check
   * @return true if the reference exists, false otherwise
   */
  boolean existsByBookingReference(String bookingReference);
}