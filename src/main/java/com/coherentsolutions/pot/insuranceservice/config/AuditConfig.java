package com.coherentsolutions.pot.insuranceservice.config;

import java.util.Optional;
import java.util.UUID;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.lang.NonNull;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class AuditConfig {

  @Bean
  public AuditorAware<UUID> auditorProvider() {
    return new SecurityAuditorAware();
  }

  private static class SecurityAuditorAware implements AuditorAware<UUID> {

    @NonNull
    @Override
    public Optional<UUID> getCurrentAuditor() {
      Authentication auth = SecurityContextHolder.getContext().getAuthentication();
      if (auth == null) {
        return Optional.of(SYSTEM);
      }
      
      String authName = auth.getName();
      if (authName == null || authName.trim().isEmpty()) {
        return Optional.of(SYSTEM);
      }
      
      try {
        return Optional.of(UUID.fromString(authName));
      } catch (IllegalArgumentException e) {
        // If the name is not a valid UUID, fall back to SYSTEM
        return Optional.of(SYSTEM);
      }
    }
  }
}
