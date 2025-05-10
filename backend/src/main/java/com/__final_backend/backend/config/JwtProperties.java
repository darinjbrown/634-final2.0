package com.__final_backend.backend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configuration properties for JWT authentication.
 */
@Component
@ConfigurationProperties(prefix = "application.jwt")
public class JwtProperties {

  private String secretKey = "5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437";
  private long accessTokenExpirationMs = 86400000; // 24 hours
  private String tokenPrefix = "Bearer ";
  private String headerString = "Authorization";

  public String getSecretKey() {
    return secretKey;
  }

  public void setSecretKey(String secretKey) {
    this.secretKey = secretKey;
  }

  public long getAccessTokenExpirationMs() {
    return accessTokenExpirationMs;
  }

  public void setAccessTokenExpirationMs(long accessTokenExpirationMs) {
    this.accessTokenExpirationMs = accessTokenExpirationMs;
  }

  public String getTokenPrefix() {
    return tokenPrefix;
  }

  public void setTokenPrefix(String tokenPrefix) {
    this.tokenPrefix = tokenPrefix;
  }

  public String getHeaderString() {
    return headerString;
  }

  public void setHeaderString(String headerString) {
    this.headerString = headerString;
  }
}