package com.__final_backend.backend.security;

import com.__final_backend.backend.entity.User;
import com.__final_backend.backend.service.AuthService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Filter that authenticates users via remember-me cookies.
 * <p>
 * This filter provides persistent login functionality by checking for a
 * remember-me cookie
 * in the request. It runs after the JWT authentication filter to handle cases
 * where a JWT token
 * is not present but a valid remember-me cookie exists. When a valid token is
 * found, the user
 * is automatically authenticated for the duration of the session.
 * <p>
 * This implementation extends Spring Security's OncePerRequestFilter to ensure
 * it only
 * processes each request once per thread, regardless of forwards or includes.
 */
public class RememberMeAuthenticationFilter extends OncePerRequestFilter {
  /**
   * Service responsible for remember-me token validation and user authentication.
   */
  private final AuthService authService;

  /** Name of the cookie that contains the remember-me token. */
  private static final String REMEMBER_ME_COOKIE_NAME = "remember-me";

  /**
   * Constructs a new RememberMeAuthenticationFilter with the specified
   * authentication service.
   *
   * @param authService the service used to validate remember-me tokens and
   *                    retrieve user information
   */
  public RememberMeAuthenticationFilter(AuthService authService) {
    this.authService = authService;
  }

  /**
   * Performs the actual filtering for each request.
   * <p>
   * This method checks if the user is already authenticated. If not, it looks for
   * a remember-me
   * cookie and attempts to authenticate the user based on the cookie value. After
   * processing,
   * the filter chain continues regardless of authentication outcome.
   *
   * @param request     the HTTP request being processed
   * @param response    the HTTP response being generated
   * @param filterChain the filter chain for invoking the next filter
   * @throws ServletException if a servlet exception occurs
   * @throws IOException      if an I/O error occurs during request processing
   */
  @Override
  protected void doFilterInternal(
      HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    // Only proceed if not already authenticated (e.g., by JWT filter)
    if (SecurityContextHolder.getContext().getAuthentication() == null
        || !SecurityContextHolder.getContext().getAuthentication().isAuthenticated()
        || SecurityContextHolder.getContext().getAuthentication().getPrincipal().equals("anonymousUser")) {

      // Check for remember-me cookie
      Cookie[] cookies = request.getCookies();
      if (cookies != null) {
        for (Cookie cookie : cookies) {
          if (REMEMBER_ME_COOKIE_NAME.equals(cookie.getName())) {
            processRememberMeCookie(cookie.getValue());
            break;
          }
        }
      }
    }

    // Continue filter chain regardless of authentication outcome
    filterChain.doFilter(request, response);
  }

  /**
   * Processes a remember-me token and establishes authentication if the token is
   * valid.
   * <p>
   * This method validates the token using the authentication service. If valid,
   * it
   * retrieves the associated user, builds their authorities based on assigned
   * roles,
   * and creates an authenticated principal in the security context.
   * <p>
   * If the token is invalid or expired, no authentication is established.
   *
   * @param token the remember-me token extracted from the cookie
   */
  private void processRememberMeCookie(String token) {
    Optional<User> userOptional = authService.validateRememberMeToken(token);

    if (userOptional.isPresent()) {
      User user = userOptional.get();
      List<SimpleGrantedAuthority> authorities = new ArrayList<>();

      // Add user authorities from roles
      if (user.getRoles() != null && !user.getRoles().isEmpty()) {
        authorities.addAll(user.getRoles().stream()
            .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
            .collect(Collectors.toList()));
      } else {
        // Default to USER role if no roles are specified
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
      }

      // Create an authenticated token with proper roles
      Authentication authentication = new UsernamePasswordAuthenticationToken(
          user.getUsername(),
          null, // Credentials are null as we authenticate via token
          authorities);

      // Establish the authentication in the current security context
      SecurityContextHolder.getContext().setAuthentication(authentication);
    }
  }
}