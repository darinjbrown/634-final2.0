package com.__final_backend.backend.dto;

import com.__final_backend.backend.entity.BookingRecord;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for BookingRecord
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingDTO {
  private Long id;
  private String bookingReference;
  private Long userId;
  private String username;
  private String flightId;
  private String departureAirport;
  private String arrivalAirport;
  private LocalDateTime departureTime;
  private LocalDateTime arrivalTime;
  private String airline;
  private String flightNumber;
  private Integer passengerCount;
  private BigDecimal totalPrice;
  private String bookingStatus;
  private LocalDateTime bookingDate;
  private String contactEmail;
  private String contactPhone;
  private String paymentMethod;
  private String additionalNotes;

  /**
   * Convert a BookingRecord entity to a BookingDTO
   *
   * @param record The BookingRecord entity
   * @return BookingDTO
   */
  public static BookingDTO fromEntity(BookingRecord record) {
    BookingDTO dto = new BookingDTO();
    dto.setId(record.getId());
    dto.setBookingReference(record.getBookingReference());
    dto.setUserId(record.getUser().getId());
    dto.setUsername(record.getUser().getUsername());

    // Map entity fields to DTO fields with name differences
    dto.setDepartureAirport(record.getOrigin());
    dto.setArrivalAirport(record.getDestination());
    dto.setDepartureTime(record.getDepartureTime());
    dto.setArrivalTime(record.getArrivalTime());
    dto.setAirline(record.getAirlineCode());
    dto.setFlightNumber(record.getFlightNumber());
    dto.setPassengerCount(record.getPassengerCount());
    dto.setTotalPrice(record.getTotalPrice());
    dto.setBookingStatus(record.getBookingStatus());
    dto.setBookingDate(record.getCreatedAt());

    // Note: Fields like contactEmail, contactPhone, paymentMethod, additionalNotes,
    // and flightId
    // are not stored in the entity. If needed, these should be added to the entity
    // or stored elsewhere.

    return dto;
  }
}