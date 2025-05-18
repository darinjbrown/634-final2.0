package com.__final_backend.backend.security;

import com.__final_backend.backend.entity.User;
import com.__final_backend.backend.security.provider.UserProvider;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Custom implementation of Spring Security's UserDetailsService interface.
 * <p>
 * This service bridges the gap between Spring Security's authentication system
 * and
 * the application's user management system. It uses the UserProvider
 * abstraction to
 * decouple the service from specific data sources, allowing seamless switching
 * between
 * different user storage mechanisms (database, XML file, etc.) without
 * modifying this class.
 * <p>
 * During authentication, Spring Security calls this service to load user
 * details,
 * convert application-specific user entities into Spring Security's UserDetails
 * objects,
 * and apply appropriate role-based authorities.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {
  /** Provider for user data that abstracts the actual storage mechanism. */
  private final UserProvider userProvider;

  /**
   * Constructs a new CustomUserDetailsService with the provided user provider.
   * <p>
   * The actual implementation of UserProvider is injected by Spring based on
   * configuration,
   * supporting the strategy pattern for user data retrieval.
   *
   * @param userProvider the provider implementation for accessing user data
   */
  public CustomUserDetailsService(UserProvider userProvider) {
    this.userProvider = userProvider;
  }

  /**
   * Loads user details by username during the authentication process.
   * <p>
   * This method is called by Spring Security's authentication manager to retrieve
   * user details when processing an authentication request. It performs these
   * steps:
   * <ol>
   * <li>Retrieves the application-specific User entity via the UserProvider</li>
   * <li>Maps the user's roles to Spring Security authorities with "ROLE_"
   * prefix</li>
   * <li>Creates and returns a Spring Security UserDetails object</li>
   * </ol>
   * <p>
   * The method applies a default "ROLE_USER" authority if the user has no defined
   * roles.
   *
   * @param username the username to load; never {@code null}
   * @return a fully populated UserDetails object
   * @throws UsernameNotFoundException if the user cannot be found or has no
   *                                   permissions
   */
  @Override
  @Transactional(readOnly = true)
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user = userProvider.findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

    List<SimpleGrantedAuthority> authorities = new ArrayList<>();

    // Add user authorities from roles
    if (user.getRoles() != null && !user.getRoles().isEmpty()) {
      authorities.addAll(user.getRoles().stream()
          .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
          .collect(Collectors.toList()));
    } else {
      // Default to USER role if no roles are specified
      authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
    }

    // Create Spring Security's UserDetails implementation with username, password
    // hash, and authorities
    return new org.springframework.security.core.userdetails.User(
        user.getUsername(),
        user.getPasswordHash(),
        authorities);
  }
}