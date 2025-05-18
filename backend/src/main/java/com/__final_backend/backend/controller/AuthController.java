package com.__final_backend.backend.controller;

import com.__final_backend.backend.entity.User;
import com.__final_backend.backend.security.JwtTokenUtil;
import com.__final_backend.backend.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
 * Controller for authentication operations in the SkyExplorer application.
 * <p>
 * This controller handles all authentication-related endpoints including user
 * registration,
 * login with JWT token generation, logout, and authentication status
 * verification.
 * It also provides remember-me functionality using secure cookies and
 * role-based
 * authorization checks.
 * </p>
 * <p>
 * The controller implements security best practices including:
 * </p>
 * <ul>
 * <li>Secure cookie handling with HttpOnly flag</li>
 * <li>JWT token authentication</li>
 * <li>Remember-me functionality with token validation</li>
 * <li>Role-based access control</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

  private final AuthenticationManager authenticationManager;
  private final JwtTokenUtil jwtTokenUtil;
  private final AuthService authService;
  private static final String REMEMBER_ME_COOKIE_NAME = "remember-me";
  private static final int REMEMBER_ME_COOKIE_MAX_AGE = 14 * 24 * 60 * 60; // 14 days in seconds

  /**
   * Constructor for dependency injection of authentication components.
   * <p>
   * Creates a new authentication controller with the required dependencies.
   * </p>
   * 
   * @param authenticationManager Spring Security authentication manager
   * @param jwtTokenUtil          utility for JWT token generation and validation
   * @param authService           service handling authentication operations
   */
  public AuthController(AuthenticationManager authenticationManager, JwtTokenUtil jwtTokenUtil,
      AuthService authService) {
    this.authenticationManager = authenticationManager;
    this.jwtTokenUtil = jwtTokenUtil;
    this.authService = authService;
  }

  /**
   * Registers a new user in the system.
   * <p>
   * This endpoint creates a new user account with the provided registration
   * details.
   * It performs validation and returns appropriate error messages if requirements
   * are not met (e.g., username already exists, invalid email format).
   * </p>
   * 
   * @param registerRequest DTO containing user registration details (username,
   *                        email, password, etc.)
   * @return ResponseEntity with success message and username or error details
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
   * Authenticates a user and issues a JWT token for authorized API access.
   * <p>
   * This endpoint validates user credentials, generates a JWT token upon
   * successful
   * authentication, and optionally sets a remember-me cookie for persistent
   * sessions.
   * The JWT token should be included in subsequent API requests as a Bearer
   * token.
   * </p>
   *
   * @param loginRequest DTO containing login credentials and remember-me
   *                     preference
   * @param response     HTTP response for setting cookies
   * @return ResponseEntity with JWT token or error message
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
   * Logs out the current user by invalidating their authentication.
   * <p>
   * This endpoint clears the security context and removes any remember-me cookies
   * to terminate the user's session. After logout, the client should discard
   * the JWT token.
   * </p>
   *
   * @param request  HTTP request for accessing cookies
   * @param response HTTP response for clearing cookies
   * @return ResponseEntity with success message
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
   * Retrieves information about the currently authenticated user.
   * <p>
   * This endpoint attempts to identify the current user through either:
   * </p>
   * <ol>
   * <li>JWT token in the Authorization header</li>
   * <li>Remember-me cookie</li>
   * </ol>
   * <p>
   * It returns user details if authenticated or a 401 Unauthorized response
   * otherwise.
   * </p>
   *
   * @param request HTTP request for accessing cookies
   * @return ResponseEntity with user details or 401 status
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
   * Checks if the currently authenticated user has a specific role.
   * <p>
   * This endpoint is useful for client-side authorization to conditionally
   * display UI elements based on the user's permissions.
   * </p>
   *
   * @param role the role name to check (e.g., "ADMIN", "USER")
   * @return ResponseEntity with boolean indicating if user has the specified role
   */
  @GetMapping("/has-role/{role}")
  public ResponseEntity<?> hasRole(@PathVariable String role) {
    boolean hasRole = authService.hasRole(role);

    Map<String, Boolean> response = new HashMap<>();
    response.put("hasRole", hasRole);

    return ResponseEntity.ok(response);
  }

  /**
   * Data Transfer Object (DTO) for login requests.
   * <p>
   * This inner class encapsulates the data sent by clients when attempting to log
   * in.
   * </p>
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
   * Data Transfer Object (DTO) for user registration requests.
   * <p>
   * This inner class encapsulates the data sent by clients when registering a new
   * user.
   * </p>
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