package com.coherentsolutions.pot.insuranceservice.config;

import java.util.UUID;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Resolve the current user's UUID. Falls back to a well-known SYSTEM UUID when unauthenticated.
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SecurityAuditor {

  public static final UUID SYSTEM = UUID.fromString("00000000-0000-0000-0000-000000000000");

  public static UUID currentUserOrSystem() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null) {
      log.warn("SecurityContext has no Authentication. Defaulting auditor to SYSTEM {}.", SYSTEM);
      return SYSTEM;
    }
    try {
      return UUID.fromString(auth.getName());
    } catch (Exception e) {
      log.error("Unexpected error resolving current auditor. Using SYSTEM {}.", SYSTEM, e);
      return SYSTEM;
    }
  }
}
