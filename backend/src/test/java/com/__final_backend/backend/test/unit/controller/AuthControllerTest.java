package com.__final_backend.backend.test.unit.controller;

import com.__final_backend.backend.controller.AuthController;
import com.__final_backend.backend.entity.User;
import com.__final_backend.backend.security.JwtTokenUtil;
import com.__final_backend.backend.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.HashMap;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit tests for the AuthController class.
 * Tests basic authentication endpoints including login.
 */
public class AuthControllerTest {

  private MockMvc mockMvc;

  @Mock
  private AuthenticationManager authenticationManager;

  @Mock
  private JwtTokenUtil jwtTokenUtil;

  @Mock
  private AuthService authService;

  @Mock
  private Authentication authentication;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    AuthController authController = new AuthController(authenticationManager, jwtTokenUtil, authService);
    mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
  }

  /**
   * Test login endpoint with valid credentials.
   * Verifies that a valid JWT token is returned when credentials are correct.
   */
  @Test
  void testLoginWithValidCredentials() throws Exception {
    // Arrange
    String username = "testUser";
    String password = "Password123";
    String token = "test-jwt-token";

    // Setup mock authentication
    when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
        .thenReturn(authentication);
    when(jwtTokenUtil.generateToken(authentication)).thenReturn(token);

    User testUser = new User();
    testUser.setUsername(username);
    testUser.setFirstName("Test");
    testUser.setLastName("User");
    when(authService.authenticate(anyString(), anyString())).thenReturn(Optional.of(testUser));

    // Create request body
    String requestBody = String.format(
        "{\"username\":\"%s\",\"password\":\"%s\",\"rememberMe\":false}",
        username, password);

    // Act & Assert
    mockMvc.perform(post("/api/auth/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content(requestBody))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.token").value(token));
  }

  /**
   * Test register endpoint with valid user data.
   * Verifies that a user can be registered successfully.
   */
  @Test
  void testRegisterWithValidData() throws Exception {
    // Arrange
    String username = "newUser";
    String email = "new@example.com";
    String password = "Password123";
    String firstName = "New";
    String lastName = "User";

    User newUser = new User();
    newUser.setUsername(username);
    newUser.setEmail(email);
    newUser.setFirstName(firstName);
    newUser.setLastName(lastName);

    when(authService.register(username, email, password, firstName, lastName))
        .thenReturn(newUser);

    // Create request body
    String requestBody = String.format(
        "{\"username\":\"%s\",\"email\":\"%s\",\"password\":\"%s\",\"firstName\":\"%s\",\"lastName\":\"%s\"}",
        username, email, password, firstName, lastName);

    // Act & Assert
    mockMvc.perform(post("/api/auth/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content(requestBody))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("User registered successfully"))
        .andExpect(jsonPath("$.username").value(username));
  }
}
