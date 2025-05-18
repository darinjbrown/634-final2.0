package com.__final_backend.backend.security.provider;

import com.__final_backend.backend.entity.User;
import com.__final_backend.backend.repository.UserRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Database implementation of UserProvider interface.
 * <p>
 * This implementation uses a Spring Data JPA repository to interact with the
 * database,
 * providing persistence for user data. It delegates all operations to the
 * underlying
 * UserRepository, which handles the actual database interactions.
 * <p>
 * This provider is the primary user data source when the application is
 * configured
 * to use database authentication.
 */
@Component
public class DatabaseUserProvider implements UserProvider {
  /** Repository for database operations on User entities. */
  private final UserRepository userRepository;

  /**
   * Constructs a new DatabaseUserProvider.
   * <p>
   * Spring automatically injects the appropriate UserRepository implementation.
   * The @Autowired annotation is optional for constructor injection since Spring
   * 4.3.
   *
   * @param userRepository the JPA repository for User entities
   */
  public DatabaseUserProvider(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  /**
   * {@inheritDoc}
   * <p>
   * This implementation delegates to the JPA repository's findByUsername method.
   */
  @Override
  public Optional<User> findByUsername(String username) {
    return userRepository.findByUsername(username);
  }

  /**
   * {@inheritDoc}
   * <p>
   * This implementation delegates to the JPA repository's findByEmail method.
   */
  @Override
  public Optional<User> findByEmail(String email) {
    return userRepository.findByEmail(email);
  }

  /**
   * {@inheritDoc}
   * <p>
   * This implementation delegates to the JPA repository's existsByUsername
   * method,
   * which uses an optimized query that doesn't require loading the entire entity.
   */
  @Override
  public boolean existsByUsername(String username) {
    return userRepository.existsByUsername(username);
  }

  /**
   * {@inheritDoc}
   * <p>
   * This implementation delegates to the JPA repository's existsByEmail method,
   * which uses an optimized query that doesn't require loading the entire entity.
   */
  @Override
  public boolean existsByEmail(String email) {
    return userRepository.existsByEmail(email);
  }

  /**
   * {@inheritDoc}
   * <p>
   * This implementation delegates to the JPA repository's findById method,
   * which is optimized to find entities by their primary key.
   */
  @Override
  public Optional<User> findById(Long id) {
    return userRepository.findById(id);
  }

  /**
   * {@inheritDoc}
   * <p>
   * This implementation delegates to the JPA repository's findAll method.
   * Be cautious when using this method with large datasets, as it loads
   * all user records into memory.
   */
  @Override
  public List<User> findAll() {
    return userRepository.findAll();
  }

  /**
   * {@inheritDoc}
   * <p>
   * This implementation delegates to the JPA repository's save method,
   * which handles both insert and update operations based on the entity's
   * ID value.
   */
  @Override
  public User save(User user) {
    return userRepository.save(user);
  }

  /**
   * {@inheritDoc}
   * <p>
   * This implementation delegates to the JPA repository's deleteById method.
   * If the ID doesn't exist, the repository operation completes silently
   * without throwing an exception.
   */
  @Override
  public void deleteById(Long id) {
    userRepository.deleteById(id);
  }
}
