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
 * Filter that checks for a valid JWT token in each request.
 * If a valid token is found, it sets the authentication in the security
 * context.
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private JwtTokenUtil jwtTokenUtil;

  public JwtAuthenticationFilter(JwtTokenUtil jwtTokenUtil) {
    this.jwtTokenUtil = jwtTokenUtil;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    String token = jwtTokenUtil.resolveToken(request);

    if (token != null && jwtTokenUtil.validateToken(token)) {
      Authentication auth = jwtTokenUtil.getAuthentication(token);
      SecurityContextHolder.getContext().setAuthentication(auth);
    }

    filterChain.doFilter(request, response);
  }
}