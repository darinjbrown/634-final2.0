package com.__final_backend.backend.service.db;

import com.__final_backend.backend.entity.User;
import java.util.List;
import java.util.Optional;

/**
 * Service interface for User entity operations
 */
public interface UserService {

  /**
   * Create a new user
   * 
   * @param user the user to create
   * @return the created user
   */
  User createUser(User user);

  /**
   * Update an existing user
   * 
   * @param user the user with updated information
   * @return the updated user
   */
  User updateUser(User user);

  /**
   * Find a user by ID
   * 
   * @param id the user ID
   * @return an Optional containing the user if found
   */
  Optional<User> getUserById(Long id);

  /**
   * Find a user by username
   * 
   * @param username the username to search for
   * @return an Optional containing the user if found
   */
  Optional<User> getUserByUsername(String username);

  /**
   * Find a user by email
   * 
   * @param email the email to search for
   * @return an Optional containing the user if found
   */
  Optional<User> getUserByEmail(String email);

  /**
   * Get all users
   * 
   * @return a list of all users
   */
  List<User> getAllUsers();

  /**
   * Delete a user by ID
   * 
   * @param id the ID of the user to delete
   */
  void deleteUserById(Long id);

  /**
   * Check if a username already exists
   * 
   * @param username the username to check
   * @return true if the username exists, false otherwise
   */
  boolean existsByUsername(String username);

  /**
   * Check if an email already exists
   * 
   * @param email the email to check
   * @return true if the email exists, false otherwise
   */
  boolean existsByEmail(String email);
}