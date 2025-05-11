package com.__final_backend.backend.controller.db;

import com.__final_backend.backend.entity.User;
import com.__final_backend.backend.service.AuthService;
import com.__final_backend.backend.service.db.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for user-related operations
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

  private final UserService userService;
  private final AuthService authService;

  @Autowired
  public UserController(UserService userService, AuthService authService) {
    this.userService = userService;
    this.authService = authService;
  }

  /**
   * Create a new user (admin only)
   */
  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<User> createUser(@RequestBody User user) {
    if (userService.existsByUsername(user.getUsername())) {
      return new ResponseEntity<>(HttpStatus.CONFLICT);
    }
    if (userService.existsByEmail(user.getEmail())) {
      return new ResponseEntity<>(HttpStatus.CONFLICT);
    }
    User createdUser = userService.createUser(user);
    return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
  }

  /**
   * Get user by ID (admin or the user themselves)
   */
  @GetMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN') or @authService.getCurrentUser().isPresent() and @authService.getCurrentUser().get().getId() == #id")
  public ResponseEntity<User> getUserById(@PathVariable Long id) {
    Optional<User> user = userService.getUserById(id);
    return user.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
        .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  /**
   * Get all users (admin only)
   */
  @GetMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<List<User>> getAllUsers() {
    List<User> users = userService.getAllUsers();
    return new ResponseEntity<>(users, HttpStatus.OK);
  }

  /**
   * Get user by username (admin or the user themselves)
   */
  @GetMapping("/username/{username}")
  @PreAuthorize("hasRole('ADMIN') or @authService.getCurrentUser().isPresent() and @authService.getCurrentUser().get().getUsername().equals(#username)")
  public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
    Optional<User> user = userService.getUserByUsername(username);
    return user.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
        .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  /**
   * Get user by email (admin or the user themselves)
   */
  @GetMapping("/email/{email}")
  @PreAuthorize("hasRole('ADMIN') or @authService.getCurrentUser().isPresent() and @authService.getCurrentUser().get().getEmail().equals(#email)")
  public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
    Optional<User> user = userService.getUserByEmail(email);
    return user.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
        .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  /**
   * Update a user (admin or the user themselves)
   */
  @PutMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN') or @authService.getCurrentUser().isPresent() and @authService.getCurrentUser().get().getId() == #id")
  public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user) {
    if (!userService.getUserById(id).isPresent()) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    user.setId(id);
    User updatedUser = userService.updateUser(user);
    return new ResponseEntity<>(updatedUser, HttpStatus.OK);
  }

  /**
   * Delete a user (admin only)
   */
  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
    if (!userService.getUserById(id).isPresent()) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    userService.deleteUserById(id);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  /**
   * Check if a username exists (public)
   */
  @GetMapping("/exists/username/{username}")
  public ResponseEntity<Boolean> existsByUsername(@PathVariable String username) {
    boolean exists = userService.existsByUsername(username);
    return new ResponseEntity<>(exists, HttpStatus.OK);
  }

  /**
   * Check if an email exists (public)
   */
  @GetMapping("/exists/email/{email}")
  public ResponseEntity<Boolean> existsByEmail(@PathVariable String email) {
    boolean exists = userService.existsByEmail(email);
    return new ResponseEntity<>(exists, HttpStatus.OK);
  }
}