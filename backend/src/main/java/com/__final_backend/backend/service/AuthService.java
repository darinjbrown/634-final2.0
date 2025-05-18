package com.__final_backend.backend.service;

import com.__final_backend.backend.entity.User;

import java.util.Optional;

/**
 * Service interface for user authentication operations.
 * <p>
 * This interface defines the contract for authentication-related operations in
 * the application,
 * including user registration, authentication, and role management. It provides
 * methods for:
 * <ul>
 * <li>User registration and account creation
 * <li>Authentication with username/email and password
 * <li>Remember-me token functionality
 * <li>User role management
 * <li>Current user access
 * </ul>
 * <p>
 * Implementations of this interface should handle secure password storage,
 * token generation,
 * and integration with the application's security framework.
 */
public interface AuthService {
  /**
   * Registers a new user with the system.
   * <p>
   * This method creates a new user account with the provided information.
   * The password will be securely encrypted before storage.
   * A default user role is typically assigned to new accounts.
   * 
   * @param username  the unique username for the new account, must not be null or
   *                  empty
   * @param email     the email address associated with the account, must be valid
   *                  and unique
   * @param password  the password for the account (will be encrypted), must meet
   *                  security requirements
   * @param firstName the user's first name
   * @param lastName  the user's last name
   * @return the registered user entity with populated ID and metadata
   * @throws IllegalArgumentException if username or email already exists
   * @throws IllegalArgumentException if any required field is null or empty
   */
  User register(String username, String email, String password, String firstName, String lastName);

  /**
   * Authenticates a user with username/email and password.
   * <p>
   * This method attempts to authenticate a user by checking the provided
   * credentials
   * against stored user data. The authentication is successful if a user with the
   * provided username or email exists and the password matches.
   * 
   * @param usernameOrEmail the username or email address of the user attempting
   *                        to authenticate
   * @param password        the password to verify
   * @return an Optional containing the authenticated user if successful, or empty
   *         if authentication fails
   * @throws IllegalArgumentException if credentials are null or empty
   */
  Optional<User> authenticate(String usernameOrEmail, String password);

  /**
   * Gets the currently authenticated user.
   * <p>
   * This method retrieves information about the user associated with the current
   * security context (e.g., from the current HTTP session or security token).
   * 
   * @return an Optional containing the current user if authenticated, or empty if
   *         not authenticated
   */
  Optional<User> getCurrentUser();

  /**
   * Checks if the provided password matches the stored encoded password.
   * <p>
   * This method securely compares a raw (plaintext) password with an encoded
   * password
   * using the application's password encoder. It should be used when validating
   * user-provided passwords against stored password hashes.
   * 
   * @param rawPassword     the raw (plaintext) password to check
   * @param encodedPassword the encoded password stored in the database
   * @return true if the password matches, false otherwise
   * @throws IllegalArgumentException if either password is null
   */
  boolean matchesPassword(String rawPassword, String encodedPassword);

  /**
   * Generates a remember-me token for a user.
   * <p>
   * This method creates a secure token that can be used for persistent
   * authentication
   * (remember-me functionality). The token is associated with the specified user
   * and typically has an expiration date.
   * 
   * @param user the user for whom to generate the token, must not be null
   * @return the generated remember-me token
   * @throws IllegalArgumentException if user is null
   */
  String generateRememberMeToken(User user);

  /**
   * Validates a remember-me token.
   * <p>
   * This method checks if a remember-me token is valid and has not expired.
   * If valid, it returns the user associated with the token.
   * 
   * @param token the token to validate
   * @return an Optional containing the user if the token is valid and not
   *         expired,
   *         or empty if the token is invalid or expired
   * @throws IllegalArgumentException if token is null or empty
   */
  Optional<User> validateRememberMeToken(String token);

  /**
   * Adds a role to a user.
   * <p>
   * This method grants a specific role to the given user, enhancing their
   * permissions
   * within the system. If the user already has the specified role, the operation
   * may be
   * idempotent depending on the implementation.
   * 
   * @param user the user to whom the role will be added, must not be null
   * @param role the role to add, must be a valid role name in the system
   * @return the updated user with the new role assigned
   * @throws IllegalArgumentException if user is null or role is invalid
   * @throws IllegalStateException    if the role cannot be added due to business
   *                                  rules
   */
  User addRole(User user, String role);

  /**
   * Removes a role from a user.
   * <p>
   * This method revokes a specific role from the given user, reducing their
   * permissions
   * within the system. If the user doesn't have the specified role, the operation
   * may be
   * idempotent depending on the implementation.
   * 
   * @param user the user from whom the role will be removed, must not be null
   * @param role the role to remove, must be a valid role name in the system
   * @return the updated user with the role removed
   * @throws IllegalArgumentException if user is null or role is invalid
   * @throws IllegalStateException    if the role cannot be removed due to
   *                                  business rules
   *                                  (e.g., removing the last administrative
   *                                  role)
   */
  User removeRole(User user, String role);

  /**
   * Checks if the current user has a specific role.
   * <p>
   * This method verifies whether the currently authenticated user has been
   * granted
   * the specified role. This is useful for authorization checks within the
   * application.
   * <p>
   * If no user is currently authenticated, the method will typically return
   * false.
   * 
   * @param role the role to check, must be a valid role name in the system
   * @return true if the current user has the specified role, false otherwise
   * @throws IllegalArgumentException if role is null or empty
   */
  boolean hasRole(String role);
}