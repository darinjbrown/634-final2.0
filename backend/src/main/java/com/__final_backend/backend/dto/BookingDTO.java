package com.__final_backend.backend.dto;

import com.__final_backend.backend.entity.BookingRecord;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for flight booking records.
 * <p>
 * This class represents flight booking information transferred between the API
 * controllers
 * and the service layer in the SkyExplorer application. It includes all details
 * needed for
 * rendering booking information in the UI and processing booking operations.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingDTO {
  /** Unique identifier for the booking record. */
  private Long id;

  /**
   * Unique reference code for the booking (e.g., alphanumeric confirmation code).
   */
  private String bookingReference;

  /** ID of the user who made this booking. */
  private Long userId;

  /** Username of the user who made this booking. */
  private String username;

  /** External identifier for the flight in the flight provider's system. */
  private String flightId;

  /** IATA code of the departure airport (e.g., "JFK"). */
  private String departureAirport;

  /** IATA code of the arrival airport (e.g., "LAX"). */
  private String arrivalAirport;

  /** Scheduled departure date and time of the flight. */
  private LocalDateTime departureTime;

  /** Scheduled arrival date and time of the flight. */
  private LocalDateTime arrivalTime;

  /** Name or code of the airline operating the flight. */
  private String airline;

  /** Flight number or identifier (e.g., "AA123"). */
  private String flightNumber;
  /** Number of passengers included in this booking. */
  private Integer passengerCount;

  /** Total price of the booking (including all passengers and fees). */
  private BigDecimal totalPrice;

  /** Current status of the booking (e.g., "CONFIRMED", "CANCELLED"). */
  private String bookingStatus;

  /** Date and time when the booking was created. */
  private LocalDateTime bookingDate;

  /** Email address provided for contact purposes. */
  private String contactEmail;

  /** Phone number provided for contact purposes. */
  private String contactPhone;

  /** Method used for payment (e.g., "CREDIT_CARD", "PAYPAL"). */
  private String paymentMethod;

  /** Any additional information or special requests for the booking. */
  private String additionalNotes;

  /**
   * Converts a BookingRecord entity to a BookingDTO.
   * <p>
   * This utility method maps fields from the database entity to the DTO,
   * handling any field name differences and transformations as needed. Note that
   * some fields in the DTO (contactEmail, contactPhone, paymentMethod,
   * additionalNotes,
   * flightId) don't have direct counterparts in the entity.
   * </p>
   *
   * @param record the BookingRecord entity to convert
   * @return a new BookingDTO populated with data from the entity
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
    dto.setBookingDate(record.getCreatedAt()); // Note: Fields like contactEmail, contactPhone, paymentMethod,
                                               // additionalNotes,
    // and flightId are not stored in the entity. If needed, these should be added
    // to the entity or stored elsewhere.

    return dto;
  }
}