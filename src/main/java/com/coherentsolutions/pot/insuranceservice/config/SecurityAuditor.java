package com.coherentsolutions.pot.insuranceservice.config;

import java.util.UUID;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Resolve the current user's UUID.
 * Falls back to a well-known SYSTEM UUID when unauthenticated.
 */
public final class SecurityAuditor {

  public static final UUID SYSTEM = UUID.fromString("00000000-0000-0000-0000-000000000000");

  private SecurityAuditor() { }

  public static UUID currentUserOrSystem() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null) return SYSTEM;
    try {
      return UUID.fromString(auth.getName());
    } catch (Exception e) {
      return SYSTEM;
    }
  }
}
