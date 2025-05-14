package com.__final_backend.backend.service;

import com.__final_backend.backend.dto.BookingDTO;
import com.__final_backend.backend.entity.BookingRecord;
import com.__final_backend.backend.entity.User;
import com.__final_backend.backend.repository.BookingRecordRepository;
import com.__final_backend.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for managing booking operations
 */
@Service
@RequiredArgsConstructor
public class BookingService {
  private final BookingRecordRepository bookingRecordRepository;
  private final UserRepository userRepository;

  /**
   * Get all bookings for a user
   *
   * @param userId ID of the user
   * @return List of BookingDTO objects
   */
  @Transactional(readOnly = true)
  public List<BookingDTO> getUserBookings(Long userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));
    List<BookingRecord> bookings = bookingRecordRepository.findByUser(user);
    return bookings.stream()
        .map(BookingDTO::fromEntity)
        .collect(Collectors.toList());
  }

  /**
   * Get booking by ID
   *
   * @param bookingId ID of the booking
   * @param userId    ID of the user requesting the booking
   * @return BookingDTO for the requested booking
   */
  @Transactional(readOnly = true)
  public BookingDTO getBookingById(Long bookingId, Long userId) {
    BookingRecord booking = bookingRecordRepository.findById(bookingId)
        .orElseThrow(() -> new EntityNotFoundException("Booking not found with ID: " + bookingId));

    // Check if the booking belongs to the user or if user has admin role
    if (!booking.getUser().getId().equals(userId)) {
      throw new AccessDeniedException("You don't have permission to access this booking");
    }

    return BookingDTO.fromEntity(booking);
  }

  /**
   * Create a new booking
   *
   * @param bookingDTO BookingDTO with booking details
   * @return Created BookingDTO
   */
  @Transactional
  public BookingDTO createBooking(BookingDTO bookingDTO) {
    User user = userRepository.findById(bookingDTO.getUserId())
        .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + bookingDTO.getUserId()));

    BookingRecord booking = new BookingRecord();
    booking.setBookingReference(generateBookingReference());
    booking.setUser(user);

    // Map DTO fields to entity fields based on the actual BookingRecord entity
    // structure
    booking.setOrigin(bookingDTO.getDepartureAirport());
    booking.setDestination(bookingDTO.getArrivalAirport());
    booking.setDepartureTime(bookingDTO.getDepartureTime());
    booking.setArrivalTime(bookingDTO.getArrivalTime());
    booking.setAirlineCode(bookingDTO.getAirline());
    booking.setFlightNumber(bookingDTO.getFlightNumber());
    booking.setPassengerCount(bookingDTO.getPassengerCount());
    booking.setTotalPrice(bookingDTO.getTotalPrice());
    booking.setBookingStatus("CONFIRMED"); // Default status

    // Note: The entity doesn't have fields for contactEmail, contactPhone, etc.
    // If you need these, the entity will need to be updated

    BookingRecord savedBooking = bookingRecordRepository.save(booking);

    // For response, we need to create a DTO that matches what the client expects
    // Even though our entity doesn't have all these fields
    BookingDTO responseDTO = new BookingDTO();
    responseDTO.setId(savedBooking.getId());
    responseDTO.setBookingReference(savedBooking.getBookingReference());
    responseDTO.setUserId(savedBooking.getUser().getId());
    responseDTO.setUsername(savedBooking.getUser().getUsername());
    responseDTO.setDepartureAirport(savedBooking.getOrigin());
    responseDTO.setArrivalAirport(savedBooking.getDestination());
    responseDTO.setDepartureTime(savedBooking.getDepartureTime());
    responseDTO.setArrivalTime(savedBooking.getArrivalTime());
    responseDTO.setAirline(savedBooking.getAirlineCode());
    responseDTO.setFlightNumber(savedBooking.getFlightNumber());
    responseDTO.setPassengerCount(savedBooking.getPassengerCount());
    responseDTO.setTotalPrice(savedBooking.getTotalPrice());
    responseDTO.setBookingStatus(savedBooking.getBookingStatus());
    responseDTO.setBookingDate(savedBooking.getCreatedAt());

    // Copy over additional fields from the input DTO
    responseDTO.setContactEmail(bookingDTO.getContactEmail());
    responseDTO.setContactPhone(bookingDTO.getContactPhone());
    responseDTO.setPaymentMethod(bookingDTO.getPaymentMethod());
    responseDTO.setAdditionalNotes(bookingDTO.getAdditionalNotes());
    responseDTO.setFlightId(bookingDTO.getFlightId());

    return responseDTO;
  }

  /**
   * Update an existing booking
   *
   * @param bookingId  ID of the booking to update
   * @param bookingDTO BookingDTO with updated details
   * @param userId     ID of the user making the update
   * @return Updated BookingDTO
   */
  @Transactional
  public BookingDTO updateBooking(Long bookingId, BookingDTO bookingDTO, Long userId) {
    BookingRecord booking = bookingRecordRepository.findById(bookingId)
        .orElseThrow(() -> new EntityNotFoundException("Booking not found with ID: " + bookingId));

    // Check if the booking belongs to the user
    if (!booking.getUser().getId().equals(userId)) {
      throw new AccessDeniedException("You don't have permission to update this booking");
    }

    // Update fields that are allowed to be changed
    booking.setPassengerCount(bookingDTO.getPassengerCount());

    // Note: ContactEmail, ContactPhone, and AdditionalNotes fields don't exist in
    // BookingRecord
    // Only update the fields that exist in the entity

    BookingRecord updatedBooking = bookingRecordRepository.save(booking);

    // For response, create a DTO with all the expected fields
    BookingDTO responseDTO = BookingDTO.fromEntity(updatedBooking);

    // Copy over the fields that aren't stored in the entity from the input DTO
    responseDTO.setContactEmail(bookingDTO.getContactEmail());
    responseDTO.setContactPhone(bookingDTO.getContactPhone());
    responseDTO.setPaymentMethod(bookingDTO.getPaymentMethod());
    responseDTO.setAdditionalNotes(bookingDTO.getAdditionalNotes());
    responseDTO.setFlightId(bookingDTO.getFlightId());

    return responseDTO;
  }

  /**
   * Cancel a booking
   *
   * @param bookingId ID of the booking to cancel
   * @param userId    ID of the user cancelling the booking
   * @return Cancelled BookingDTO
   */
  @Transactional
  public BookingDTO cancelBooking(Long bookingId, Long userId) {
    BookingRecord booking = bookingRecordRepository.findById(bookingId)
        .orElseThrow(() -> new EntityNotFoundException("Booking not found with ID: " + bookingId));

    // Check if the booking belongs to the user
    if (!booking.getUser().getId().equals(userId)) {
      throw new AccessDeniedException("You don't have permission to cancel this booking");
    }

    booking.setBookingStatus("CANCELLED");
    BookingRecord cancelledBooking = bookingRecordRepository.save(booking);
    return BookingDTO.fromEntity(cancelledBooking);
  }

  /**
   * Delete a booking
   *
   * @param bookingId ID of the booking to delete
   * @param userId    ID of the user deleting the booking
   */
  @Transactional
  public void deleteBooking(Long bookingId, Long userId) {
    BookingRecord booking = bookingRecordRepository.findById(bookingId)
        .orElseThrow(() -> new EntityNotFoundException("Booking not found with ID: " + bookingId));

    // Check if the booking belongs to the user
    if (!booking.getUser().getId().equals(userId)) {
      throw new AccessDeniedException("You don't have permission to delete this booking");
    }

    bookingRecordRepository.delete(booking);
  }

  /**
   * Generate a unique booking reference
   *
   * @return Unique booking reference string
   */
  private String generateBookingReference() {
    // Generate a booking reference with format: B-xxxxx (where x is alphanumeric)
    return "B-" + UUID.randomUUID().toString().substring(0, 10).toUpperCase();
  }
}