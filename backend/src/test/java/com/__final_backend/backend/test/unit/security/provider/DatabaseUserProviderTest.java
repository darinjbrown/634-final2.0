package com.__final_backend.backend.test.unit.security.provider;

import com.__final_backend.backend.entity.User;
import com.__final_backend.backend.repository.UserRepository;
import com.__final_backend.backend.security.provider.DatabaseUserProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for DatabaseUserProvider class.
 * Tests database operations through the UserProvider interface.
 */
public class DatabaseUserProviderTest {

  private DatabaseUserProvider databaseUserProvider;

  @Mock
  private UserRepository userRepository;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    databaseUserProvider = new DatabaseUserProvider(userRepository);
  }

  /**
   * Test finding a user by username.
   * Verifies that the provider correctly returns a user when found in the
   * database.
   */
  @Test
  void testFindByUsername() {
    // Arrange
    String username = "testUser";
    User testUser = createTestUser(username, "test@example.com");

    when(userRepository.findByUsername(username)).thenReturn(Optional.of(testUser));
    when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

    // Act & Assert
    Optional<User> foundUser = databaseUserProvider.findByUsername(username);

    assertEquals(username, foundUser.get().getUsername(), "Username should match");

    // Verify non-existent user
    Optional<User> notFoundUser = databaseUserProvider.findByUsername("nonExistentUser");
    assertFalse(notFoundUser.isPresent(), "Non-existent user should not be found");
  }

  /**
   * Test checking if a username exists.
   */
  @Test
  void testExistsByUsername() {
    // Arrange
    String existingUsername = "existingUser";
    String nonExistingUsername = "nonExistingUser";

    when(userRepository.existsByUsername(existingUsername)).thenReturn(true);
    when(userRepository.existsByUsername(nonExistingUsername)).thenReturn(false);

    // Act & Assert
    assertTrue(databaseUserProvider.existsByUsername(existingUsername), "Existing username should return true");
    assertFalse(databaseUserProvider.existsByUsername(nonExistingUsername),
        "Non-existing username should return false");

    verify(userRepository, times(1)).existsByUsername(existingUsername);
    verify(userRepository, times(1)).existsByUsername(nonExistingUsername);
  }

  /**
   * Utility method to create a test user.
   */
  private User createTestUser(String username, String email) {
    User user = new User();
    user.setId(1L);
    user.setUsername(username);
    user.setEmail(email);
    user.setPasswordHash("hashedPassword");
    user.setFirstName("Test");
    user.setLastName("User");
    user.setRoles(new HashSet<>(Arrays.asList("USER")));
    user.setCreatedAt(LocalDateTime.now());
    user.setUpdatedAt(LocalDateTime.now());
    return user;
  }
}
