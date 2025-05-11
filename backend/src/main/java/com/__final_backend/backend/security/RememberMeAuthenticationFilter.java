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
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Filter that checks for remember-me cookies and authenticates users
 * accordingly.
 * This runs after the JWT authentication filter to handle cases where JWT token
 * is not present
 * but a valid remember-me cookie exists.
 */
public class RememberMeAuthenticationFilter extends OncePerRequestFilter {

  private final AuthService authService;
  private static final String REMEMBER_ME_COOKIE_NAME = "remember-me";

  public RememberMeAuthenticationFilter(AuthService authService) {
    this.authService = authService;
  }

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

    filterChain.doFilter(request, response);
  }

  /**
   * Process the remember-me token and set authentication if valid
   * 
   * @param token the remember-me token
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
          null,
          authorities);

      SecurityContextHolder.getContext().setAuthentication(authentication);
    }
  }
}