package com.__final_backend.backend.test.unit.service.db;

import com.__final_backend.backend.entity.BookingRecord;
import com.__final_backend.backend.entity.User;
import com.__final_backend.backend.repository.BookingRecordRepository;
import com.__final_backend.backend.service.db.BookingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the BookingService class.
 * Tests basic booking functionality.
 */
class BookingServiceTest {

  @Mock
  private BookingRecordRepository bookingRecordRepository;

  private BookingService bookingService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    bookingService = new BookingService(bookingRecordRepository);
  }

  /**
   * Test creating a booking with valid flight data.
   * Verifies that a booking can be created successfully.
   */
  @Test
  void testCreateBookingWithValidData() {
    // Arrange
    User user = new User();
    user.setId(1L);
    user.setUsername("testUser");

    BookingRecord bookingRecord = new BookingRecord();
    bookingRecord.setUser(user);
    bookingRecord.setFlightNumber("AA123");
    bookingRecord.setOrigin("JFK");
    bookingRecord.setDestination("LAX");
    bookingRecord.setDepartureTime(LocalDateTime.now().plusDays(7));
    bookingRecord.setArrivalTime(LocalDateTime.now().plusDays(7).plusHours(6));

    // Configure mock to return the booking with an ID
    when(bookingRecordRepository.save(any(BookingRecord.class))).thenAnswer(invocation -> {
      BookingRecord savedBooking = invocation.getArgument(0);
      savedBooking.setId(1L);
      // Ensure booking reference was generated
      assertNotNull(savedBooking.getBookingReference());
      return savedBooking;
    });

    // Act
    BookingRecord createdBooking = bookingService.createBooking(bookingRecord);

    // Assert
    assertNotNull(createdBooking, "Created booking should not be null");
    assertNotNull(createdBooking.getId(), "Booking ID should be assigned");
    assertNotNull(createdBooking.getBookingReference(), "Booking reference should be generated");
    assertEquals("AA123", createdBooking.getFlightNumber(), "Flight number should match");
    assertEquals("JFK", createdBooking.getOrigin(), "Origin should match");
    assertEquals("LAX", createdBooking.getDestination(), "Destination should match");
    assertEquals(user.getId(), createdBooking.getUser().getId(), "User ID should match");

    // Verify interactions
    verify(bookingRecordRepository).save(any(BookingRecord.class));
  }
}
