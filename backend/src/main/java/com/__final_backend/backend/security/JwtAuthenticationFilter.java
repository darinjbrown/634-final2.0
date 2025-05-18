package com.__final_backend.backend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filter that processes JWT authentication for incoming requests.
 * <p>
 * This filter inspects each incoming HTTP request for a valid JWT token. When a
 * valid token
 * is found, it extracts the user's authentication information and establishes
 * the security
 * context. This enables protected API endpoints to recognize the authenticated
 * user.
 * <p>
 * The filter extends Spring Security's OncePerRequestFilter to ensure it only
 * executes
 * once per request, regardless of request forwarding or includes.
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {
  /**
   * Utility for JWT token operations like resolution, validation and
   * authentication extraction.
   */
  private final JwtTokenUtil jwtTokenUtil;

  /**
   * Constructs a new JWT authentication filter.
   *
   * @param jwtTokenUtil the utility to use for JWT token operations
   */
  public JwtAuthenticationFilter(JwtTokenUtil jwtTokenUtil) {
    this.jwtTokenUtil = jwtTokenUtil;
  }

  /**
   * Processes a request through the JWT authentication filter.
   * <p>
   * This method performs the following steps:
   * <ol>
   * <li>Extracts the JWT token from the request headers</li>
   * <li>Validates the token if one is found</li>
   * <li>If valid, extracts the authentication information and establishes the
   * security context</li>
   * <li>Continues the filter chain regardless of authentication outcome</li>
   * </ol>
   * <p>
   * If no token is found or the token is invalid, the security context remains
   * unchanged
   * and the request continues as unauthenticated.
   *
   * @param request     the HTTP request being processed
   * @param response    the HTTP response being generated
   * @param filterChain the filter chain for invoking the next filter
   * @throws ServletException if a servlet exception occurs
   * @throws IOException      if an I/O error occurs during request processing
   */
  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    String token = jwtTokenUtil.resolveToken(request);

    if (token != null && jwtTokenUtil.validateToken(token)) {
      Authentication auth = jwtTokenUtil.getAuthentication(token);
      SecurityContextHolder.getContext().setAuthentication(auth);
    }

    // Continue filter chain regardless of authentication outcome
    filterChain.doFilter(request, response);
  }
}