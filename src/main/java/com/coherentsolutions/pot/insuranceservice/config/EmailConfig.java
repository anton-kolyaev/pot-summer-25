package com.coherentsolutions.pot.insuranceservice.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for email service.
 *
 * <p>This class provides email-related configuration and ensures
 * the email service is only loaded when email is enabled.
 */
@Configuration
@ConditionalOnProperty(name = "spring.mail.host", matchIfMissing = false)
public class EmailConfig {
    
    // Email configuration is handled by Spring Boot's auto-configuration
    // when spring.mail.host is provided in application.yml
    
} 