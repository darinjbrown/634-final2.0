package com.__final_backend.backend.test.unit.entity;

import com.__final_backend.backend.entity.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the User entity class.
 * Tests role management methods and basic properties.
 */
public class UserEntityTest {

  /**
   * Test adding roles to a user and checking if they have those roles.
   * Verifies that the addRole and hasRole methods work correctly.
   */
  @Test
  void testRoleManagement() {
    // Arrange
    User user = new User();

    // Act & Assert - Add roles and verify
    user.addRole("USER");
    assertTrue(user.hasRole("USER"), "User should have USER role");
    assertFalse(user.hasRole("ADMIN"), "User should not have ADMIN role");

    // Add another role
    user.addRole("ADMIN");
    assertTrue(user.hasRole("USER"), "User should still have USER role");
    assertTrue(user.hasRole("ADMIN"), "User should now have ADMIN role");
  }

  /**
   * Test setting and getting basic properties of the User entity.
   * Verifies that all properties are correctly stored and retrieved.
   */
  @Test
  void testBasicProperties() {
    // Arrange - Create test data
    Long id = 1L;
    String username = "testuser";
    String email = "test@example.com";
    String passwordHash = "hashedPassword123";
    String firstName = "Test";
    String lastName = "User";
    LocalDateTime now = LocalDateTime.now();

    // Act - Create user and set properties
    User user = new User();
    user.setId(id);
    user.setUsername(username);
    user.setEmail(email);
    user.setPasswordHash(passwordHash);
    user.setFirstName(firstName);
    user.setLastName(lastName);
    user.setCreatedAt(now);
    user.setUpdatedAt(now);
    user.setRoles(new HashSet<>());

    // Assert - Verify that properties were correctly set
    assertEquals(id, user.getId(), "ID should match");
    assertEquals(username, user.getUsername(), "Username should match");
    assertEquals(email, user.getEmail(), "Email should match");
    assertEquals(passwordHash, user.getPasswordHash(), "Password hash should match");
    assertEquals(firstName, user.getFirstName(), "First name should match");
    assertEquals(lastName, user.getLastName(), "Last name should match");
    assertEquals(now, user.getCreatedAt(), "Created at timestamp should match");
    assertEquals(now, user.getUpdatedAt(), "Updated at timestamp should match");
    assertNotNull(user.getRoles(), "Roles set should not be null");
    assertTrue(user.getRoles().isEmpty(), "Roles set should be empty");
  }

  /**
   * Test the constructor that initializes all fields.
   * Verifies that the all-args constructor correctly sets all properties.
   */
  @Test
  void testAllArgsConstructor() {
    // Arrange - Create test data
    Long id = 1L;
    String username = "testuser";
    String email = "test@example.com";
    String passwordHash = "hashedPassword123";
    String firstName = "Test";
    String lastName = "User";
    LocalDateTime createdAt = LocalDateTime.now();
    LocalDateTime updatedAt = LocalDateTime.now();
    HashSet<String> roles = new HashSet<>();
    roles.add("USER");

    // Act - Create user with all-args constructor
    User user = new User(
        id, username, email, passwordHash, firstName, lastName,
        roles, createdAt, updatedAt, null, null, null);

    // Assert - Verify that properties were correctly set
    assertEquals(id, user.getId(), "ID should match");
    assertEquals(username, user.getUsername(), "Username should match");
    assertEquals(email, user.getEmail(), "Email should match");
    assertEquals(passwordHash, user.getPasswordHash(), "Password hash should match");
    assertEquals(firstName, user.getFirstName(), "First name should match");
    assertEquals(lastName, user.getLastName(), "Last name should match");
    assertEquals(createdAt, user.getCreatedAt(), "Created at timestamp should match");
    assertEquals(updatedAt, user.getUpdatedAt(), "Updated at timestamp should match");
    assertTrue(user.hasRole("USER"), "User should have USER role");
  }
}
