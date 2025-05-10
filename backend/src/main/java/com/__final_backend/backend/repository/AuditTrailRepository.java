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
 * Repository interface for AuditTrail entity operations
 */
@Repository
public interface AuditTrailRepository extends JpaRepository<AuditTrail, Long> {

  /**
   * Find all audit trail entries for a specific user
   * 
   * @param user the user whose audit trail entries to find
   * @return a list of audit trail entries
   */
  List<AuditTrail> findByUser(User user);

  /**
   * Find all audit trail entries for a specific user with pagination
   * 
   * @param user     the user whose audit trail entries to find
   * @param pageable pagination information
   * @return a page of audit trail entries
   */
  Page<AuditTrail> findByUser(User user, Pageable pageable);

  /**
   * Find audit trail entries by action type
   * 
   * @param actionType the action type to search for
   * @return a list of audit trail entries
   */
  List<AuditTrail> findByActionType(String actionType);

  /**
   * Find audit trail entries by entity type
   * 
   * @param entityType the entity type to search for
   * @return a list of audit trail entries
   */
  List<AuditTrail> findByEntityType(String entityType);

  /**
   * Find audit trail entries by entity ID
   * 
   * @param entityId the entity ID to search for
   * @return a list of audit trail entries
   */
  List<AuditTrail> findByEntityId(Long entityId);

  /**
   * Find audit trail entries by timestamp range
   * 
   * @param startTime the start of the time range
   * @param endTime   the end of the time range
   * @return a list of audit trail entries
   */
  List<AuditTrail> findByTimestampBetween(LocalDateTime startTime, LocalDateTime endTime);

  /**
   * Find audit trail entries by user and action type
   * 
   * @param user       the user whose audit trail entries to find
   * @param actionType the action type to search for
   * @return a list of audit trail entries
   */
  List<AuditTrail> findByUserAndActionType(User user, String actionType);
}