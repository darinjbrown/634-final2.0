package com.__final_backend.backend.service.db;

import com.__final_backend.backend.entity.BookingRecord;
import com.__final_backend.backend.entity.User;
import com.__final_backend.backend.repository.BookingRecordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
 * Service class for booking record DB operations
 */
@Service("bookingRecordService")
public class BookingService {

  private static final Logger logger = LoggerFactory.getLogger(BookingService.class);
  private final BookingRecordRepository bookingRecordRepository;

  @Autowired
  public BookingService(BookingRecordRepository bookingRecordRepository) {
    this.bookingRecordRepository = bookingRecordRepository;
  }

  /**
   * Create a new booking record
   * 
   * @param bookingRecord the booking record to create
   * @return the created booking record
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
   * Get a booking record by ID
   * 
   * @param id the booking record ID
   * @return the booking record if found
   */
  public Optional<BookingRecord> getBookingById(Long id) {
    logger.info("Getting booking with ID: {}", id);
    return bookingRecordRepository.findById(id);
  }

  /**
   * Get a booking record by booking reference
   * 
   * @param bookingReference the booking reference
   * @return the booking record if found
   */
  public Optional<BookingRecord> getBookingByReference(String bookingReference) {
    logger.info("Getting booking with reference: {}", bookingReference);
    return bookingRecordRepository.findByBookingReference(bookingReference);
  }

  /**
   * Get all booking records for a user
   * 
   * @param user the user
   * @return list of booking records
   */
  public List<BookingRecord> getBookingsByUser(User user) {
    logger.info("Getting all bookings for user: {}", user.getUsername());
    return bookingRecordRepository.findByUser(user);
  }

  /**
   * Get booking records for a user with pagination
   * 
   * @param user     the user
   * @param pageable pagination information
   * @return page of booking records
   */
  public Page<BookingRecord> getBookingsByUser(User user, Pageable pageable) {
    logger.info("Getting paged bookings for user: {}", user.getUsername());
    return bookingRecordRepository.findByUser(user, pageable);
  }

  /**
   * Update a booking record
   * 
   * @param bookingRecord the booking record with updated information
   * @return the updated booking record
   */
  public BookingRecord updateBooking(BookingRecord bookingRecord) {
    logger.info("Updating booking with reference: {}", bookingRecord.getBookingReference());
    return bookingRecordRepository.save(bookingRecord);
  }

  /**
   * Update booking status
   * 
   * @param bookingReference the booking reference
   * @param newStatus        the new booking status
   * @return true if updated successfully, false otherwise
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
   * Delete a booking record
   * 
   * @param id the booking record ID
   */
  public void deleteBooking(Long id) {
    logger.info("Deleting booking with ID: {}", id);
    bookingRecordRepository.deleteById(id);
  }

  /**
   * Generate a unique booking reference
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