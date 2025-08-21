package com.coherentsolutions.pot.insuranceservice.config;

import com.coherentsolutions.pot.insuranceservice.service.UserAuditorService;
import java.util.Optional;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Resolve the current user's UUID. Falls back to a well-known SYSTEM UUID when unauthenticated.
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SecurityAuditor {

  private static ApplicationContext applicationContext;

  @Autowired
  public static void setApplicationContext(ApplicationContext applicationContext) {
    SecurityAuditor.applicationContext = applicationContext;
  }

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
      log.debug("SecurityAuditor: Authentication principal name: {}", name);
      
      if (name == null || name.isBlank() || ("anonymousUser").equals(name)) {
        return warnFallback("Expected UUID principal but was anonymous/blank.");
      }
      
      // Check if the name is an Auth0 ID (format: auth0|...)
      if (name.startsWith("auth0|")) {
        log.debug("Auth0 ID detected: {}. Looking up local user UUID.", name);
        
        if (applicationContext != null) {
          try {
            UserAuditorService userAuditorService = applicationContext.getBean(UserAuditorService.class);
            Optional<UUID> localUserId = userAuditorService.findLocalUserIdByAuth0Id(name);
            if (localUserId.isPresent()) {
              log.debug("Found local user UUID: {} for Auth0 ID: {}", localUserId.get(), name);
              return localUserId.get();
            } else {
              log.warn("No local user found for Auth0 ID: {}, using SYSTEM UUID", name);
              return SYSTEM;
            }
          } catch (Exception e) {
            log.error("Error looking up local user UUID for Auth0 ID: {}", name, e);
            return SYSTEM;
          }
        } else {
          log.warn("ApplicationContext not available for Auth0 ID lookup: {}, using SYSTEM UUID", name);
          return SYSTEM;
        }
      }
      
      try {
        return UUID.fromString(name);
      } catch (IllegalArgumentException e) {
        return warnFallback("Expected UUID principal but got non-UUID name: " + name);
      }
    } catch (Exception e) {
      return errorFallback("Unexpected error resolving current auditor.", e);
    }
  }
}
