package com.__final_backend.backend.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Entry point for handling authentication exceptions.
 * Returns a proper JSON response when authentication fails.
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public void commence(HttpServletRequest request, HttpServletResponse response,
      AuthenticationException authException) throws IOException {

    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

    Map<String, Object> body = new HashMap<>();
    body.put("status", HttpServletResponse.SC_UNAUTHORIZED);
    body.put("error", "Unauthorized");
    body.put("message", authException.getMessage() != null ? authException.getMessage() : "Authentication required");
    body.put("path", request.getServletPath());

    objectMapper.writeValue(response.getOutputStream(), body);
  }
}