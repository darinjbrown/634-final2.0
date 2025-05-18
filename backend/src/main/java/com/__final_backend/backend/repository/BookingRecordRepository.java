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
 * Repository interface for BookingRecord entity operations.
 * <p>
 * Provides methods for managing flight booking records in the database,
 * including CRUD operations
 * inherited from JpaRepository and custom query methods for retrieving bookings
 * based on
 * various criteria such as reference number, user, route, and booking status.
 * <p>
 * The BookingRecord entity represents confirmed flight reservations made by
 * users, which are
 * essential for tracking a customer's itinerary and providing booking
 * management features.
 */
@Repository
public interface BookingRecordRepository extends JpaRepository<BookingRecord, Long> {
  /**
   * Finds a booking record by its reference number.
   * <p>
   * This method is typically used for looking up a specific booking when a user
   * provides
   * a booking reference during check-in, booking management, or customer support
   * inquiries.
   * The booking reference is expected to be unique across all bookings.
   *
   * @param bookingReference the booking reference to search for (e.g.,
   *                         "ABC123XYZ")
   * @return an Optional containing the booking record if found, or empty if no
   *         booking
   *         with the given reference exists
   */
  Optional<BookingRecord> findByBookingReference(String bookingReference);

  /**
   * Finds all booking records for a specific user.
   * <p>
   * This method retrieves the complete booking history for a particular user,
   * ordered by the
   * default repository ordering (typically by creation date or ID). Use this
   * method when
   * pagination is not required and you need the user's full booking history.
   *
   * @param user the user whose booking records to find
   * @return a list of all booking records belonging to the specified user, which
   *         may be empty
   *         if the user has not made any bookings
   */
  List<BookingRecord> findByUser(User user);

  /**
   * Finds all booking records for a specific user with pagination support.
   * <p>
   * This method retrieves the booking history for a user with support for
   * pagination, sorting,
   * and filtering. It's particularly useful for displaying booking history in a
   * paginated view
   * or when dealing with users who have an extensive booking history.
   *
   * @param user     the user whose booking records to find
   * @param pageable pagination and sorting information (page number, page size,
   *                 sort criteria)
   * @return a page of booking records belonging to the specified user, which may
   *         be empty if
   *         the user has not made any bookings or the pageable parameters go
   *         beyond
   *         available results
   */
  Page<BookingRecord> findByUser(User user, Pageable pageable);

  /**
   * Finds booking records by booking status.
   * <p>
   * This method retrieves all bookings with a specific status, regardless of
   * which users
   * made them. Useful for administrative tasks such as monitoring confirmed
   * bookings,
   * tracking cancellations, or following up on pending bookings.
   *
   * @param bookingStatus the booking status to search for (e.g., "CONFIRMED",
   *                      "CANCELLED",
   *                      "CHECKED_IN")
   * @return a list of booking records with the specified status, which may be
   *         empty if
   *         no bookings match the status
   */
  List<BookingRecord> findByBookingStatus(String bookingStatus);

  /**
   * Finds booking records by origin and destination airports.
   * <p>
   * This method retrieves all bookings for a specific route (origin-destination
   * pair),
   * regardless of which users made them. Useful for route analysis, capacity
   * planning,
   * or for identifying popular routes across all bookings.
   *
   * @param origin      the origin airport code (3-letter IATA code, e.g., "JFK")
   * @param destination the destination airport code (3-letter IATA code, e.g.,
   *                    "LAX")
   * @return a list of booking records matching the specified route, which may be
   *         empty if
   *         no bookings match the criteria
   */
  List<BookingRecord> findByOriginAndDestination(String origin, String destination);

  /**
   * Finds booking records by departure time range.
   * <p>
   * This method retrieves all bookings with flights scheduled to depart within a
   * specific
   * time window. Useful for operational planning, analyzing booking patterns for
   * specific
   * travel periods, or identifying potentially affected bookings during
   * disruptions.
   *
   * @param startTime the start of the time range (inclusive)
   * @param endTime   the end of the time range (inclusive)
   * @return a list of booking records with departure times within the specified
   *         range,
   *         which may be empty if no bookings match the criteria
   */
  List<BookingRecord> findByDepartureTimeBetween(LocalDateTime startTime, LocalDateTime endTime);

  /**
   * Finds booking records by user and booking status.
   * <p>
   * This method combines user-specific filtering with status filtering,
   * retrieving all
   * bookings made by a particular user that are in a specific status. Useful for
   * showing
   * a user their confirmed bookings, cancelled bookings, or bookings ready for
   * check-in.
   *
   * @param user          the user whose booking records to find
   * @param bookingStatus the booking status to search for (e.g., "CONFIRMED",
   *                      "CANCELLED",
   *                      "CHECKED_IN")
   * @return a list of booking records matching the specified user and status,
   *         which may be
   *         empty if no bookings match the criteria
   */
  List<BookingRecord> findByUserAndBookingStatus(User user, String bookingStatus);

  /**
   * Checks if a booking reference already exists in the database.
   * <p>
   * This method is typically used during booking creation to ensure uniqueness of
   * booking references. It performs a more efficient check than
   * findByBookingReference
   * since it doesn't need to retrieve the entire booking record.
   *
   * @param bookingReference the booking reference to check (e.g., "ABC123XYZ")
   * @return true if the reference exists, false otherwise
   */
  boolean existsByBookingReference(String bookingReference);
}