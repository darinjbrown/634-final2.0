package com.__final_backend.backend.service;

import com.__final_backend.backend.entity.User;

import java.util.Optional;

/**
 * Service interface for user authentication operations
 */
public interface AuthService {

  /**
   * Register a new user with the system
   * 
   * @param username  the username
   * @param email     the email address
   * @param password  the password (will be encrypted)
   * @param firstName the first name
   * @param lastName  the last name
   * @return the registered user
   * @throws IllegalArgumentException if username or email already exists
   */
  User register(String username, String email, String password, String firstName, String lastName);

  /**
   * Authenticate a user with username/email and password
   * 
   * @param usernameOrEmail the username or email
   * @param password        the password
   * @return an Optional containing the user if authentication is successful
   */
  Optional<User> authenticate(String usernameOrEmail, String password);

  /**
   * Get the currently authenticated user
   * 
   * @return an Optional containing the current user if authenticated
   */
  Optional<User> getCurrentUser();

  /**
   * Check if the provided password matches the stored password for a user
   * 
   * @param rawPassword     the raw password to check
   * @param encodedPassword the encoded password stored in the database
   * @return true if the password matches, false otherwise
   */
  boolean matchesPassword(String rawPassword, String encodedPassword);

  /**
   * Generate a remember-me token for a user
   * 
   * @param user the user
   * @return the remember-me token
   */
  String generateRememberMeToken(User user);

  /**
   * Validate a remember-me token
   * 
   * @param token the token to validate
   * @return an Optional containing the user if the token is valid
   */
  Optional<User> validateRememberMeToken(String token);

  /**
   * Add a role to a user
   * 
   * @param user the user
   * @param role the role to add
   * @return the updated user
   */
  User addRole(User user, String role);

  /**
   * Remove a role from a user
   * 
   * @param user the user
   * @param role the role to remove
   * @return the updated user
   */
  User removeRole(User user, String role);

  /**
   * Check if the current user has a specific role
   * 
   * @param role the role to check
   * @return true if the current user has the role, false otherwise
   */
  boolean hasRole(String role);
}