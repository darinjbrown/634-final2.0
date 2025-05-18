package com.__final_backend.backend.security.provider.sync;

import com.__final_backend.backend.entity.User;
import com.__final_backend.backend.repository.UserRepository;
import com.__final_backend.backend.security.provider.XmlUserProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Synchronizes user data between XML authentication source and the database.
 * <p>
 * This component ensures data consistency between external XML-based user
 * records and the
 * application's database. When the application is configured to use XML
 * authentication,
 * this synchronizer maintains a copy of user data in the database to support
 * relational
 * integrity with other entities that reference users.
 * <p>
 * The synchronizer only operates when the application is configured to use XML
 * authentication.
 * It handles both existing and newly registered users.
 */
@Component
public class XmlToDbUserSynchronizer {
  private final UserRepository userRepository;
  private final XmlUserProvider xmlUserProvider;
  private final boolean isXmlAuthProvider;

  /**
   * Creates a new XML to database user synchronizer.
   *
   * @param userRepository  repository for database user operations
   * @param xmlUserProvider provider for XML user operations
   * @param authProvider    configuration value indicating the authentication
   *                        provider to use,
   *                        defaults to "database" if not specified
   */
  public XmlToDbUserSynchronizer(
      UserRepository userRepository,
      XmlUserProvider xmlUserProvider,
      @Value("${app.auth.provider:database}") String authProvider) {
    this.userRepository = userRepository;
    this.xmlUserProvider = xmlUserProvider;
    this.isXmlAuthProvider = "xml".equalsIgnoreCase(authProvider);
  }

  /**
   * Ensures that the XML user exists in the database for relational integrity.
   * <p>
   * This method checks if a user exists in the XML source and synchronizes their
   * information
   * to the database. If the user already exists in the database, their
   * information is updated
   * if necessary. If the user doesn't exist in the database, a new database
   * record is created.
   * <p>
   * This synchronization is only performed when XML authentication is enabled.
   *
   * @param username the username of the authenticated user
   * @return the synchronized database user entity or null if XML authentication
   *         is disabled,
   *         or the user doesn't exist in the XML source
   */
  public User synchronizeUser(String username) {
    // Only operate in XML auth mode
    if (!isXmlAuthProvider) {
      return null;
    }

    // Get user from XML authentication source
    Optional<User> xmlUserOpt = xmlUserProvider.findByUsername(username);
    if (xmlUserOpt.isEmpty()) {
      return null; // User not found in XML source
    }

    User xmlUser = xmlUserOpt.get();

    // Check if user already exists in database
    Optional<User> dbUserOpt = userRepository.findByUsername(username);
    if (dbUserOpt.isPresent()) {
      // User exists in database, check if any updates are needed
      User existingDbUser = dbUserOpt.get();
      boolean needsUpdate = false;

      // Update email if it has changed in XML source
      if (!existingDbUser.getEmail().equals(xmlUser.getEmail())) {
        existingDbUser.setEmail(xmlUser.getEmail());
        needsUpdate = true;
      }

      // Update roles if they have changed in XML source
      if (!existingDbUser.getRoles().equals(xmlUser.getRoles())) {
        existingDbUser.setRoles(xmlUser.getRoles());
        needsUpdate = true;
      }

      // Save changes if any updates were made
      if (needsUpdate) {
        existingDbUser.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(existingDbUser);
      }

      return existingDbUser;
    } else {
      // User doesn't exist in database, create new database user from XML data
      User newDbUser = new User();
      newDbUser.setUsername(xmlUser.getUsername());
      newDbUser.setEmail(xmlUser.getEmail());
      newDbUser.setPasswordHash(xmlUser.getPasswordHash());
      newDbUser.setRoles(xmlUser.getRoles());
      newDbUser.setCreatedAt(LocalDateTime.now());
      newDbUser.setUpdatedAt(LocalDateTime.now());

      return userRepository.save(newDbUser);
    }
  }

  /**
   * Ensures that a newly registered XML user exists in the database.
   * <p>
   * This method should be called after successful registration in XML mode to
   * ensure
   * that the newly created user is properly synchronized with the database. It
   * leverages
   * the existing synchronization logic to avoid code duplication.
   * <p>
   * This synchronization is only performed when XML authentication is enabled.
   *
   * @param username the username of the registered user
   * @return the synchronized database user entity or null if XML authentication
   *         is disabled,
   *         or the user doesn't exist in the XML source
   */
  public User synchronizeNewUser(String username) {
    // Reuse existing synchronization logic
    return synchronizeUser(username);
  }
}
