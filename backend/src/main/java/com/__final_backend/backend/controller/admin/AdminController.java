package com.__final_backend.backend.controller.admin;

import com.__final_backend.backend.entity.User;
import com.__final_backend.backend.service.AuthService;
import com.__final_backend.backend.service.db.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Controller for administrative operations.
 * All endpoints in this controller require ADMIN role.
 */
@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

  private final UserService userService;
  private final AuthService authService;

  @Autowired
  public AdminController(UserService userService, AuthService authService) {
    this.userService = userService;
    this.authService = authService;
  }

  /**
   * Grant the ADMIN role to a user
   * 
   * @param userId the ID of the user to promote
   * @return response with success message or error
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
   * Remove the ADMIN role from a user
   * 
   * @param userId the ID of the user to demote
   * @return response with success message or error
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
   * Add a role to a user
   * 
   * @param userId the ID of the user
   * @param role   the role to add
   * @return response with success message or error
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
   * Remove a role from a user
   * 
   * @param userId the ID of the user
   * @param role   the role to remove
   * @return response with success message or error
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