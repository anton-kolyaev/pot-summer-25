package com.coherentsolutions.pot.insuranceservice.config;

import java.util.Optional;
import java.util.UUID;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Configuration class to enable JPA auditing and provide the auditor information.
 */
@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class AuditConfig {

  /**
   * Provides the current auditor's UUID for JPA auditing.
   *
   * <p>If the user is authenticated, returns a UUID generated from the
   * user's authentication name. Otherwise, returns a default anonymous UUID.
   */
  @Bean
  public AuditorAware<UUID> auditorProvider() {
    return () -> {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

      if (authentication == null
          ||
          !authentication.isAuthenticated()
          ||
          "anonymousUser".equals(authentication.getPrincipal())) {
        // Return a default UUID for anonymous users
        return Optional.of(UUID.fromString("00000000-0000-0000-0000-000000000000"));
      }

      // TODO: Refactor this when proper authentication is implemented to use actual user UUID
      // For now, generate a hash-based UUID from the authentication name
      return Optional.of(UUID.nameUUIDFromBytes(authentication.getName().getBytes()));
    };
  }
} 