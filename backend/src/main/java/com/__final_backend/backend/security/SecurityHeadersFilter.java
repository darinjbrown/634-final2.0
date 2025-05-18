package com.__final_backend.backend.security;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Filter to add security headers to all HTTP responses.
 * <p>
 * This filter enhances the application's security posture by adding HTTP
 * response headers
 * that protect against common web vulnerabilities like XSS, MIME-sniffing,
 * clickjacking,
 * and information disclosure.
 * <p>
 * The filter is configured with the highest precedence to ensure security
 * headers are applied
 * before any other filters in the chain.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SecurityHeadersFilter implements Filter {
  /**
   * Applies security headers to the HTTP response and continues the filter chain.
   * <p>
   * This method intercepts all HTTP responses and adds security headers to
   * protect against
   * various web vulnerabilities. It preserves existing headers if they have
   * already been set
   * by other components.
   *
   * @param request  incoming servlet request
   * @param response outgoing servlet response
   * @param chain    filter chain to execute
   * @throws IOException      if an I/O error occurs during request processing
   * @throws ServletException if a servlet error occurs during request processing
   */
  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {

    HttpServletResponse httpResponse = (HttpServletResponse) response;

    // Add security headers to protect against common attacks //
    // X-Content-Type-Options: Prevents browsers from MIME-sniffing a response from
    // the declared content-type, which helps prevent content-type confusion attacks
    httpResponse.setHeader("X-Content-Type-Options", "nosniff"); // X-Frame-Options: Protects against clickjacking
                                                                 // attacks by controlling
    // whether a browser should be allowed to render a page in a <frame>, <iframe>
    // or <object>
    httpResponse.setHeader("X-Frame-Options", "SAMEORIGIN"); // Cache-Control: Prevents caching of sensitive information
                                                             // which could lead to
    // information disclosure if cached responses are accessed by unauthorized users
    if (!httpResponse.containsHeader("Cache-Control")) {
      httpResponse.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
    }

    // Pragma: Legacy HTTP/1.0 header for cache control
    if (!httpResponse.containsHeader("Pragma")) {
      httpResponse.setHeader("Pragma", "no-cache");
    } // X-XSS-Protection: Enables the cross-site scripting (XSS) filter built into
      // modern
    // browsers and stops page rendering when an attack is detected
    httpResponse.setHeader("X-XSS-Protection", "1; mode=block"); // Feature-Policy (now called Permissions-Policy):
                                                                 // Restricts which browser features
    // the application can use, limiting potential attack surface and privacy
    // concerns
    httpResponse.setHeader("Feature-Policy", "camera 'none'; microphone 'none'; geolocation 'none'");
    // Continue with the filter chain, passing the enhanced response with security
    // headers
    chain.doFilter(request, httpResponse);
  }

  /**
   * Called by the web container to indicate to a filter that it is being placed
   * into service.
   * <p>
   * This implementation is empty as no initialization is required.
   *
   * @param filterConfig the filter configuration object used by the servlet
   *                     container
   *                     to pass information to this filter
   */
  @Override
  public void init(FilterConfig filterConfig) {
    // No initialization required
  }

  /**
   * Called by the web container to indicate to a filter that it is being taken
   * out of service.
   * <p>
   * This implementation is empty as no cleanup is required.
   */
  @Override
  public void destroy() {
    // No cleanup required
  }
}