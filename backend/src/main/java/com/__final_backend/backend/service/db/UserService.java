package com.__final_backend.backend.service.db;

import com.__final_backend.backend.entity.User;
import java.util.List;
import java.util.Optional;

/**
 * Service interface defining operations for managing user data.
 * <p>
 * This interface provides a contract for user management operations including
 * creation,
 * retrieval, update, and deletion of user records. It separates the business
 * logic
 * from the actual data access implementation, supporting clean architecture
 * principles.
 * <p>
 * Implementations of this interface typically handle business rules,
 * validation,
 * and coordinate with data access components to persist and retrieve user
 * information.
 */
public interface UserService {
  /**
   * Creates a new user in the system.
   * <p>
   * This method handles the business logic for user creation, including any
   * validation rules.
   * The user object may be incomplete (e.g., missing ID) as the implementation
   * typically
   * generates and assigns any required system values.
   * 
   * @param user the user entity to create, containing at minimum the required
   *             fields
   * @return the created user with generated ID and any other system-populated
   *         fields
   */
  User createUser(User user);

  /**
   * Updates an existing user in the system.
   * <p>
   * This method applies changes to an existing user record identified by the ID
   * in the
   * provided user object. All fields in the user object will typically overwrite
   * the
   * corresponding fields in the existing record.
   * 
   * @param user the user entity with updated information and a valid ID
   * @return the updated user entity reflecting all changes
   */
  User updateUser(User user);

  /**
   * Retrieves a user by their unique identifier.
   * <p>
   * This method is typically used when working with specific user records that
   * have
   * been previously identified.
   * 
   * @param id the unique identifier of the user to retrieve
   * @return an Optional containing the user if found, or empty if no user exists
   *         with the given ID
   */
  Optional<User> getUserById(Long id);

  /**
   * Retrieves a user by their username.
   * <p>
   * This method is commonly used during authentication processes and profile
   * lookups.
   * The search is typically case-sensitive, matching exactly the username as
   * stored.
   * 
   * @param username the username to search for (case-sensitive)
   * @return an Optional containing the user if found, or empty if no matching
   *         user exists
   */
  Optional<User> getUserByUsername(String username);

  /**
   * Retrieves a user by their email address.
   * <p>
   * This method is commonly used during password recovery workflows and when
   * verifying
   * email uniqueness during registration. The search is typically
   * case-insensitive.
   * 
   * @param email the email address to search for (e.g., "user@example.com")
   * @return an Optional containing the user if found, or empty if no matching
   *         user exists
   */
  Optional<User> getUserByEmail(String email);

  /**
   * Retrieves all users in the system.
   * <p>
   * This method is typically used for administrative purposes such as user
   * management
   * interfaces or reporting. Be cautious when using this method with large
   * datasets,
   * as it may return a significant number of records.
   * 
   * @return a list containing all User entities, which may be empty but never
   *         null
   */
  List<User> getAllUsers();

  /**
   * Deletes a user by their unique identifier.
   * <p>
   * This method removes a user record from the system. Implementations may choose
   * to perform a soft delete (marking the record as inactive) or a hard delete
   * (physically removing the record), depending on business requirements.
   * <p>
   * If no user exists with the specified ID, implementations typically complete
   * silently without error.
   * 
   * @param id the unique identifier of the user to delete
   */
  void deleteUserById(Long id);

  /**
   * Checks if a username already exists in the system.
   * <p>
   * This method is typically used during user registration to ensure
   * username uniqueness before creating a new user account. The check
   * is typically more efficient than retrieving the full user entity.
   * 
   * @param username the username to check for existence
   * @return true if the username exists, false otherwise
   */
  boolean existsByUsername(String username);

  /**
   * Checks if an email address already exists in the system.
   * <p>
   * This method is typically used during user registration to ensure
   * email uniqueness before creating a new user account. The check
   * is typically more efficient than retrieving the full user entity.
   * 
   * @param email the email address to check for existence
   * @return true if the email exists, false otherwise
   */
  boolean existsByEmail(String email);
}