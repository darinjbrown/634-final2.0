package com.__final_backend.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entity representing a flight search performed by a user.
 * Maps to the 'flight_searches' table in the database.
 */
@Entity
@Table(name = "flight_searches")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlightSearch {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User user;

  @Column(nullable = false, length = 3)
  private String origin;

  @Column(nullable = false, length = 3)
  private String destination;

  @Column(name = "departure_date", nullable = false)
  private LocalDate departureDate;

  @Column(name = "return_date")
  private LocalDate returnDate;

  @Column(name = "number_of_travelers", nullable = false)
  private Integer numberOfTravelers;

  @Column(name = "trip_type", nullable = false, length = 20)
  private String tripType;

  @Column(name = "search_time", nullable = false)
  private LocalDateTime searchTime;

  @PrePersist
  protected void onCreate() {
    this.searchTime = LocalDateTime.now();
  }
}