package com.__final_backend.backend.service.db;

import com.__final_backend.backend.entity.User;
import com.__final_backend.backend.security.provider.UserProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of the UserService interface for managing user data
 * operations.
 * <p>
 * This service acts as a facade for user-related operations, providing a
 * unified entry point
 * for creating, reading, updating, and deleting user entities. It delegates the
 * actual data
 * access operations to the configured UserProvider implementation, maintaining
 * a clean
 * separation between the service layer and data access logic.
 * <p>
 * All methods in this service are executed within transactions to ensure data
 * consistency.
 */
@Service
@Transactional
public class UserServiceImpl implements UserService {
  /** Provider abstraction for user data access operations. */
  private final UserProvider userProvider;

  /**
   * Constructs a new UserServiceImpl with the specified user provider.
   * <p>
   * Spring automatically injects the appropriate UserProvider implementation
   * based on
   * configuration, allowing this service to work with different data sources.
   *
   * @param userProvider the provider to use for user data operations
   */
  public UserServiceImpl(UserProvider userProvider) {
    this.userProvider = userProvider;
  }

  /**
   * {@inheritDoc}
   * <p>
   * This implementation delegates to the configured UserProvider to save the new
   * user
   * entity, which typically generates a unique ID and persists the user record.
   */
  @Override
  public User createUser(User user) {
    return userProvider.save(user);
  }

  /**
   * {@inheritDoc}
   * <p>
   * This implementation delegates to the configured UserProvider to update the
   * existing
   * user entity. The user must have a valid ID to be properly updated.
   */
  @Override
  public User updateUser(User user) {
    return userProvider.save(user);
  }

  /**
   * {@inheritDoc}
   * <p>
   * This implementation delegates to the UserProvider's findById method to locate
   * a user by their unique identifier.
   */
  @Override
  public Optional<User> getUserById(Long id) {
    return userProvider.findById(id);
  }

  /**
   * {@inheritDoc}
   * <p>
   * This implementation delegates to the UserProvider's findByUsername method
   * to perform a case-sensitive username search.
   */
  @Override
  public Optional<User> getUserByUsername(String username) {
    return userProvider.findByUsername(username);
  }

  /**
   * {@inheritDoc}
   * <p>
   * This implementation delegates to the UserProvider's findByEmail method
   * to perform an email lookup, typically used during registration or password
   * recovery.
   */
  @Override
  public Optional<User> getUserByEmail(String email) {
    return userProvider.findByEmail(email);
  }

  /**
   * {@inheritDoc}
   * <p>
   * This implementation delegates to the UserProvider's findAll method.
   * Be cautious when using this method with large datasets, as it loads
   * all user records into memory.
   */
  @Override
  public List<User> getAllUsers() {
    return userProvider.findAll();
  }

  /**
   * {@inheritDoc}
   * <p>
   * This implementation delegates to the UserProvider's deleteById method.
   * If no user exists with the specified ID, the operation completes silently.
   */
  @Override
  public void deleteUserById(Long id) {
    userProvider.deleteById(id);
  }

  /**
   * {@inheritDoc}
   * <p>
   * This implementation delegates to the UserProvider's existsByUsername method,
   * which typically uses an optimized query to check for username existence.
   */
  @Override
  public boolean existsByUsername(String username) {
    return userProvider.existsByUsername(username);
  }

  /**
   * {@inheritDoc}
   * <p>
   * This implementation delegates to the UserProvider's existsByEmail method,
   * which typically uses an optimized query to check for email existence.
   */
  @Override
  public boolean existsByEmail(String email) {
    return userProvider.existsByEmail(email);
  }
}