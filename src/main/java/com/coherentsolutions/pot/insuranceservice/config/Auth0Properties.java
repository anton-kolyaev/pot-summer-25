package com.coherentsolutions.pot.insuranceservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for Auth0 Management API integration.
 *
 * <p>This record contains all Auth0-related configuration properties
 * that can be set via application.yml or environment variables.
 */
@ConfigurationProperties(prefix = "auth0")
public record Auth0Properties(
    String domain,
    String apiToken,
    String audience,
    String clientId,
    String connection,
    int timeout,
    boolean enabled
) {
  /*
   * Default constructor with default timeout value.
   */
  public Auth0Properties {
    if (timeout <= 0) {
      timeout = 10000; // Default timeout of 10 seconds
    }
  }
} 