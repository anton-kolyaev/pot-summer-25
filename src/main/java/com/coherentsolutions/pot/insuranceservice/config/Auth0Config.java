package com.coherentsolutions.pot.insuranceservice.config;

import com.auth0.client.mgmt.ManagementAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
   *
   * @param auth0Properties the Auth0 configuration properties
   * @return configured ManagementAPI instance
   */
  @Bean
  public ManagementAPI managementAPI(Auth0Properties auth0Properties) {
    return ManagementAPI.newBuilder(auth0Properties.domain(), auth0Properties.clientId())
        .build();
  }
} 