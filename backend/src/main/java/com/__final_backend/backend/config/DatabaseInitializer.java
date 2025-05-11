package com.__final_backend.backend.config;

import com.__final_backend.backend.entity.User;
import com.__final_backend.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Database initializer that runs on application startup
 * Ensures that required system accounts like AdminTester are always present
 */
@Component
public class DatabaseInitializer implements CommandLineRunner {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Autowired
  public DatabaseInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  @Transactional
  public void run(String... args) {
    // Create or update admin test account
    createAdminTesterAccount();
  }

  /**
   * Ensures the AdminTester account exists with admin privileges
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