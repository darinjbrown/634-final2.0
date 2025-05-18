package com.__final_backend.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Entity representing a user in the SkyExplorer application.
 * <p>
 * Maps to the 'users' table in the database and contains user account
 * information,
 * including credentials, personal details, and associated roles. This entity
 * also
 * maintains relationships with flight searches, saved flights, and bookings
 * made by the user.
 * </p>
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
  /** Unique identifier for the user. */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /** Unique username for the user account, limited to 50 characters. */
  @Column(nullable = false, unique = true, length = 50)
  private String username;

  /** Unique email address for the user account, limited to 100 characters. */
  @Column(nullable = false, unique = true, length = 100)
  private String email;

  /** Hashed password value for secure credential storage. */
  @Column(name = "password_hash", nullable = false)
  private String passwordHash;
  /** User's first name, limited to 50 characters. */
  @Column(name = "first_name", length = 50)
  private String firstName;

  /** User's last name, limited to 50 characters. */
  @Column(name = "last_name", length = 50)
  private String lastName;

  /**
   * Collection of security roles assigned to the user.
   * Stored in a separate 'user_roles' table with eager fetching to optimize
   * authorization checks.
   */
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
  @Column(name = "role")
  private Set<String> roles = new HashSet<>();

  /**
   * Timestamp when the user account was created.
   * Automatically set during entity creation and cannot be updated.
   */
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  /**
   * Timestamp when the user account was last updated.
   * Automatically updated whenever the entity is modified.
   */
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;
  /**
   * Flight searches performed by this user.
   * Bidirectional relationship with cascade operations and orphan removal for
   * data integrity.
   */
  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<FlightSearch> flightSearches;

  /**
   * Flights saved by this user for future reference.
   * Bidirectional relationship with cascade operations and orphan removal for
   * data integrity.
   */
  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<SavedFlight> savedFlights;

  /**
   * Booking records associated with this user.
   * Bidirectional relationship with cascade operations and orphan removal for
   * data integrity.
   */
  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<BookingRecord> bookingRecords;

  /**
   * Adds a role to the user's set of roles.
   * <p>
   * This method adds the specified role to the user's permissions. The role is
   * typically
   * a string constant like "USER", "ADMIN", etc.
   * </p>
   * 
   * @param role the role to add to the user's permissions
   */
  public void addRole(String role) {
    roles.add(role);
  }

  /**
   * Checks if the user has a specific role.
   * <p>
   * This method verifies whether the user has been granted a particular
   * permission role.
   * Used for authorization checks throughout the application.
   * </p>
   * 
   * @param role the role to check for
   * @return true if the user has the specified role, false otherwise
   */
  public boolean hasRole(String role) {
    return roles.contains(role);
  }

  /**
   * Sets creation and update timestamps before the entity is persisted.
   * Automatically called by JPA during entity creation.
   */
  @PrePersist
  protected void onCreate() {
    this.createdAt = LocalDateTime.now();
    this.updatedAt = LocalDateTime.now();
  }

  /**
   * Updates the last modified timestamp before the entity is updated.
   * Automatically called by JPA during entity updates.
   */
  @PreUpdate
  protected void onUpdate() {
    this.updatedAt = LocalDateTime.now();
  }
}