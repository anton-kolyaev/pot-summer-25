package com.coherentsolutions.pot.insuranceservice.config;

import com.auth0.client.mgmt.ManagementAPI;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

/**
 * Configuration class for Auth0 Management API integration.
 *
 * <p>This class creates a configured ManagementAPI instance for interacting
 * with Auth0's Management API using properties from Auth0Properties.
 */
@Configuration
public class Auth0Config {

  /**
   * Creates and configures the Auth0 Management API client.
   * Only creates the bean if Auth0 domain and client ID are properly configured.
   *
   * @param auth0Properties the Auth0 configuration properties
   * @return configured ManagementAPI instance
   */
  @Bean
  @ConditionalOnProperty(name = "auth0.enabled", havingValue = "true", matchIfMissing = false)
  public ManagementAPI managementAPI(Auth0Properties auth0Properties) {
    // Check if Auth0 is properly configured
    if (!StringUtils.hasText(auth0Properties.domain()) 
        || !StringUtils.hasText(auth0Properties.apiToken())) {
      throw new IllegalStateException(
          "Auth0 configuration is incomplete. Please set AUTH0_DOMAIN and AUTH0_API_TOKEN "
          + "environment variables.");
    }
    
    return ManagementAPI.newBuilder(auth0Properties.domain(), auth0Properties.apiToken())
        .build();
  }
} 