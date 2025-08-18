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

  private static UUID warnFallback(String reason) {
    log.warn("{} Defaulting auditor to SYSTEM {}.", reason, SYSTEM);
    return SYSTEM;
  }

  private static UUID errorFallback(String reason, Throwable t) {
    log.error("{} Defaulting auditor to SYSTEM {}.", reason, SYSTEM, t);
    return SYSTEM;
  }

  public static UUID currentUserOrSystem() {
    try {
      Authentication auth = SecurityContextHolder.getContext().getAuthentication();
      if (auth == null || !auth.isAuthenticated()) {
        return warnFallback("SecurityContext has no authenticated principal.");
      }
      String name = auth.getName();
      if (name == null || name.isBlank() || ("anonymousUser").equals(name)) {
        return warnFallback("Expected UUID principal but was anonymous/blank.");
      }
      try {
        return UUID.fromString(name);
      } catch (IllegalArgumentException e) {
        return warnFallback("Expected UUID principal but got non-UUID name.");
      }
    } catch (Exception e) {
      return errorFallback("Unexpected error resolving current auditor.", e);
    }
  }
}
