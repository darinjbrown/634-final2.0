package com.__final_backend.backend.security.provider;

import com.__final_backend.backend.entity.User;

import java.util.List;
import java.util.Optional;

/**
 * Interface defining operations for user data providers.
 * <p>
 * This interface abstracts the user data access layer, allowing the application
 * to work with
 * different data sources through a unified API. Implementations can fetch user
 * data from
 * different sources such as databases, XML files, or external services.
 * <p>
 * Each method in this interface represents a core operation related to user
 * management,
 * including finding, creating, updating, and deleting user records.
 */
public interface UserProvider {
  /**
   * Finds a user by their username.
   * <p>
   * This method is primarily used during authentication processes to locate
   * a user record based on the provided username.
   * 
   * @param username the username to search for (case-sensitive)
   * @return an Optional containing the user if found, or empty if no matching
   *         user exists
   */
  Optional<User> findByUsername(String username);

  /**
   * Finds a user by their email address.
   * <p>
   * This method is commonly used for password recovery workflows and
   * alternative authentication methods.
   * 
   * @param email the email address to search for (e.g., "user@example.com")
   * @return an Optional containing the user if found, or empty if no matching
   *         user exists
   */
  Optional<User> findByEmail(String email);

  /**
   * Checks if a username already exists.
   * <p>
   * This method is typically used during user registration to ensure
   * username uniqueness before creating a new user account.
   * 
   * @param username the username to check for existence
   * @return true if the username exists, false otherwise
   */
  boolean existsByUsername(String username);

  /**
   * Checks if an email address already exists.
   * <p>
   * This method is typically used during user registration to ensure
   * email uniqueness before creating a new user account.
   * 
   * @param email the email address to check for existence
   * @return true if the email exists, false otherwise
   */
  boolean existsByEmail(String email);

  /**
   * Finds a user by their unique ID.
   * <p>
   * This method is commonly used when working with user-specific data
   * and operations that require validated user identity.
   * 
   * @param id the unique identifier of the user to find
   * @return an Optional containing the user if found, or empty if no user exists
   *         with the given ID
   */
  Optional<User> findById(Long id);

  /**
   * Retrieves all users from the data source.
   * <p>
   * This method is typically used for administrative purposes such as user
   * management
   * interfaces or reporting. Be cautious when using this method with large
   * datasets,
   * as it may return a significant number of records.
   * 
   * @return a List containing all User objects, which may be empty but never null
   */
  List<User> findAll();

  /**
   * Saves or updates a user in the data source.
   * <p>
   * This method handles both creating new users and updating existing ones:
   * <ul>
   * <li>If the user has no ID or ID is 0, it's treated as a new user</li>
   * <li>If the user has an ID that exists in the data source, the record is
   * updated</li>
   * <li>If the user has an ID that doesn't exist, implementation behavior may
   * vary</li>
   * </ul>
   * 
   * @param user the User object to save or update
   * @return the saved User object, with generated ID and any other
   *         system-populated fields
   */
  User save(User user);

  /**
   * Deletes a user by their unique ID.
   * <p>
   * This method permanently removes a user record from the data source.
   * Implementations should consider handling related data accordingly,
   * possibly through cascading deletes or data archiving.
   * <p>
   * If no user with the specified ID exists, implementations typically
   * complete silently without error.
   * 
   * @param id the unique identifier of the user to delete
   */
  void deleteById(Long id);
}
