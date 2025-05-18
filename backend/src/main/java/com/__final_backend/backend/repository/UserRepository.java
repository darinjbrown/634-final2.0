package com.__final_backend.backend.repository;

import com.__final_backend.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for User entity operations.
 * <p>
 * Provides methods for managing User entities in the database, including CRUD
 * operations
 * inherited from JpaRepository and custom query methods for user-specific
 * functionality
 * such as finding users by username or email and validating unique constraints.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  /**
   * Finds a user by their username.
   * <p>
   * This method is typically used during authentication to look up a user based
   * on the
   * username provided during login. Spring Data JPA automatically implements this
   * method
   * based on the method name.
   *
   * @param username the username to search for (case-sensitive)
   * @return an Optional containing the user if found, or empty if no user with
   *         the given
   *         username exists
   */
  Optional<User> findByUsername(String username);

  /**
   * Finds a user by their email address.
   * <p>
   * This method is commonly used during password recovery flows or when
   * confirming
   * email uniqueness. Spring Data JPA automatically implements this method based
   * on
   * the method name.
   *
   * @param email the email address to search for (case-insensitive match is
   *              recommended
   *              in the database configuration)
   * @return an Optional containing the user if found, or empty if no user with
   *         the given
   *         email exists
   */
  Optional<User> findByEmail(String email);

  /**
   * Checks if a username already exists in the database.
   * <p>
   * This method is typically used during user registration to enforce unique
   * usernames.
   * It performs a more efficient check than findByUsername since it doesn't need
   * to
   * retrieve the entire user object.
   *
   * @param username the username to check (case-sensitive)
   * @return true if the username exists, false otherwise
   */
  boolean existsByUsername(String username);

  /**
   * Checks if an email address already exists in the database.
   * <p>
   * This method is typically used during user registration to enforce unique
   * email addresses.
   * It performs a more efficient check than findByEmail since it doesn't need to
   * retrieve the entire user object.
   *
   * @param email the email to check (case-insensitive match is recommended in the
   *              database configuration)
   * @return true if the email exists, false otherwise
   */
  boolean existsByEmail(String email);
}