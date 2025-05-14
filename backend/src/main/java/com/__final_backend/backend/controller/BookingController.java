package com.__final_backend.backend.controller;

import com.__final_backend.backend.dto.BookingDTO;
import com.__final_backend.backend.entity.User;
import com.__final_backend.backend.repository.UserRepository;
import com.__final_backend.backend.service.BookingService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for booking operations
 */
@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

  private final BookingService bookingService;
  private final UserRepository userRepository;

  /**
   * Helper method to get the current user's ID from the authentication context
   *
   * @return the ID of the currently authenticated user
   * @throws EntityNotFoundException if the user is not found
   */
  private Long getCurrentUserId() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String username = auth.getName();

    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new EntityNotFoundException("User not found with username: " + username));

    return user.getId();
  }

  /**
   * Get all bookings for the authenticated user
   * 
   * @return List of BookingDTO objects
   */
  @GetMapping
  public ResponseEntity<List<BookingDTO>> getUserBookings() {
    Long userId = getCurrentUserId();
    List<BookingDTO> bookings = bookingService.getUserBookings(userId);
    return ResponseEntity.ok(bookings);
  }

  /**
   * Get a booking by ID
   * 
   * @param bookingId ID of the booking
   * @return BookingDTO for the requested booking
   */
  @GetMapping("/{bookingId}")
  public ResponseEntity<BookingDTO> getBookingById(@PathVariable Long bookingId) {
    Long userId = getCurrentUserId();
    BookingDTO booking = bookingService.getBookingById(bookingId, userId);
    return ResponseEntity.ok(booking);
  }

  /**
   * Create a new booking
   * 
   * @param bookingDTO BookingDTO with booking details
   * @return Created BookingDTO
   */
  @PostMapping
  public ResponseEntity<BookingDTO> createBooking(@RequestBody BookingDTO bookingDTO) {
    Long userId = getCurrentUserId();
    bookingDTO.setUserId(userId);
    BookingDTO createdBooking = bookingService.createBooking(bookingDTO);
    return new ResponseEntity<>(createdBooking, HttpStatus.CREATED);
  }

  /**
   * Update an existing booking
   * 
   * @param bookingId  ID of the booking to update
   * @param bookingDTO BookingDTO with updated details
   * @return Updated BookingDTO
   */
  @PutMapping("/{bookingId}")
  public ResponseEntity<BookingDTO> updateBooking(
      @PathVariable Long bookingId,
      @RequestBody BookingDTO bookingDTO) {
    Long userId = getCurrentUserId();
    BookingDTO updatedBooking = bookingService.updateBooking(bookingId, bookingDTO, userId);
    return ResponseEntity.ok(updatedBooking);
  }

  /**
   * Cancel a booking
   * 
   * @param bookingId ID of the booking to cancel
   * @return Cancelled BookingDTO
   */
  @PatchMapping("/{bookingId}/cancel")
  public ResponseEntity<BookingDTO> cancelBooking(@PathVariable Long bookingId) {
    Long userId = getCurrentUserId();
    BookingDTO cancelledBooking = bookingService.cancelBooking(bookingId, userId);
    return ResponseEntity.ok(cancelledBooking);
  }

  /**
   * Delete a booking
   * 
   * @param bookingId ID of the booking to delete
   * @return No content response
   */
  @DeleteMapping("/{bookingId}")
  public ResponseEntity<Void> deleteBooking(@PathVariable Long bookingId) {
    Long userId = getCurrentUserId();
    bookingService.deleteBooking(bookingId, userId);
    return ResponseEntity.noContent().build();
  }
}
