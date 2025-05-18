package com.__final_backend.backend.test.unit.service.db;

import com.__final_backend.backend.entity.User;
import com.__final_backend.backend.security.provider.UserProvider;
import com.__final_backend.backend.service.db.UserService;
import com.__final_backend.backend.service.db.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for UserServiceImpl
 * Tests basic user management operations
 */
class UserServiceImplTest {

  @Mock
  private UserProvider userProvider;

  private UserService userService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    userService = new UserServiceImpl(userProvider);
  }

  /**
   * Test creating a user and retrieving by username
   * Verifies that a user can be created and then retrieved by username
   */
  @Test
  void testCreateAndRetrieveUserByUsername() {
    // Arrange
    User user = new User();
    user.setUsername("testuser");
    user.setEmail("test@example.com");
    user.setFirstName("Test");
    user.setLastName("User");
    user.setPasswordHash("hashedpassword");
    user.setCreatedAt(LocalDateTime.now());
    user.setUpdatedAt(LocalDateTime.now());
    user.addRole("USER");

    // Configure mock behaviors
    when(userProvider.save(any(User.class))).thenAnswer(invocation -> {
      User savedUser = invocation.getArgument(0);
      savedUser.setId(1L);
      return savedUser;
    });

    when(userProvider.findByUsername("testuser")).thenReturn(Optional.of(user));

    // Act
    // Create the user
    User createdUser = userService.createUser(user);

    // Retrieve the user by username
    Optional<User> retrievedUser = userService.getUserByUsername("testuser");

    // Assert
    assertNotNull(createdUser, "Created user should not be null");
    assertEquals(1L, createdUser.getId(), "User ID should be assigned");

    assertTrue(retrievedUser.isPresent(), "User should be retrieved by username");
    assertEquals("testuser", retrievedUser.get().getUsername(), "Username should match");
    assertEquals("test@example.com", retrievedUser.get().getEmail(), "Email should match");
    assertEquals("Test", retrievedUser.get().getFirstName(), "First name should match");
    assertEquals("User", retrievedUser.get().getLastName(), "Last name should match");
    assertTrue(retrievedUser.get().getRoles().contains("USER"), "User should have USER role");

    // Verify repository interactions
    verify(userProvider).save(any(User.class));
    verify(userProvider).findByUsername("testuser");
  }
}
