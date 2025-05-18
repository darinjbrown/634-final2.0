package com.__final_backend.backend.config;

import com.__final_backend.backend.entity.User;
import com.__final_backend.backend.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Database initializer that runs on application startup.
 *
 * <p>
 * This component ensures that required system accounts like AdminTester are
 * always
 * present in the database. It runs automatically when the application starts
 * and
 * creates or updates necessary accounts with appropriate roles.
 */
@Component
public class DatabaseInitializer implements CommandLineRunner {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public DatabaseInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  /**
   * Runs database initialization tasks when the application starts.
   *
   * <p>
   * This method is invoked automatically by Spring Boot after application context
   * has been created. It handles all initialization tasks that require database
   * access.
   *
   * @param args Command line arguments passed to the application
   */
  @Override
  @Transactional
  public void run(String... args) {
    // Create or update admin test account
    createAdminTesterAccount();
  }

  /**
   * Ensures the AdminTester account exists with admin privileges.
   *
   * <p>
   * This method either creates a new AdminTester account if it doesn't exist,
   * or updates an existing one to ensure it has the correct password and roles.
   */
  private void createAdminTesterAccount() {
    String username = "AdminTester";
    String password = "Test634";
    String email = "admin.tester@example.com";

    // Check if user exists
    Optional<User> existingUser = userRepository.findByUsername(username);

    if (existingUser.isPresent()) {
      User adminUser = existingUser.get();

      // Ensure password is correct
      adminUser.setPasswordHash(passwordEncoder.encode(password));

      // Ensure user has ADMIN role
      if (!adminUser.getRoles().contains("ADMIN")) {
        Set<String> roles = new HashSet<>(adminUser.getRoles());
        roles.add("ADMIN");
        adminUser.setRoles(roles);
      }

      // Ensure user has USER role
      if (!adminUser.getRoles().contains("USER")) {
        Set<String> roles = new HashSet<>(adminUser.getRoles());
        roles.add("USER");
        adminUser.setRoles(roles);
      }

      userRepository.save(adminUser);
      System.out.println("AdminTester account updated with correct password and roles");
    } else {
      // Create new admin user
      User adminUser = new User();
      adminUser.setUsername(username);
      adminUser.setEmail(email);
      adminUser.setPasswordHash(passwordEncoder.encode(password));
      adminUser.setFirstName("Admin");
      adminUser.setLastName("Tester");
      adminUser.setCreatedAt(LocalDateTime.now());
      adminUser.setUpdatedAt(LocalDateTime.now());

      // Set roles
      Set<String> roles = new HashSet<>();
      roles.add("ADMIN");
      roles.add("USER");
      adminUser.setRoles(roles);

      userRepository.save(adminUser);
      System.out.println("AdminTester account created successfully with ADMIN and USER roles");
    }
  }
}