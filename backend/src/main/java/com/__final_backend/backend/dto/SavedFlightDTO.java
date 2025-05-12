package com.__final_backend.backend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for SavedFlight entity used in API requests and responses
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SavedFlightDTO {

  private Long id;

  private String airlineCode;

  private String airlineName;

  private String flightNumber;

  private String origin;

  private String destination;

  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime departureTime;

  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime arrivalTime;

  private BigDecimal price;

  private String currency = "USD";
}