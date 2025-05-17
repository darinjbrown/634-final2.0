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
 * Custom implementation of UserDetailsService that uses the UserProvider
 * abstraction.
 * This allows switching between different user data sources.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

  private final UserProvider userProvider;

  public CustomUserDetailsService(UserProvider userProvider) {
    this.userProvider = userProvider;
  }

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

    return new org.springframework.security.core.userdetails.User(
        user.getUsername(),
        user.getPasswordHash(),
        authorities);
  }
}