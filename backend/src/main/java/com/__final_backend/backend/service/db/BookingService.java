package com.__final_backend.backend.service.db;

import com.__final_backend.backend.entity.BookingRecord;
import com.__final_backend.backend.entity.User;
import com.__final_backend.backend.repository.BookingRecordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
 * Service class for managing flight booking operations.
 * <p>
 * This service handles the creation, retrieval, updating, and deletion of
 * booking records.
 * It includes functionality for generating unique booking references, managing
 * booking statuses,
 * and retrieving bookings by various criteria such as ID, reference number, or
 * user.
 * <p>
 * All database operations are performed through the BookingRecordRepository,
 * and
 * methods include appropriate logging for operational monitoring and
 * troubleshooting.
 */
@Service("bookingRecordService")
public class BookingService {
  /** Logger for this class. */
  private static final Logger logger = LoggerFactory.getLogger(BookingService.class);

  /** Repository for database operations on booking records. */
  private final BookingRecordRepository bookingRecordRepository;

  /**
   * Constructs a new BookingService with the specified repository.
   * <p>
   * Spring automatically injects the appropriate BookingRecordRepository
   * implementation.
   * The @Autowired annotation is optional for constructor injection since Spring
   * 4.3.
   *
   * @param bookingRecordRepository the repository for booking record persistence
   */
  public BookingService(BookingRecordRepository bookingRecordRepository) {
    this.bookingRecordRepository = bookingRecordRepository;
  }

  /**
   * Creates a new booking record in the database.
   * <p>
   * This method persists a new booking record to the database, automatically
   * generating
   * a unique booking reference if one is not provided. The booking reference
   * follows
   * a format of two uppercase letters followed by six digits.
   * 
   * @param bookingRecord the booking record to create, must not be null
   * @return the created booking record with populated ID and generated booking
   *         reference
   * @throws IllegalArgumentException if bookingRecord is null
   */
  public BookingRecord createBooking(BookingRecord bookingRecord) {
    // Generate a unique booking reference if not provided
    if (bookingRecord.getBookingReference() == null || bookingRecord.getBookingReference().isEmpty()) {
      bookingRecord.setBookingReference(generateUniqueBookingReference());
    }

    logger.info("Creating booking with reference: {}", bookingRecord.getBookingReference());
    return bookingRecordRepository.save(bookingRecord);
  }

  /**
   * Retrieves a booking record by its unique identifier.
   * <p>
   * This method attempts to find a booking record with the specified ID in the
   * database.
   * If no record exists with the given ID, an empty Optional is returned.
   * 
   * @param id the unique identifier of the booking record, must not be null
   * @return an Optional containing the booking record if found, or an empty
   *         Optional if not found
   * @throws IllegalArgumentException if id is null
   */
  public Optional<BookingRecord> getBookingById(Long id) {
    logger.info("Getting booking with ID: {}", id);
    return bookingRecordRepository.findById(id);
  }

  /**
   * Retrieves a booking record by its booking reference.
   * <p>
   * This method searches for a booking using the unique booking reference string.
   * This is typically used for customer-facing lookups where the customer has a
   * booking reference number but not the internal ID.
   * 
   * @param bookingReference the unique booking reference string, must not be null
   *                         or empty
   * @return an Optional containing the booking record if found, or an empty
   *         Optional if not found
   * @throws IllegalArgumentException if bookingReference is null or empty
   */
  public Optional<BookingRecord> getBookingByReference(String bookingReference) {
    logger.info("Getting booking with reference: {}", bookingReference);
    return bookingRecordRepository.findByBookingReference(bookingReference);
  }

  /**
   * Retrieves all booking records for a specific user.
   * <p>
   * This method returns all bookings associated with the specified user without
   * pagination.
   * For users with many bookings, consider using the paginated version
   * {@link #getBookingsByUser(User, Pageable)} instead to improve performance.
   * 
   * @param user the user whose bookings to find, must not be null
   * @return a list of all booking records for the user; empty list if none found
   * @throws IllegalArgumentException if user is null
   * @see #getBookingsByUser(User, Pageable)
   */
  public List<BookingRecord> getBookingsByUser(User user) {
    logger.info("Getting all bookings for user: {}", user.getUsername());
    return bookingRecordRepository.findByUser(user);
  }

