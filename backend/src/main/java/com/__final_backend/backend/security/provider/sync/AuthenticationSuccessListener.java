package com.__final_backend.backend.security.provider.sync;

import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

/**
 * Listens for successful authentication events to trigger user synchronization.
 * <p>
 * This component captures Spring Security's authentication success events and
 * uses
 * the {@link XmlToDbUserSynchronizer} to ensure that users authenticated via
 * XML
 * are properly synchronized to the database for relational integrity.
 * <p>
 * The listener operates transparently without affecting the authentication
 * process,
 * enabling the application to maintain database records for users while
 * authenticating
 * against an external XML source.
 */
@Component
public class AuthenticationSuccessListener {

  private final XmlToDbUserSynchronizer synchronizer;

  /**
   * Creates a new authentication success listener.
   *
   * @param synchronizer the user synchronizer that will handle XML-to-database
   *                     synchronization
   */
  public AuthenticationSuccessListener(XmlToDbUserSynchronizer synchronizer) {
    this.synchronizer = synchronizer;
  }

  /**
   * Handles successful authentication events by triggering user synchronization.
   * <p>
   * When a user successfully authenticates, this method extracts their username
   * and
   * passes it to the synchronizer to ensure the user exists in the database.
   * The method only processes events where the principal is an instance of
   * {@link UserDetails}, which is the standard Spring Security representation
   * of an authenticated user.
   *
   * @param event the authentication success event containing the authenticated
   *              user's details
   */
  @EventListener
  public void onAuthenticationSuccess(AuthenticationSuccessEvent event) {
    if (event.getAuthentication().getPrincipal() instanceof UserDetails) {
      UserDetails userDetails = (UserDetails) event.getAuthentication().getPrincipal();
      synchronizer.synchronizeUser(userDetails.getUsername());
    }
  }
}
