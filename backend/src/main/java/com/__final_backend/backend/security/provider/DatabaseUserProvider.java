package com.__final_backend.backend.security.provider;

import com.__final_backend.backend.entity.User;
import com.__final_backend.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Database implementation of UserProvider.
 * Retrieves user information from a database using Spring Data JPA repository.
 */
@Component
public class DatabaseUserProvider implements UserProvider {

  private final UserRepository userRepository;

  @Autowired
  public DatabaseUserProvider(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public Optional<User> findByUsername(String username) {
    return userRepository.findByUsername(username);
  }

  @Override
  public Optional<User> findByEmail(String email) {
    return userRepository.findByEmail(email);
  }

  @Override
  public boolean existsByUsername(String username) {
    return userRepository.existsByUsername(username);
  }

  @Override
  public boolean existsByEmail(String email) {
    return userRepository.existsByEmail(email);
  }

  @Override
  public Optional<User> findById(Long id) {
    return userRepository.findById(id);
  }

  @Override
  public List<User> findAll() {
    return userRepository.findAll();
  }

  @Override
  public User save(User user) {
    return userRepository.save(user);
  }

  @Override
  public void deleteById(Long id) {
    userRepository.deleteById(id);
  }
}
