package com.__final_backend.backend.test.unit.service;

import com.__final_backend.backend.entity.User;
import com.__final_backend.backend.security.JwtTokenUtil;
import com.__final_backend.backend.security.provider.UserProvider;
import com.__final_backend.backend.security.provider.sync.XmlToDbUserSynchronizer;
import com.__final_backend.backend.service.AuthService;
import com.__final_backend.backend.service.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the AuthServiceImpl class.
 * Tests basic authentication functionality including user registration.
 */
class AuthServiceImplTest {

  @Mock
  private UserProvider userProvider;

  @Mock
  private PasswordEncoder passwordEncoder;

  @Mock
  private JwtTokenUtil jwtTokenUtil;

  @Mock
  private XmlToDbUserSynchronizer xmlToDbSynchronizer;

  private AuthService authService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    authService = new AuthServiceImpl(userProvider, passwordEncoder, jwtTokenUtil, xmlToDbSynchronizer);
  }

  /**
   * Test user registration with valid inputs.
   * Verifies that a user can be registered successfully.
   */
  @Test
  void testRegisterWithValidInputs() {
    // Arrange
    String username = "testUser";
    String email = "test@example.com";
    String password = "Password123";
    String firstName = "Test";
    String lastName = "User";
    String encodedPassword = "encoded_password";

    // Configure mocks
    when(userProvider.existsByUsername(username)).thenReturn(false);
    when(userProvider.existsByEmail(email)).thenReturn(false);
    when(passwordEncoder.encode(password)).thenReturn(encodedPassword);

    // Mock the save method to return a user with the same properties
    when(userProvider.save(any(User.class))).thenAnswer(invocation -> {
      User savedUser = invocation.getArgument(0);
      savedUser.setId(1L); // Simulate database assigning an ID
      return savedUser;
    });

    // Act
    User registeredUser = authService.register(username, email, password, firstName, lastName);

    // Assert
    assertNotNull(registeredUser, "Registered user should not be null");
    assertEquals(username, registeredUser.getUsername(), "Username should match");
    assertEquals(email, registeredUser.getEmail(), "Email should match");
    assertEquals(encodedPassword, registeredUser.getPasswordHash(), "Password should be encoded");
    assertEquals(firstName, registeredUser.getFirstName(), "First name should match");
    assertEquals(lastName, registeredUser.getLastName(), "Last name should match");
    assertTrue(registeredUser.getRoles().contains("USER"), "User should have USER role");

    // Verify interactions
    verify(userProvider).existsByUsername(username);
    verify(userProvider).existsByEmail(email);
    verify(passwordEncoder).encode(password);
    verify(userProvider).save(any(User.class));
    verify(xmlToDbSynchronizer).synchronizeNewUser(username);
  }
}
