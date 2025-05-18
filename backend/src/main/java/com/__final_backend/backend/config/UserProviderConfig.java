package com.__final_backend.backend.config;

import com.__final_backend.backend.security.provider.DatabaseUserProvider;
import com.__final_backend.backend.security.provider.UserProvider;
import com.__final_backend.backend.security.provider.XmlUserProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Configuration class for user authentication providers.
 *
 * <p>
 * This class configures the appropriate user provider implementation based on
 * application properties. It supports multiple authentication strategies
 * including
 * database and XML-based user storage.
 */
@Configuration
public class UserProviderConfig {
  @Value("${app.auth.provider:database}")
  private String authProvider;

  /**
   * Configures and provides the appropriate user provider implementation.
   *
   * <p>
   * This method selects between database and XML-based user providers based on
   * the
   * {@code app.auth.provider} application property. If no provider is specified
   * or an
   * unknown value is provided, it defaults to the database provider.
   *
   * @param databaseUserProvider The database-backed user provider implementation
   * @param xmlUserProvider      The XML file-backed user provider implementation
   * @return The configured user provider implementation based on application
   *         configuration
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
