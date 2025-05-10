package com.__final_backend.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity representing a flight that a user has saved for later reference.
 * Maps to the 'saved_flights' table in the database.
 */
@Entity
@Table(name = "saved_flights")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SavedFlight {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(name = "airline_code", nullable = false, length = 3)
  private String airlineCode;

  @Column(name = "airline_name", length = 100)
  private String airlineName;

  @Column(name = "flight_number", nullable = false, length = 10)
  private String flightNumber;

  @Column(nullable = false, length = 3)
  private String origin;

  @Column(nullable = false, length = 3)
  private String destination;

  @Column(name = "departure_time", nullable = false)
  private LocalDateTime departureTime;

  @Column(name = "arrival_time", nullable = false)
  private LocalDateTime arrivalTime;

  @Column(nullable = false, precision = 10, scale = 2)
  private BigDecimal price;

  @Column(length = 3)
  private String currency = "USD";

  @Column(name = "saved_at")
  private LocalDateTime savedAt;

  @PrePersist
  protected void onCreate() {
    this.savedAt = LocalDateTime.now();
  }
}