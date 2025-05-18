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
 * REST controller for flight booking operations in the SkyExplorer application.
 * <p>
 * This controller provides endpoints for creating, retrieving, updating, and
 * deleting flight
 * bookings. All operations are performed in the context of the currently
 * authenticated user,
 * ensuring that users can only access and modify their own bookings.
 * </p>
 * <p>
 * The controller uses {@link BookingService} to handle business logic and data
 * persistence
 * operations related to bookings.
 * </p>
 */
@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

  private final BookingService bookingService;
  private final UserRepository userRepository;

  /**
   * Helper method to get the current user's ID from the authentication context.
   * <p>
   * Retrieves user details from the Spring Security context and looks up the
   * corresponding
   * user entity in the database to get their unique identifier.
   * </p>
   *
   * @return the ID of the currently authenticated user
   * @throws EntityNotFoundException if the user is not found in the database
   */
  private Long getCurrentUserId() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String username = auth.getName();

    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new EntityNotFoundException("User not found with username: " + username));

    return user.getId();
  }

  /**
   * Retrieves all bookings for the currently authenticated user.
   * <p>
   * This endpoint returns a list of all flight bookings associated with the
   * current user,
   * including details such as flight information, passenger data, and booking
   * status.
   * </p>
   * 
   * @return ResponseEntity containing a list of BookingDTO objects with HTTP
   *         status 200 (OK)
   */
  @GetMapping
  public ResponseEntity<List<BookingDTO>> getUserBookings() {
    Long userId = getCurrentUserId();
    List<BookingDTO> bookings = bookingService.getUserBookings(userId);
    return ResponseEntity.ok(bookings);
  }

  /**
   * Retrieves a specific booking by its ID for the authenticated user.
   * <p>
   * This endpoint returns detailed information about a single booking. The system
   * verifies
   * that the requested booking belongs to the current user before returning the
   * data.
   * </p>
   * 
   * @param bookingId the unique identifier of the booking to retrieve
   * @return ResponseEntity containing the BookingDTO with HTTP status 200 (OK)
   * @throws EntityNotFoundException if the booking does not exist or doesn't
   *                                 belong to the user
   */
  @GetMapping("/{bookingId}")
  public ResponseEntity<BookingDTO> getBookingById(@PathVariable Long bookingId) {
    Long userId = getCurrentUserId();
    BookingDTO booking = bookingService.getBookingById(bookingId, userId);
    return ResponseEntity.ok(booking);
  }

  /**
   * Creates a new flight booking for the authenticated user.
   * <p>
   * This endpoint processes the booking request, associates it with the current
   * user,
   * and persists it in the system. The user ID is automatically set based on the
   * authentication context.
   * </p>
   * 
   * @param bookingDTO the booking details including flight information and
   *                   passenger data
   * @return ResponseEntity containing the created BookingDTO with HTTP status 201
   *         (Created)
   */
  @PostMapping
  public ResponseEntity<BookingDTO> createBooking(@RequestBody BookingDTO bookingDTO) {
    Long userId = getCurrentUserId();
    bookingDTO.setUserId(userId);
    BookingDTO createdBooking = bookingService.createBooking(bookingDTO);
    return new ResponseEntity<>(createdBooking, HttpStatus.CREATED);
  }

  /**
   * Updates an existing booking for the authenticated user.
   * <p>
   * This endpoint allows modifying the details of an existing booking. The system
   * verifies
   * that the booking belongs to the current user before processing the update
   * request.
   * </p>
   * 
   * @param bookingId  the unique identifier of the booking to update
   * @param bookingDTO the updated booking details
   * @return ResponseEntity containing the updated BookingDTO with HTTP status 200
   *         (OK)
   * @throws EntityNotFoundException if the booking does not exist or doesn't
   *                                 belong to the user
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
   * Cancels an existing booking for the authenticated user.
   * <p>
   * This endpoint changes the status of a booking to canceled. The system
   * verifies
   * that the booking belongs to the current user before processing the
   * cancellation.
   * </p>
   * 
   * @param bookingId the unique identifier of the booking to cancel
   * @return ResponseEntity containing the canceled BookingDTO with HTTP status
   *         200 (OK)
   * @throws EntityNotFoundException if the booking does not exist or doesn't
   *                                 belong to the user
   */
  @PatchMapping("/{bookingId}/cancel")
  public ResponseEntity<BookingDTO> cancelBooking(@PathVariable Long bookingId) {
    Long userId = getCurrentUserId();
    BookingDTO cancelledBooking = bookingService.cancelBooking(bookingId, userId);
    return ResponseEntity.ok(cancelledBooking);
  }

  /**
   * Permanently deletes a booking for the authenticated user.
   * <p>
   * This endpoint removes a booking from the system entirely. The system verifies
   * that the booking belongs to the current user before processing the deletion.
   * </p>
   * 
   * @param bookingId the unique identifier of the booking to delete
   * @return ResponseEntity with HTTP status 204 (No Content) indicating
   *         successful deletion
   * @throws EntityNotFoundException if the booking does not exist or doesn't
   *                                 belong to the user
   */
  @DeleteMapping("/{bookingId}")
  public ResponseEntity<Void> deleteBooking(@PathVariable Long bookingId) {
    Long userId = getCurrentUserId();
    bookingService.deleteBooking(bookingId, userId);
    return ResponseEntity.noContent().build();
  }
}
