package com.__final_backend.backend.controller.admin;

import com.__final_backend.backend.entity.User;
import com.__final_backend.backend.service.AuthService;
import com.__final_backend.backend.service.db.UserService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Controller for administrative operations.
 * <p>
 * This controller provides endpoints for user role management, including
 * promoting users
 * to admin status, demoting admins, and adding or removing specific roles.
 * All endpoints in this controller require ADMIN role authentication.
 * </p>
 */
@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

  private final UserService userService;
  private final AuthService authService;

  public AdminController(UserService userService, AuthService authService) {
    this.userService = userService;
    this.authService = authService;
  }

  /**
   * Grants the ADMIN role to a user.
   * <p>
   * This endpoint allows promoting a regular user to administrator status by
   * adding
   * the ADMIN role to their account. If the user already has the ADMIN role,
   * the operation is idempotent and returns a message indicating the role already
   * exists.
   * </p>
   *
   * @param userId the ID of the user to promote
   * @return ResponseEntity containing a success message or error information
   */
  @PostMapping("/users/{userId}/promote")
  public ResponseEntity<?> promoteToAdmin(@PathVariable Long userId) {
    Optional<User> userOpt = userService.getUserById(userId);

    if (userOpt.isEmpty()) {
      return ResponseEntity.notFound().build();
    }

    User user = userOpt.get();

    // Check if already an admin
    if (user.hasRole("ADMIN")) {
      Map<String, String> response = new HashMap<>();
      response.put("message", "User already has ADMIN role");
      return ResponseEntity.ok(response);
    }

    // Add ADMIN role
    user = authService.addRole(user, "ADMIN");

    Map<String, String> response = new HashMap<>();
    response.put("message", "User promoted to ADMIN successfully");
    return ResponseEntity.ok(response);
  }

  /**
   * Removes the ADMIN role from a user.
   * <p>
   * This endpoint allows demoting an administrator by removing the ADMIN role
   * from their account.
   * If the user does not have the ADMIN role, the operation is idempotent and
   * returns
   * a message indicating the role was not present.
   * </p>
   *
   * @param userId the ID of the user to demote
   * @return ResponseEntity containing a success message or error information
   */
  @PostMapping("/users/{userId}/demote")
  public ResponseEntity<?> demoteFromAdmin(@PathVariable Long userId) {
    Optional<User> userOpt = userService.getUserById(userId);

    if (userOpt.isEmpty()) {
      return ResponseEntity.notFound().build();
    }

    User user = userOpt.get();

    // Check if user is an admin
    if (!user.hasRole("ADMIN")) {
      Map<String, String> response = new HashMap<>();
      response.put("message", "User does not have ADMIN role");
      return ResponseEntity.ok(response);
    }

    // Remove ADMIN role
    user = authService.removeRole(user, "ADMIN");

    Map<String, String> response = new HashMap<>();
    response.put("message", "ADMIN role removed from user successfully");
    return ResponseEntity.ok(response);
  }

  /**
   * Adds a specific role to a user.
   * <p>
   * This endpoint assigns a specified role to a user's account. If the user
   * already has the specified role, the operation is idempotent and returns
   * a message indicating the role already exists.
   * </p>
   *
   * @param userId the ID of the user
   * @param role   the role to add
   * @return ResponseEntity containing a success message or error information
   */
  @PostMapping("/users/{userId}/roles/{role}")
  public ResponseEntity<?> addRoleToUser(@PathVariable Long userId, @PathVariable String role) {
    Optional<User> userOpt = userService.getUserById(userId);

    if (userOpt.isEmpty()) {
      return ResponseEntity.notFound().build();
    }

    User user = userOpt.get();

    // Check if user already has the role
    if (user.hasRole(role)) {
      Map<String, String> response = new HashMap<>();
      response.put("message", "User already has the role: " + role);
      return ResponseEntity.ok(response);
    }

    // Add role
    user = authService.addRole(user, role);

    Map<String, String> response = new HashMap<>();
    response.put("message", "Role added to user successfully: " + role);
    return ResponseEntity.ok(response);
  }

  /**
   * Removes a specific role from a user.
   * <p>
   * This endpoint revokes a specified role from a user's account. If the user
   * does not have the specified role, the operation is idempotent and returns
   * a message indicating the role was not present.
   * </p>
   *
   * @param userId the ID of the user
   * @param role   the role to remove
   * @return ResponseEntity containing a success message or error information
   */
  @DeleteMapping("/users/{userId}/roles/{role}")
  public ResponseEntity<?> removeRoleFromUser(@PathVariable Long userId, @PathVariable String role) {
    Optional<User> userOpt = userService.getUserById(userId);

    if (userOpt.isEmpty()) {
      return ResponseEntity.notFound().build();
    }

    User user = userOpt.get();

    // Check if user has the role
    if (!user.hasRole(role)) {
      Map<String, String> response = new HashMap<>();
      response.put("message", "User does not have the role: " + role);
      return ResponseEntity.ok(response);
    }

    // Remove role
    user = authService.removeRole(user, role);

    Map<String, String> response = new HashMap<>();
    response.put("message", "Role removed from user successfully: " + role);
    return ResponseEntity.ok(response);
  }
}