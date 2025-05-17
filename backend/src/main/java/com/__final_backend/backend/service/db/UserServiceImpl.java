package com.__final_backend.backend.service.db;

import com.__final_backend.backend.entity.User;
import com.__final_backend.backend.security.provider.UserProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of the UserService interface
 */
@Service
@Transactional
public class UserServiceImpl implements UserService {
  private final UserProvider userProvider;

  @Autowired
  public UserServiceImpl(UserProvider userProvider) {
    this.userProvider = userProvider;
  }

  @Override
  public User createUser(User user) {
    return userProvider.save(user);
  }

  @Override
  public User updateUser(User user) {
    return userProvider.save(user);
  }

  @Override
  public Optional<User> getUserById(Long id) {
    return userProvider.findById(id);
  }

  @Override
  public Optional<User> getUserByUsername(String username) {
    return userProvider.findByUsername(username);
  }

  @Override
  public Optional<User> getUserByEmail(String email) {
    return userProvider.findByEmail(email);
  }

  @Override
  public List<User> getAllUsers() {
    return userProvider.findAll();
  }

  @Override
  public void deleteUserById(Long id) {
    userProvider.deleteById(id);
  }

  @Override
  public boolean existsByUsername(String username) {
    return userProvider.existsByUsername(username);
  }

  @Override
  public boolean existsByEmail(String email) {
    return userProvider.existsByEmail(email);
  }
}