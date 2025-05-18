package com.__final_backend.backend.controller.db;

import com.__final_backend.backend.entity.User;
import com.__final_backend.backend.service.AuthService;
import com.__final_backend.backend.service.db.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for user-related operations.
 *
 * <p>
 * This controller provides endpoints for managing user accounts, including
 * creation,
 * retrieval, updating, and deletion. Access to these operations is restricted
 * based on
 * user roles and ownership of the data being accessed.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {
  private final UserService userService;
  private final AuthService authService;

  /**
   * Constructs a new UserController with required dependencies.
   *
   * @param userService service for user management operations
   * @param authService service for authentication operations
   */
  public UserController(UserService userService, AuthService authService) {
    this.userService = userService;
    this.authService = authService;
  }

  /**
   * Creates a new user account.
   *
   * <p>
   * This endpoint is restricted to administrators only. It validates that the
   * username
   * and email are not already in use before creating the new user account.
   *
   * @param user the user data for account creation
   * @return the created user with HTTP 201 Created, or HTTP 409 Conflict if
   *         username/email exists
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
   * Retrieves a user by their ID.
   *
   * <p>
   * This endpoint is accessible by administrators or the user themselves.
   * The authorization is handled through a SpEL expression that checks if the
   * current user's ID matches the requested ID.
   *
   * @param id the ID of the user to retrieve
   * @return the user with HTTP 200 OK, or HTTP 404 Not Found if no user exists
   *         with that ID
   */
  @GetMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN') or @authService.getCurrentUser().isPresent() and @authService.getCurrentUser().get().getId() == #id")
  public ResponseEntity<User> getUserById(@PathVariable Long id) {
    Optional<User> user = userService.getUserById(id);
    return user.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
        .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  /**
   * Retrieves all user accounts in the system.
   *
   * <p>
   * This endpoint is restricted to administrators only.
   *
   * @return a list of all users with HTTP 200 OK
   */
  @GetMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<List<User>> getAllUsers() {
    List<User> users = userService.getAllUsers();
    return new ResponseEntity<>(users, HttpStatus.OK);
  }

  /**
   * Retrieves a user by their username.
   *
   * <p>
   * This endpoint is accessible by administrators or the user themselves.
   * Authorization is handled through a SpEL expression that checks if the
   * current user's username matches the requested username.
   *
   * @param username the username of the user to retrieve
   * @return the user with HTTP 200 OK, or HTTP 404 Not Found if no user exists
   *         with that username
   */
  @GetMapping("/username/{username}")
  @PreAuthorize("hasRole('ADMIN') or @authService.getCurrentUser().isPresent() and @authService.getCurrentUser().get().getUsername().equals(#username)")
  public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
    Optional<User> user = userService.getUserByUsername(username);
    return user.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
        .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  /**
   * Retrieves a user by their email address.
   *
   * <p>
   * This endpoint is accessible by administrators or the user themselves.
   * Authorization is handled through a SpEL expression that checks if the
   * current user's email matches the requested email.
   *
   * @param email the email address of the user to retrieve
   * @return the user with HTTP 200 OK, or HTTP 404 Not Found if no user exists
   *         with that email
   */
  @GetMapping("/email/{email}")
  @PreAuthorize("hasRole('ADMIN') or @authService.getCurrentUser().isPresent() and @authService.getCurrentUser().get().getEmail().equals(#email)")
  public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
    Optional<User> user = userService.getUserByEmail(email);
    return user.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
        .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  /**
   * Updates an existing user account.
   *
   * <p>
   * This endpoint is accessible by administrators or the user themselves.
   * Authorization is handled through a SpEL expression that checks if the
   * current user's ID matches the ID of the user being updated.
   *
   * @param id   the ID of the user to update
   * @param user the updated user data
   * @return the updated user with HTTP 200 OK, or HTTP 404 Not Found if no user
   *         exists with that ID
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
   * Deletes a user account.
   *
   * <p>
   * This endpoint is restricted to administrators only. It verifies that the
   * user exists before attempting to delete them.
   *
   * @param id the ID of the user to delete
   * @return HTTP 204 No Content if successful, or HTTP 404 Not Found if no user
   *         exists with that ID
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
   * Checks if a username is already in use.
   *
   * <p>
   * This endpoint is publicly accessible and used primarily during
   * registration to provide immediate feedback on username availability.
   *
   * @param username the username to check
   * @return true if the username exists, false otherwise, with HTTP 200 OK
   */
  @GetMapping("/exists/username/{username}")
  public ResponseEntity<Boolean> existsByUsername(@PathVariable String username) {
    boolean exists = userService.existsByUsername(username);
    return new ResponseEntity<>(exists, HttpStatus.OK);
  }

  /**
   * Checks if an email address is already in use.
   *
   * <p>
   * This endpoint is publicly accessible and used primarily during
   * registration to provide immediate feedback on email availability.
   *
   * @param email the email address to check
   * @return true if the email exists, false otherwise, with HTTP 200 OK
   */
  @GetMapping("/exists/email/{email}")
  public ResponseEntity<Boolean> existsByEmail(@PathVariable String email) {
    boolean exists = userService.existsByEmail(email);
    return new ResponseEntity<>(exists, HttpStatus.OK);
  }
}