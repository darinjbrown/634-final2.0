package com.__final_backend.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Common security configuration beans that need to be accessed by multiple
 * components.
 *
 * <p>
 * This configuration class provides security-related beans that are used across
 * the application.
 * It has been extracted from the main security configuration to prevent
 * circular dependencies
 * between components.
 */
@Configuration
public class CommonSecurityConfig {

  /**
   * Creates a password encoder bean for securely hashing passwords.
   *
   * <p>
   * Uses BCrypt hashing algorithm which automatically includes a random salt
   * and is resistant to brute force attacks.
   *
   * @return A BCryptPasswordEncoder instance for password hashing
   */
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}