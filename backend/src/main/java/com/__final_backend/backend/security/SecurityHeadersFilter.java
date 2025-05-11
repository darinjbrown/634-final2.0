package com.__final_backend.backend.security;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Filter to add security headers to all HTTP responses.
 * These headers help protect against various common web vulnerabilities.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SecurityHeadersFilter implements Filter {

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {

    HttpServletResponse httpResponse = (HttpServletResponse) response;

    // Add security headers to protect against common attacks

    // X-Content-Type-Options: Prevents browsers from MIME-sniffing a response from
    // the declared content-type
    httpResponse.setHeader("X-Content-Type-Options", "nosniff");

    // X-Frame-Options: Protects against clickjacking attacks
    httpResponse.setHeader("X-Frame-Options", "SAMEORIGIN");

    // Cache-Control: Prevents caching of sensitive information
    if (!httpResponse.containsHeader("Cache-Control")) {
      httpResponse.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
    }

    // Pragma: Legacy HTTP/1.0 header for cache control
    if (!httpResponse.containsHeader("Pragma")) {
      httpResponse.setHeader("Pragma", "no-cache");
    }

    // X-XSS-Protection: Stops pages from loading when XSS is detected
    httpResponse.setHeader("X-XSS-Protection", "1; mode=block");

    // Feature-Policy: Restricts which browser features the app can use
    httpResponse.setHeader("Feature-Policy", "camera 'none'; microphone 'none'; geolocation 'none'");

    // Continue with the filter chain
    chain.doFilter(request, httpResponse);
  }
}