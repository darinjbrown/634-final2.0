package com.__final_backend.backend.config;

import com.__final_backend.backend.security.provider.DatabaseUserProvider;
import com.__final_backend.backend.security.provider.UserProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Configuration class for user provider.
 * Initially configures the database implementation as the primary provider.
 */
@Configuration
public class UserProviderConfig {

  /**
   * Configure the user provider bean.
   * For now, this simply returns the database implementation.
   * Later this will be extended to support switching between providers.
   * 
   * @param databaseUserProvider The database user provider implementation
   * @return The configured user provider
   */
  @Bean
  @Primary
  public UserProvider userProvider(DatabaseUserProvider databaseUserProvider) {
    return databaseUserProvider;
  }
}
