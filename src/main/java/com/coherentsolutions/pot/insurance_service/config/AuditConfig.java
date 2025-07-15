package com.coherentsolutions.pot.insurance_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;
import java.util.UUID;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class AuditConfig {

    @Bean
    public AuditorAware<UUID> auditorProvider() {
        return () -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            
            if (authentication == null || 
                !authentication.isAuthenticated() || 
                "anonymousUser".equals(authentication.getPrincipal())) {
                // Return a default UUID for anonymous users
                return Optional.of(UUID.fromString("00000000-0000-0000-0000-000000000000"));
            }
            
            // Try to parse the principal as UUID, if it fails, generate a hash-based UUID
            try {
                return Optional.of(UUID.fromString(authentication.getName()));
            } catch (IllegalArgumentException e) {
                // If the principal is not a valid UUID, generate a hash-based UUID
                return Optional.of(UUID.nameUUIDFromBytes(authentication.getName().getBytes()));
            }
        };
    }
} 