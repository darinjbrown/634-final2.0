package com.__final_backend.backend.security.provider.sync;

import com.__final_backend.backend.entity.User;
import com.__final_backend.backend.repository.UserRepository;
import com.__final_backend.backend.security.provider.XmlUserProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class XmlToDbUserSynchronizer {

  private final UserRepository userRepository;
  private final XmlUserProvider xmlUserProvider;
  private final boolean isXmlAuthProvider;

  @Autowired
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
   * 
   * @param username The username of the authenticated user
   * @return The synchronized database user entity or null if not applicable
   */
  public User synchronizeUser(String username) {
    // Only operate in XML auth mode
    if (!isXmlAuthProvider) {
      return null;
    }

    // Get user from XML
    Optional<User> xmlUserOpt = xmlUserProvider.findByUsername(username);
    if (xmlUserOpt.isEmpty()) {
      return null; // User not found
    }

    User xmlUser = xmlUserOpt.get(); // Check if user exists in DB
    Optional<User> dbUserOpt = userRepository.findByUsername(username);

    if (dbUserOpt.isPresent()) {
      User existingDbUser = dbUserOpt.get();
      boolean needsUpdate = false;

      // Check if any fields need updating
      if (!existingDbUser.getEmail().equals(xmlUser.getEmail())) {
        existingDbUser.setEmail(xmlUser.getEmail());
        needsUpdate = true;
      }

      if (!existingDbUser.getRoles().equals(xmlUser.getRoles())) {
        existingDbUser.setRoles(xmlUser.getRoles());
        needsUpdate = true;
      }

      if (needsUpdate) {
        existingDbUser.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(existingDbUser);
      }

      return existingDbUser;
    } else {
      // Create new DB user based on XML user
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
   * This should be called after successful registration in XML mode.
   * 
   * @param username The username of the registered user
   * @return The synchronized database user entity or null if not applicable
   */
  public User synchronizeNewUser(String username) {
    // Reuse existing synchronization logic
    return synchronizeUser(username);
  }
}
