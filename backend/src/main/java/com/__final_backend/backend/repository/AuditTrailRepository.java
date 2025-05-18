package com.__final_backend.backend.repository;

import com.__final_backend.backend.entity.AuditTrail;
import com.__final_backend.backend.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for AuditTrail entity operations.
 * <p>
 * Provides methods for managing audit trail records in the database, including
 * CRUD operations
 * inherited from JpaRepository and custom query methods for retrieving audit
 * logs based on
 * various criteria such as user, action type, entity type, and time ranges.
 * <p>
 * The AuditTrail entity tracks system events and user actions for compliance,
 * security
 * monitoring, and troubleshooting purposes.
 */
@Repository
public interface AuditTrailRepository extends JpaRepository<AuditTrail, Long> {
  /**
   * Finds all audit trail entries for a specific user.
   * <p>
   * This method retrieves the complete audit history for a particular user,
   * ordered by the
   * default repository ordering (typically by timestamp). Use this method when
   * you need to
   * review all activities performed by a specific user for security analysis or
   * user
   * activity reporting.
   *
   * @param user the user whose audit trail entries to find
   * @return a list of all audit trail entries associated with the specified user,
   *         which may be
   *         empty if no audit records exist for the user
   */
  List<AuditTrail> findByUser(User user);

  /**
   * Finds all audit trail entries for a specific user with pagination support.
   * <p>
   * This method retrieves audit records for a user with support for pagination,
   * sorting,
   * and filtering. It's particularly useful for displaying audit logs in an
   * administrative
   * interface or when dealing with users who have extensive audit histories.
   *
   * @param user     the user whose audit trail entries to find
   * @param pageable pagination and sorting information (page number, page size,
   *                 sort criteria)
   * @return a page of audit trail entries associated with the specified user,
   *         which may be empty
   *         if no audit records exist for the user or the pageable parameters go
   *         beyond
   *         available results
   */
  Page<AuditTrail> findByUser(User user, Pageable pageable);

  /**
   * Finds audit trail entries by action type.
   * <p>
   * This method retrieves all audit records for a specific type of action,
   * regardless of which
   * users performed them. Useful for monitoring particular system activities,
   * such as login
   * attempts, data modifications, or administrative actions.
   *
   * @param actionType the action type to search for (e.g., "LOGIN", "CREATE",
   *                   "DELETE", "UPDATE")
   * @return a list of audit trail entries for the specified action type, which
   *         may be empty if
   *         no audit records match the criteria
   */
  List<AuditTrail> findByActionType(String actionType);

  /**
   * Finds audit trail entries by entity type.
   * <p>
   * This method retrieves all audit records related to a specific type of entity,
   * regardless
   * of which actions were performed or which users performed them. Useful for
   * tracking all
   * operations performed on a particular resource type, such as users, bookings,
   * or flights.
   *
   * @param entityType the entity type to search for (e.g., "USER", "BOOKING",
   *                   "FLIGHT")
   * @return a list of audit trail entries for the specified entity type, which
   *         may be empty if
   *         no audit records match the criteria
   */
  List<AuditTrail> findByEntityType(String entityType);

  /**
   * Finds audit trail entries by entity ID.
   * <p>
   * This method retrieves all audit records related to a specific entity
   * instance, identified by
   * its ID. Useful for tracking the complete history of a particular record, such
   * as all actions
   * performed on a specific booking, user account, or flight.
   *
   * @param entityId the unique identifier of the entity instance to search for
   * @return a list of audit trail entries for the specified entity ID, which may
   *         be empty if
   *         no audit records match the criteria
   */
  List<AuditTrail> findByEntityId(Long entityId);

  /**
   * Finds audit trail entries by timestamp range.
   * <p>
   * This method retrieves all audit records created within a specific time
   * window, regardless
   * of users, actions, or entities involved. Useful for investigating system
   * activities during
   * a particular period, such as during a security incident, after a system
   * upgrade, or for
   * generating periodic audit reports.
   *
   * @param startTime the start of the time range (inclusive)
   * @param endTime   the end of the time range (inclusive)
   * @return a list of audit trail entries with timestamps within the specified
   *         range, which may
   *         be empty if no audit records match the criteria
   */
  List<AuditTrail> findByTimestampBetween(LocalDateTime startTime, LocalDateTime endTime);

  /**
   * Finds audit trail entries by user and action type.
   * <p>
   * This method combines user-specific filtering with action type filtering,
   * retrieving all
   * audit records of a particular action performed by a specific user. Useful for
   * targeted
   * security investigations, such as tracking login attempts by a specific user
   * or monitoring
   * administrative actions performed by particular staff members.
   *
   * @param user       the user whose audit trail entries to find
   * @param actionType the action type to search for (e.g., "LOGIN", "CREATE",
   *                   "DELETE", "UPDATE")
   * @return a list of audit trail entries matching the specified user and action
   *         type, which may
   *         be empty if no audit records match the criteria
   */
  List<AuditTrail> findByUserAndActionType(User user, String actionType);
}