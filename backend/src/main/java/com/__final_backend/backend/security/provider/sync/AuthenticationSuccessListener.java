package com.__final_backend.backend.security.provider.sync;

import com.__final_backend.backend.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationSuccessListener {

  private final XmlToDbUserSynchronizer synchronizer;

  @Autowired
  public AuthenticationSuccessListener(XmlToDbUserSynchronizer synchronizer) {
    this.synchronizer = synchronizer;
  }

  @EventListener
  public void onAuthenticationSuccess(AuthenticationSuccessEvent event) {
    if (event.getAuthentication().getPrincipal() instanceof UserDetails) {
      UserDetails userDetails = (UserDetails) event.getAuthentication().getPrincipal();
      synchronizer.synchronizeUser(userDetails.getUsername());
    }
  }
}
