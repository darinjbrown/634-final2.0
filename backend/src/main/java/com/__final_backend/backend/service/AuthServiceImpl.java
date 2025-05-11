package com.__final_backend.backend.service;

import com.__final_backend.backend.entity.User;
import com.__final_backend.backend.repository.UserRepository;
import com.__final_backend.backend.security.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of AuthService for user authentication operations
 */
@Service
public class AuthServiceImpl implements AuthService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtTokenUtil jwtTokenUtil;
  private final Map<String, RememberMeToken> rememberMeTokenStore = new HashMap<>();

  @Autowired
  public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtTokenUtil jwtTokenUtil) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtTokenUtil = jwtTokenUtil;
  }

  @Override
  @Transactional
  public User register(String username, String email, String password, String firstName, String lastName) {
    // Check if username already exists
    if (userRepository.existsByUsername(username)) {
      throw new IllegalArgumentException("Username already exists");
    }

    // Check if email already exists
    if (userRepository.existsByEmail(email)) {
      throw new IllegalArgumentException("Email already exists");
    }

    // Create new user
    User user = new User();
    user.setUsername(username);
    user.setEmail(email);
    user.setPasswordHash(passwordEncoder.encode(password));
    user.setFirstName(firstName);
    user.setLastName(lastName);
    user.setCreatedAt(LocalDateTime.now());
    user.setUpdatedAt(LocalDateTime.now());

    // Add default USER role
    user.addRole("USER");

    return userRepository.save(user);
  }

  @Override
  public Optional<User> authenticate(String usernameOrEmail, String password) {
    // Try to find user by username
    Optional<User> userOptional = userRepository.findByUsername(usernameOrEmail);

    // If not found, try by email
    if (userOptional.isEmpty()) {
      userOptional = userRepository.findByEmail(usernameOrEmail);
    }

    // Check if user exists and password matches
    if (userOptional.isPresent() && matchesPassword(password, userOptional.get().getPasswordHash())) {
      return userOptional;
    }

    return Optional.empty();
  }

  @Override
  public Optional<User> getCurrentUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !authentication.isAuthenticated() ||
        authentication.getPrincipal().equals("anonymousUser")) {
      return Optional.empty();
    }

    UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    return userRepository.findByUsername(userDetails.getUsername());
  }

  @Override
  public boolean matchesPassword(String rawPassword, String encodedPassword) {
    return passwordEncoder.matches(rawPassword, encodedPassword);
  }

  @Override
  public String generateRememberMeToken(User user) {
    String token = UUID.randomUUID().toString();
    RememberMeToken rememberMeToken = new RememberMeToken(user.getUsername(), LocalDateTime.now().plusDays(14));
    rememberMeTokenStore.put(token, rememberMeToken);
    return token;
  }

  @Override
  public Optional<User> validateRememberMeToken(String token) {
    RememberMeToken rememberMeToken = rememberMeTokenStore.get(token);

    if (rememberMeToken == null || rememberMeToken.getExpiryDate().isBefore(LocalDateTime.now())) {
      // Remove expired token
      if (rememberMeToken != null) {
        rememberMeTokenStore.remove(token);
      }
      return Optional.empty();
    }

    return userRepository.findByUsername(rememberMeToken.getUsername());
  }

  @Override
  @Transactional
  public User addRole(User user, String role) {
    user.addRole(role);
    return userRepository.save(user);
  }

  @Override
  @Transactional
  public User removeRole(User user, String role) {
    user.getRoles().remove(role);
    return userRepository.save(user);
  }

  @Override
  public boolean hasRole(String role) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth != null && auth.isAuthenticated() && !(auth.getPrincipal().equals("anonymousUser"))) {
      return auth.getAuthorities().stream()
          .anyMatch(a -> a.getAuthority().equals("ROLE_" + role));
    }
    return false;
  }

  /**
   * Private class to store remember-me token information
   */
  private static class RememberMeToken {
    private final String username;
    private final LocalDateTime expiryDate;

    public RememberMeToken(String username, LocalDateTime expiryDate) {
      this.username = username;
      this.expiryDate = expiryDate;
    }

    public String getUsername() {
      return username;
    }

    public LocalDateTime getExpiryDate() {
      return expiryDate;
    }
  }
}