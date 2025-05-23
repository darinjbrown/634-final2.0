package com.__final_backend.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity representing an audit trail entry for tracking important system
 * operations.
 * <p>
 * This entity records user activities and system events for security,
 * compliance,
 * and troubleshooting purposes. Each entry includes information about who
 * performed
 * an action, what action was taken, which entity was affected, and when it
 * occurred.
 * <p>
 * Maps to the 'audit_trail' table in the database.
 */
@Entity
@Table(name = "audit_trail")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditTrail {
  /**
   * Unique identifier for the audit trail entry.
   * <p>
   * Automatically generated by the database.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /**
   * The user who performed the action being audited.
   * <p>
   * Many-to-one relationship with the User entity. This can be null for
   * system-generated
   * events or actions performed by unauthenticated users. Configured for lazy
   * loading
   * to optimize performance.
   */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User user;
  /**
   * Type of action that was performed.
   * <p>
   * This categorizes the operation being audited, such as "CREATE", "UPDATE",
   * "DELETE",
   * "LOGIN", "LOGOUT", etc. Required field with a maximum length of 50
   * characters.
   */
  @Column(name = "action_type", nullable = false, length = 50)
  private String actionType;

  /**
   * Type of entity that was affected by the action.
   * <p>
   * Identifies the type of resource being operated on, such as "USER", "BOOKING",
   * "FLIGHT", etc. Required field with a maximum length of 50 characters.
   */
  @Column(name = "entity_type", nullable = false, length = 50)
  private String entityType;

  /**
   * Identifier of the specific entity instance that was affected.
   * <p>
   * This is typically the primary key of the affected entity. Can be null for
   * operations
   * that don't target a specific entity instance (e.g., system-wide actions).
   */
  @Column(name = "entity_id")
  private Long entityId;
  /**
   * Detailed description of the action that was performed.
   * <p>
   * Provides additional context about the action, including relevant details that
   * may
   * be useful for troubleshooting or investigation. This can be null if no
   * additional
   * information is needed beyond the action and entity types.
   */
  @Column
  private String description;

  /**
   * IP address from which the action was performed.
   * <p>
   * Captured for security monitoring and auditing purposes. Maximum length of 50
   * characters
   * to accommodate both IPv4 and IPv6 addresses. Can be null for system-generated
   * events.
   */
  @Column(name = "ip_address", length = 50)
  private String ipAddress;

  /**
   * Date and time when the action was performed.
   * <p>
   * Automatically set by the {@link #onCreate()} method during entity creation
   * and marked
   * as non-updatable to preserve an accurate historical record.
   */
  @Column(nullable = false, updatable = false)
  private LocalDateTime timestamp;

  /**
   * Lifecycle callback method executed before persisting the entity.
   * <p>
   * This method automatically sets the timestamp to the current date and time
   * when a new AuditTrail entity is created, ensuring an accurate record of
   * when the audited action occurred.
   */
  @PrePersist
  protected void onCreate() {
    this.timestamp = LocalDateTime.now();
  }
}