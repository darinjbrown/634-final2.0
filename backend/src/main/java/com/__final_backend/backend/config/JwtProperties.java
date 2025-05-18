package com.__final_backend.backend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configuration properties for JWT authentication.
 *
 * <p>
 * This class manages JSON Web Token (JWT) related settings that can be
 * customized
 * through application properties with the prefix 'application.jwt'. It provides
 * default values
 * that can be overridden in the application configuration.
 */
@Component
@ConfigurationProperties(prefix = "application.jwt")
public class JwtProperties {

  // Default secret key used for signing JWT tokens
  private String secretKey = "5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437";

  // Token expiration time in milliseconds (default: 24 hours)
  private long accessTokenExpirationMs = 86400000;

  // Token prefix used in Authorization header
  private String tokenPrefix = "Bearer ";

  // HTTP header name for authentication token
  private String headerString = "Authorization";

  /**
   * Gets the secret key used for JWT token signing and validation.
   *
   * @return The secret key string
   */
  public String getSecretKey() {
    return secretKey;
  }

  /**
   * Sets the secret key used for JWT token signing and validation.
   *
   * @param secretKey The secret key string
   */
  public void setSecretKey(String secretKey) {
    this.secretKey = secretKey;
  }

  /**
   * Gets the access token expiration time in milliseconds.
   *
   * @return The token expiration time in milliseconds
   */
  public long getAccessTokenExpirationMs() {
    return accessTokenExpirationMs;
  }

  /**
   * Sets the access token expiration time in milliseconds.
   *
   * @param accessTokenExpirationMs The token expiration time in milliseconds
   */
  public void setAccessTokenExpirationMs(long accessTokenExpirationMs) {
    this.accessTokenExpirationMs = accessTokenExpirationMs;
  }

  /**
   * Gets the token prefix used in the Authorization header.
   *
   * @return The token prefix (e.g., "Bearer ")
   */
  public String getTokenPrefix() {
    return tokenPrefix;
  }

  /**
   * Sets the token prefix used in the Authorization header.
   *
   * @param tokenPrefix The token prefix (e.g., "Bearer ")
   */
  public void setTokenPrefix(String tokenPrefix) {
    this.tokenPrefix = tokenPrefix;
  }

  /**
   * Gets the HTTP header name used for authentication.
   *
   * @return The HTTP header name
   */
  public String getHeaderString() {
    return headerString;
  }

  /**
   * Sets the HTTP header name used for authentication.
   *
   * @param headerString The HTTP header name
   */
  public void setHeaderString(String headerString) {
    this.headerString = headerString;
  }
}