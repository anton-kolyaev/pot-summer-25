package com.coherentsolutions.pot.insuranceservice.config;

import java.util.Optional;
import java.util.UUID;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class AuditConfig {

  @Bean
  public AuditorAware<UUID> auditorProvider() {
    return new SecurityAuditorAware();
  }

  private static final UUID SYSTEM = UUID.fromString("00000000-0000-0000-0000-000000000000");

  private static class SecurityAuditorAware implements AuditorAware<UUID> {

    @NonNull
    @Override
    public Optional<UUID> getCurrentAuditor() {
      Authentication auth = SecurityContextHolder.getContext().getAuthentication();

      if (auth == null || !auth.isAuthenticated()) {
        return Optional.of(SYSTEM);
      }
      // Branch is hit when the SecurityContext doesn’t contain a valid JWT-based Authentication,
      // e.g. during scheduled jobs or anonymous/unauthenticated requests.
      var authUuid = auth.getName() != null ? UUID.fromString(auth.getName()) : SYSTEM;
      return Optional.of(authUuid);
    }
  }
}
