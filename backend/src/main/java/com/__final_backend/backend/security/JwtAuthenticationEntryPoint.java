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
 * Entry point for handling authentication exceptions in the JWT security flow.
 * <p>
 * This component provides a consistent handling mechanism for authentication
 * failures
 * by converting Spring Security's AuthenticationException into a standardized
 * JSON response.
 * It is invoked whenever an unauthenticated user attempts to access a protected
 * resource
 * and authentication is required.
 * <p>
 * The response includes detailed information about the error including status
 * code,
 * error type, descriptive message, and the request path that triggered the
 * exception.
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

  /** Jackson object mapper for serializing error response to JSON. */
  private final ObjectMapper objectMapper = new ObjectMapper();

  /**
   * Commences an authentication entry point with the specified response.
   * <p>
   * This method is invoked when an AuthenticationException is thrown during the
   * authentication
   * process. It prepares and sends a standardized JSON error response with HTTP
   * status 401
   * (Unauthorized).
   * <p>
   * The response includes:
   * <ul>
   * <li>HTTP status code (401)</li>
   * <li>Error type ("Unauthorized")</li>
   * <li>Descriptive message (either from the exception or a default)</li>
   * <li>Request path that triggered the exception</li>
   * </ul>
   *
   * @param request       the HTTP request that resulted in an authentication
   *                      exception
   * @param response      the HTTP response to be populated
   * @param authException the exception that triggered this entry point
   * @throws IOException if an I/O error occurs during response writing
   */
  @Override
  public void commence(HttpServletRequest request, HttpServletResponse response,
      AuthenticationException authException) throws IOException {

    // Configure the response with appropriate content type and status code
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

    // Create and populate the response body
    Map<String, Object> body = new HashMap<>();
    body.put("status", HttpServletResponse.SC_UNAUTHORIZED);
    body.put("error", "Unauthorized");
    body.put("message", authException.getMessage() != null ? authException.getMessage() : "Authentication required");
    body.put("path", request.getServletPath());

    // Serialize and write the response
    objectMapper.writeValue(response.getOutputStream(), body);
  }
}