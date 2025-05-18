package com.__final_backend.backend.service;

import com.__final_backend.backend.entity.User;
import com.__final_backend.backend.security.JwtTokenUtil;
import com.__final_backend.backend.security.provider.UserProvider;
import com.__final_backend.backend.security.provider.sync.XmlToDbUserSynchronizer;
import org.springframework.security.core.Authentication;
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
 * Implementation of AuthService for user authentication operations.
 * <p>
 * This service handles user authentication flows including:
 * <ul>
 * <li>User registration and account creation
 * <li>Authentication with username/email and password
 * <li>Remember-me token generation and validation
 * <li>User role management
 * <li>Synchronization between XML and database user stores
 * </ul>
 * <p>
 * The implementation supports multiple user providers through the UserProvider
 * interface
 * and includes secure password handling with encryption.
 */
@Service
public class AuthServiceImpl implements AuthService {
  /** User provider for user repository operations. */
  private final UserProvider userProvider;

  /** Password encoder for secure password handling. */
  private final PasswordEncoder passwordEncoder;
  /**
   * JWT token utility for authentication tokens.
   * Injected for potential future use in token-based authentication features.
   */
  private final JwtTokenUtil jwtTokenUtil;

  /** Synchronizer for XML to database user data. */
  private final XmlToDbUserSynchronizer xmlToDbSynchronizer;

  /** In-memory store for remember-me tokens. */
  private final Map<String, RememberMeToken> rememberMeTokenStore = new HashMap<>();

  /**
   * Constructs a new AuthServiceImpl with the required dependencies.
   * <p>
   * All dependencies are automatically injected by Spring's dependency injection
   * system.
   *
   * @param userProvider        the provider for user data access
   * @param passwordEncoder     the encoder for secure password handling
   * @param jwtTokenUtil        the utility for JWT token operations
   * @param xmlToDbSynchronizer the synchronizer for XML to database user data
   */
  public AuthServiceImpl(UserProvider userProvider,
      PasswordEncoder passwordEncoder,
      JwtTokenUtil jwtTokenUtil,
      XmlToDbUserSynchronizer xmlToDbSynchronizer) {
    this.userProvider = userProvider;
    this.passwordEncoder = passwordEncoder;
    this.jwtTokenUtil = jwtTokenUtil;
    this.xmlToDbSynchronizer = xmlToDbSynchronizer;
  }

  /**
   * {@inheritDoc}
   * <p>
   * The registration process includes:
   * <ul>
   * <li>Validating that the username and email are unique
   * <li>Creating a new user entity with secure password hashing
   * <li>Adding the default USER role
   * <li>Synchronizing with XML data store if needed
   * </ul>
   *
   * @throws IllegalArgumentException if username or email already exists
   */
  @Override
  @Transactional
  public User register(String username, String email, String password, String firstName, String lastName) {
    // Check if username already exists
    if (userProvider.existsByUsername(username)) {
      throw new IllegalArgumentException("Username already exists");
    }

    // Check if email already exists
    if (userProvider.existsByEmail(email)) {
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

    // Save user through the provider
    User savedUser = userProvider.save(user);

    // If using XML provider, also synchronize to database
    if (savedUser != null) {
      xmlToDbSynchronizer.synchronizeNewUser(username);
    }

    return savedUser;
  }

  /**
   * {@inheritDoc}
   * <p>
   * The authentication process supports both username and email login.
   * It first attempts to find the user by username, and if not found,
   * tries to find by email address. Passwords are verified using the
   * secure password encoder.
   */
  @Override
  public Optional<User> authenticate(String usernameOrEmail, String password) {
    // Try to find user by username
    Optional<User> userOptional = userProvider.findByUsername(usernameOrEmail);

    // If not found, try by email
    if (userOptional.isEmpty()) {
      userOptional = userProvider.findByEmail(usernameOrEmail);
    }

    // Check if user exists and password matches
    if (userOptional.isPresent() && matchesPassword(password, userOptional.get().getPasswordHash())) {
      return userOptional;
    }

    return Optional.empty();
  }

  /**
   * {@inheritDoc}
   * <p>
   * This method extracts user information from the Spring Security context.
   * It returns empty if the user is not authenticated or if the authentication
   * is for an anonymous user.
   */
  @Override
  public Optional<User> getCurrentUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !authentication.isAuthenticated() ||
        authentication.getPrincipal().equals("anonymousUser")) {
      return Optional.empty();
    }

    UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    return userProvider.findByUsername(userDetails.getUsername());
  }

  /**
   * {@inheritDoc}
   * <p>
   * This method uses the configured password encoder to securely compare
   * the raw password with the encoded password stored in the database.
   */
  @Override
  public boolean matchesPassword(String rawPassword, String encodedPassword) {
    return passwordEncoder.matches(rawPassword, encodedPassword);
  }

  /**
   * {@inheritDoc}
   * <p>
   * Creates a remember-me token valid for 14 days. The token is stored in memory
   * with the associated username and expiry date. A UUID is used to ensure token
   * uniqueness.
   */
  @Override
  public String generateRememberMeToken(User user) {
    String token = UUID.randomUUID().toString();
    RememberMeToken rememberMeToken = new RememberMeToken(user.getUsername(), LocalDateTime.now().plusDays(14));
    rememberMeTokenStore.put(token, rememberMeToken);
    return token;
  }

  /**
   * {@inheritDoc}
   * <p>
   * Validates that a remember-me token exists and has not expired.
   * Expired tokens are automatically removed from the token store.
   * If valid, returns the associated user.
   */
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

    return userProvider.findByUsername(rememberMeToken.getUsername());
  }

  /**
   * {@inheritDoc}
   * <p>
   * Adds a role to the user and persists the changes.
   * This operation is performed within a transaction to ensure data consistency.
   */
  @Override
  @Transactional
  public User addRole(User user, String role) {
    user.addRole(role);
    return userProvider.save(user);
  }

  /**
   * {@inheritDoc}
   * <p>
   * Removes a role from the user and persists the changes.
   * This operation is performed within a transaction to ensure data consistency.
   */
  @Override
  @Transactional
  public User removeRole(User user, String role) {
    user.getRoles().remove(role);
    return userProvider.save(user);
  }

  /**
   * {@inheritDoc}
   * <p>
   * Checks if the currently authenticated user has the specified role.
   * This method looks for Spring Security authorities with the "ROLE_" prefix
   * followed by the role name.
   */
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
   * Private class to store remember-me token information.
   * <p>
   * This immutable inner class encapsulates the data associated with a
   * remember-me token,
   * including the username and when the token expires.
   */
  private static class RememberMeToken {
    /** The username associated with this token. */
    private final String username;

    /** The date and time when this token expires. */
    private final LocalDateTime expiryDate;

    /**
     * Constructs a new RememberMeToken.
     *
     * @param username   the username associated with this token
     * @param expiryDate the date and time when this token expires
     */
    public RememberMeToken(String username, LocalDateTime expiryDate) {
      this.username = username;
      this.expiryDate = expiryDate;
    }

    /**
     * Gets the username associated with this token.
     *
     * @return the username
     */
    public String getUsername() {
      return username;
    }

    /**
     * Gets the expiry date of this token.
     *
     * @return the expiry date
     */
    public LocalDateTime getExpiryDate() {
      return expiryDate;
    }
  }
}