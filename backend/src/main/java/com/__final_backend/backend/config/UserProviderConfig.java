package com.__final_backend.backend.config;

import com.__final_backend.backend.security.provider.DatabaseUserProvider;
import com.__final_backend.backend.security.provider.UserProvider;
import com.__final_backend.backend.security.provider.XmlUserProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Configuration class for user provider.
 * Configures the provider based on application properties.
 */
@Configuration
public class UserProviderConfig {

  @Value("${app.auth.provider:database}")
  private String authProvider;

  /**
   * Configure the user provider bean.
   * Returns the appropriate provider implementation based on configuration.
   * 
   * @param databaseUserProvider The database user provider implementation
   * @param xmlUserProvider      The XML user provider implementation
   * @return The configured user provider
   */
  @Bean
  @Primary
  public UserProvider userProvider(DatabaseUserProvider databaseUserProvider,
      XmlUserProvider xmlUserProvider) {
    switch (authProvider.toLowerCase()) {
      case "xml":
        return xmlUserProvider;
      case "database":
      default:
        return databaseUserProvider;
    }
  }
}