  /**
   * Retrieves booking records for a specific user with pagination support.
   * <p>
   * This method is recommended for users with many bookings as it improves
   * performance
   * and reduces memory consumption. The pageable parameter allows controlling
   * page size,
   * page number, and sorting options.
   * 
   * @param user     the user whose bookings to find, must not be null
   * @param pageable pagination information including page number, size, and
   *                 sorting, must not be null
   * @return a page of booking records for the user
   * @throws IllegalArgumentException if user or pageable is null
   */
  public Page<BookingRecord> getBookingsByUser(User user, Pageable pageable) {
    logger.info("Getting paged bookings for user: {}", user.getUsername());
    return bookingRecordRepository.findByUser(user, pageable);
  }

  /**
   * Updates an existing booking record with new information.
   * <p>
   * This method persists changes to an existing booking record. The booking
   * record must
   * have a valid ID that corresponds to an existing record in the database.
   * Otherwise,
   * a new record will be created instead of updating an existing one.
   * 
   * @param bookingRecord the booking record with updated information, must not be
   *                      null
   * @return the updated booking record with any database-generated values
   * @throws IllegalArgumentException if bookingRecord is null
   */
  public BookingRecord updateBooking(BookingRecord bookingRecord) {
    logger.info("Updating booking with reference: {}", bookingRecord.getBookingReference());
    return bookingRecordRepository.save(bookingRecord);
  }

  /**
   * Updates the status of a booking identified by its reference.
   * <p>
   * This method provides a convenient way to update just the status of a booking
   * without needing to fetch, modify, and save the entire entity. Common statuses
   * include "CONFIRMED", "CANCELLED", "CHECKED_IN", etc.
   * 
   * @param bookingReference the unique booking reference string, must not be null
   *                         or empty
   * @param newStatus        the new booking status to set, must not be null
   * @return true if the booking was found and updated, false if the booking was
   *         not found
   * @throws IllegalArgumentException if bookingReference is null or empty, or if
   *                                  newStatus is null
   */
  public boolean updateBookingStatus(String bookingReference, String newStatus) {
    Optional<BookingRecord> bookingOpt = bookingRecordRepository.findByBookingReference(bookingReference);
    if (bookingOpt.isPresent()) {
      BookingRecord booking = bookingOpt.get();
      booking.setBookingStatus(newStatus);
      bookingRecordRepository.save(booking);
      logger.info("Updated status of booking {} to {}", bookingReference, newStatus);
      return true;
    }
    logger.warn("Failed to update status of booking {}: not found", bookingReference);
    return false;
  }

  /**
   * Deletes a booking record from the database by its ID.
   * <p>
   * This method permanently removes a booking record from the database. This
   * operation
   * cannot be undone, so it should be used with caution and proper authorization.
   * 
   * @param id the unique identifier of the booking record to delete, must not be
   *           null
   * @throws IllegalArgumentException                               if id is null
   * @throws org.springframework.dao.EmptyResultDataAccessException if no entity
   *                                                                exists with
   *                                                                the given ID
   */
  public void deleteBooking(Long id) {
    logger.info("Deleting booking with ID: {}", id);
    bookingRecordRepository.deleteById(id);
  }

  /**
   * Generates a unique booking reference string.
   * <p>
   * This method creates a booking reference in the format of two uppercase
   * letters
   * followed by six digits (e.g., "AB123456"). It ensures uniqueness by checking
   * against existing references in the database. This provides a user-friendly
   * reference that is less prone to transcription errors than purely numeric IDs.
   * 
   * @return a unique booking reference string
   */
  private String generateUniqueBookingReference() {
    Random random = new Random();
    String bookingRef;
    do {
      // Generate a mix of letters and numbers
      StringBuilder sb = new StringBuilder();
      sb.append((char) (random.nextInt(26) + 'A'));
      sb.append((char) (random.nextInt(26) + 'A'));

      // Add 6 digits
      for (int i = 0; i < 6; i++) {
        sb.append(random.nextInt(10));
      }
      bookingRef = sb.toString();
    } while (bookingRecordRepository.existsByBookingReference(bookingRef));

    return bookingRef;
  }
}