package com.__final_backend.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity representing a flight that a user has saved for later reference.
 * <p>
 * This entity stores flight information that users have saved to revisit later,
 * facilitating a convenient way for users to track interesting flight options
 * without needing to repeat their search. It includes all essential flight
 * details
 * such as airline information, departure and arrival times, origin,
 * destination,
 * and pricing information.
 * <p>
 * Maps to the 'saved_flights' table in the database.
 */
@Entity
@Table(name = "saved_flights")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SavedFlight {
  /**
   * Unique identifier for the saved flight.
   * <p>
   * Automatically generated by the database.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /**
   * The user who saved this flight.
   * <p>
   * Many-to-one relationship with the User entity. Each saved flight belongs
   * to exactly one user, while a user can have multiple saved flights.
   * This field is configured for lazy loading to optimize performance.
   */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  /**
   * IATA code of the airline operating this flight.
   * <p>
   * Three-letter identification code, e.g., "AAL" for American Airlines.
   */
  @Column(name = "airline_code", nullable = false, length = 3)
  private String airlineCode;

  /**
   * Full name of the airline operating this flight.
   * <p>
   * For example, "American Airlines" or "Delta Air Lines".
   */
  @Column(name = "airline_name", length = 100)
  private String airlineName;

  /**
   * Flight number or identifier.
   * <p>
   * Typically consists of airline code followed by numbers, e.g., "AA123".
   */
  @Column(name = "flight_number", nullable = false, length = 10)
  private String flightNumber;

  /**
   * IATA airport code for the departure location.
   * <p>
   * Three-letter code identifying the departure airport, e.g., "JFK" for John F.
   * Kennedy International Airport.
   */
  @Column(nullable = false, length = 3)
  private String origin;

  /**
   * IATA airport code for the arrival location.
   * <p>
   * Three-letter code identifying the arrival airport, e.g., "LAX" for Los
   * Angeles International Airport.
   */
  @Column(nullable = false, length = 3)
  private String destination;

  /**
   * Scheduled departure date and time of the flight.
   * <p>
   * Stored as a LocalDateTime value in the system's time zone.
   */
  @Column(name = "departure_time", nullable = false)
  private LocalDateTime departureTime;

  /**
   * Scheduled arrival date and time of the flight.
   * <p>
   * Stored as a LocalDateTime value in the system's time zone.
   */
  @Column(name = "arrival_time", nullable = false)
  private LocalDateTime arrivalTime;

  /**
   * The price of the flight.
   * <p>
   * Stored with precision of 10 digits and scale of 2 decimal places.
   */
  @Column(nullable = false, precision = 10, scale = 2)
  private BigDecimal price;

  /**
   * Currency code in which the price is expressed.
   * <p>
   * Three-letter ISO 4217 currency code, defaults to "USD" (United States
   * Dollar).
   */
  @Column(length = 3)
  private String currency = "USD";

  /**
   * Timestamp indicating when the flight was saved by the user.
   * <p>
   * Automatically set by the {@link #onCreate()} method during entity creation.
   */
  @Column(name = "saved_at")
  private LocalDateTime savedAt;

  /**
   * Lifecycle callback method executed before persisting the entity.
   * <p>
   * This method automatically sets the savedAt timestamp to the current date and
   * time
   * when a new SavedFlight entity is created, ensuring that each saved flight
   * record
   * tracks when it was initially saved by the user.
   */
  @PrePersist
  protected void onCreate() {
    this.savedAt = LocalDateTime.now();
  }
}