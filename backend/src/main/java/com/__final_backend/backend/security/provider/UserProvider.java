package com.__final_backend.backend.security.provider;

import com.__final_backend.backend.entity.User;

import java.util.List;
import java.util.Optional;

/**
 * Interface defining operations for user data providers.
 * Implementations can fetch user data from different sources (database, XML,
 * etc.)
 */
public interface UserProvider {

  /**
   * Find a user by their username
   * 
   * @param username the username to search for
   * @return an Optional containing the user if found
   */
  Optional<User> findByUsername(String username);

  /**
   * Find a user by their email address
   * 
   * @param email the email to search for
   * @return an Optional containing the user if found
   */
  Optional<User> findByEmail(String email);

  /**
   * Check if a username already exists
   * 
   * @param username the username to check
   * @return true if the username exists, false otherwise
   */
  boolean existsByUsername(String username);

  /**
   * Check if an email address already exists
   * 
   * @param email the email to check
   * @return true if the email exists, false otherwise
   */
  boolean existsByEmail(String email);

  /**
   * Get user by ID
   * 
   * @param id the user ID
   * @return an Optional containing the user if found
   */
  Optional<User> findById(Long id);

  /**
   * Get all users
   * 
   * @return a list of all users
   */
  List<User> findAll();

  /**
   * Save a user
   * 
   * @param user the user to save
   * @return the saved user
   */
  User save(User user);

  /**
   * Delete a user by ID
   * 
   * @param id the ID of the user to delete
   */
  void deleteById(Long id);
}
