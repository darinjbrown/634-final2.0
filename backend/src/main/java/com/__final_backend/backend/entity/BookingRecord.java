package com.__final_backend.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity representing a flight booking record.
 * Maps to the 'booking_records' table in the database.
 */
@Entity
@Table(name = "booking_records")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingRecord {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User user;

  @Column(name = "booking_reference", nullable = false, unique = true, length = 20)
  private String bookingReference;

  @Column(name = "airline_code", nullable = false, length = 3)
  private String airlineCode;

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

  @Column(name = "passenger_count", nullable = false)
  private Integer passengerCount;

  @Column(name = "total_price", nullable = false, precision = 10, scale = 2)
  private BigDecimal totalPrice;

  @Column(name = "booking_status", nullable = false, length = 20)
  private String bookingStatus = "CONFIRMED";

  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @PrePersist
  protected void onCreate() {
    this.createdAt = LocalDateTime.now();
  }
}