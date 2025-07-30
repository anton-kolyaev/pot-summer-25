package com.coherentsolutions.pot.insuranceservice.config;

import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class AuditConfig {

  @Bean
  public AuditorAware<String> auditorProvider() {
    return new SecurityAuditorAware();
  }

  private static class SecurityAuditorAware implements AuditorAware<String> {

    @NotNull
    @Override
    public Optional<String> getCurrentAuditor() {
      Authentication auth = SecurityContextHolder.getContext().getAuthentication();
      if (auth instanceof JwtAuthenticationToken jwt) {
        return Optional.of(jwt.getToken().getSubject());
      }
      return Optional.empty();
    }
  }
}
