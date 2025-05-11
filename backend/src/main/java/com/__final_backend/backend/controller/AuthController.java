package com.__final_backend.backend.controller;

import com.__final_backend.backend.entity.User;
import com.__final_backend.backend.security.JwtTokenUtil;
import com.__final_backend.backend.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Controller for authentication operations including login, register, and
 * logout
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

  private final AuthenticationManager authenticationManager;
  private final JwtTokenUtil jwtTokenUtil;
  private final AuthService authService;
  private static final String REMEMBER_ME_COOKIE_NAME = "remember-me";
  private static final int REMEMBER_ME_COOKIE_MAX_AGE = 14 * 24 * 60 * 60; // 14 days in seconds

  @Autowired
  public AuthController(AuthenticationManager authenticationManager, JwtTokenUtil jwtTokenUtil,
      AuthService authService) {
    this.authenticationManager = authenticationManager;
    this.jwtTokenUtil = jwtTokenUtil;
    this.authService = authService;
  }

  /**
   * Register a new user
   * 
   * @param registerRequest the registration data
   * @return response with success message or error
   */
  @PostMapping("/register")
  public ResponseEntity<?> registerUser(@RequestBody RegisterRequest registerRequest) {
    try {
      User user = authService.register(
          registerRequest.getUsername(),
          registerRequest.getEmail(),
          registerRequest.getPassword(),
          registerRequest.getFirstName(),
          registerRequest.getLastName());

      Map<String, Object> response = new HashMap<>();
      response.put("message", "User registered successfully");
      response.put("username", user.getUsername());

      return ResponseEntity.ok(response);
    } catch (IllegalArgumentException e) {
      Map<String, String> errorResponse = new HashMap<>();
      errorResponse.put("error", e.getMessage());
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
  }

  /**
   * Authenticate user and generate JWT token
   *
   * @param loginRequest the login credentials
   * @param response     HTTP response for setting cookies
   * @return JWT token if authentication is successful
   */
  @PostMapping("/login")
  public ResponseEntity<?> authenticateUser(
      @RequestBody LoginRequest loginRequest,
      HttpServletResponse response) {

    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            loginRequest.getUsername(),
            loginRequest.getPassword()));

    SecurityContextHolder.getContext().setAuthentication(authentication);
    String jwt = jwtTokenUtil.generateToken(authentication);

    // Handle remember-me functionality
    if (loginRequest.isRememberMe()) {
      Optional<User> userOptional = authService.authenticate(
          loginRequest.getUsername(), loginRequest.getPassword());

      if (userOptional.isPresent()) {
        String rememberMeToken = authService.generateRememberMeToken(userOptional.get());
        Cookie rememberMeCookie = new Cookie(REMEMBER_ME_COOKIE_NAME, rememberMeToken);
        rememberMeCookie.setMaxAge(REMEMBER_ME_COOKIE_MAX_AGE);
        rememberMeCookie.setPath("/");
        rememberMeCookie.setHttpOnly(true); // For security, not accessible via JavaScript
        response.addCookie(rememberMeCookie);
      }
    }

    Map<String, String> responseBody = new HashMap<>();
    responseBody.put("token", jwt);

    return ResponseEntity.ok(responseBody);
  }

  /**
   * Logout user by invalidating authentication
   *
   * @param request  HTTP request for accessing cookies
   * @param response HTTP response for clearing cookies
   * @return success message
   */
  @PostMapping("/logout")
  public ResponseEntity<?> logoutUser(
      HttpServletRequest request,
      HttpServletResponse response) {

    // Clear security context
    SecurityContextHolder.clearContext();

    // Clear remember-me cookie if exists
    Cookie[] cookies = request.getCookies();
    if (cookies != null) {
      for (Cookie cookie : cookies) {
        if (REMEMBER_ME_COOKIE_NAME.equals(cookie.getName())) {
          cookie.setValue("");
          cookie.setPath("/");
          cookie.setMaxAge(0);
          response.addCookie(cookie);
          break;
        }
      }
    }

    Map<String, String> responseBody = new HashMap<>();
    responseBody.put("message", "Logout successful");

    return ResponseEntity.ok(responseBody);
  }

  /**
   * Check if a user is authenticated via JWT token or remember-me cookie
   *
   * @param request HTTP request for accessing cookies
   * @return user details if authenticated
   */
  @GetMapping("/me")
  public ResponseEntity<?> getCurrentUser(HttpServletRequest request) {
    // First try to get user from security context (JWT token)
    Optional<User> currentUser = authService.getCurrentUser();

    // If not authenticated via JWT, try remember-me cookie
    if (currentUser.isEmpty()) {
      Cookie[] cookies = request.getCookies();
      if (cookies != null) {
        for (Cookie cookie : cookies) {
          if (REMEMBER_ME_COOKIE_NAME.equals(cookie.getName())) {
            currentUser = authService.validateRememberMeToken(cookie.getValue());
            break;
          }
        }
      }
    }

    if (currentUser.isPresent()) {
      User user = currentUser.get();
      Map<String, Object> responseBody = new HashMap<>();
      responseBody.put("username", user.getUsername());
      responseBody.put("email", user.getEmail());
      responseBody.put("firstName", user.getFirstName());
      responseBody.put("lastName", user.getLastName());

      return ResponseEntity.ok(responseBody);
    } else {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
  }

  /**
   * Check if the current user has a specific role
   *
   * @param role the role to check
   * @return true if the user has the role, false otherwise
   */
  @GetMapping("/has-role/{role}")
  public ResponseEntity<?> hasRole(@PathVariable String role) {
    boolean hasRole = authService.hasRole(role);

    Map<String, Boolean> response = new HashMap<>();
    response.put("hasRole", hasRole);

    return ResponseEntity.ok(response);
  }

  /**
   * Login request data class
   */
  public static class LoginRequest {
    private String username;
    private String password;
    private boolean rememberMe;

    public LoginRequest() {
    }

    public String getUsername() {
      return username;
    }

    public void setUsername(String username) {
      this.username = username;
    }

    public String getPassword() {
      return password;
    }

    public void setPassword(String password) {
      this.password = password;
    }

    public boolean isRememberMe() {
      return rememberMe;
    }

    public void setRememberMe(boolean rememberMe) {
      this.rememberMe = rememberMe;
    }
  }

  /**
   * Registration request data class
   */
  public static class RegisterRequest {
    private String username;
    private String email;
    private String password;
    private String firstName;
    private String lastName;

    public RegisterRequest() {
    }

    public String getUsername() {
      return username;
    }

    public void setUsername(String username) {
      this.username = username;
    }

    public String getEmail() {
      return email;
    }

    public void setEmail(String email) {
      this.email = email;
    }

    public String getPassword() {
      return password;
    }

    public void setPassword(String password) {
      this.password = password;
    }

    public String getFirstName() {
      return firstName;
    }

    public void setFirstName(String firstName) {
      this.firstName = firstName;
    }

    public String getLastName() {
      return lastName;
    }

    public void setLastName(String lastName) {
      this.lastName = lastName;
    }
  }
}