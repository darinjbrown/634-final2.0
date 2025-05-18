package com.__final_backend.backend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Data Transfer Object (DTO) for SavedFlight entity used in API requests and
 * responses.
 * <p>
 * This class represents flight information that users can save for future
 * reference,
 * containing details such as airline information, flight numbers,
 * departure/arrival times,
 * and pricing data.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SavedFlightDTO {
  /** Unique identifier for the saved flight record. */
  private Long id;

  /** IATA or ICAO airline code (e.g., "AA" for American Airlines). */
  private String airlineCode;

  /** Full name of the airline (e.g., "American Airlines"). */
  private String airlineName;

  /** Flight number or identifier (e.g., "AA123"). */
  private String flightNumber;

  /** IATA code for the origin airport (e.g., "JFK" for John F. Kennedy). */
  private String origin;

  /** IATA code for the destination airport (e.g., "LAX" for Los Angeles). */
  private String destination;
  /**
   * Scheduled departure time in ISO format.
   * Formatted as yyyy-MM-dd'T'HH:mm:ss for JSON serialization/deserialization.
   */
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime departureTime;

  /**
   * Scheduled arrival time in ISO format.
   * Formatted as yyyy-MM-dd'T'HH:mm:ss for JSON serialization/deserialization.
   */
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime arrivalTime;

  /** Price of the flight ticket. */
  private BigDecimal price;

  /** Currency code for the price (default is "USD"). */
  private String currency = "USD";
}