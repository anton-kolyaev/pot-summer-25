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
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

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
      if (auth instanceof JwtAuthenticationToken) {
        return Optional.of(UUID.fromString(auth.getName()));
      }
      // Branch is hit when the SecurityContext doesn’t contain a valid JWT-based Authentication,
      // e.g. during scheduled jobs or anonymous/unauthenticated requests.
      return Optional.of(SYSTEM);
    }
  }
}
