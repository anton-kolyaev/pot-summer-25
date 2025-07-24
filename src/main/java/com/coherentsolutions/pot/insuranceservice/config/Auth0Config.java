package com.coherentsolutions.pot.insuranceservice.config;

import com.auth0.client.mgmt.ManagementAPI;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for Auth0 Management API integration.
 *
 * <p>This class provides configuration properties for Auth0 and creates a configured
 * ManagementAPI instance for interacting with Auth0's Management API.
 */
@Configuration
@ConfigurationProperties(prefix = "auth0")
public class Auth0Config {

  private String domain;
  private String clientId;
  private String clientSecret;
  private String audience;
  private int timeout = 10000;

  /**
   * Creates and configures the Auth0 Management API client.
   *
   * @return configured ManagementAPI instance
   */
  @Bean
  public ManagementAPI managementAPI() {
    return ManagementAPI.newBuilder(domain, clientId)
        .build();
  }

  // Getters and Setters
  public String getDomain() {
    return domain;
  }

  public void setDomain(String domain) {
    this.domain = domain;
  }

  public String getClientId() {
    return clientId;
  }

  public void setClientId(String clientId) {
    this.clientId = clientId;
  }

  public String getClientSecret() {
    return clientSecret;
  }

  public void setClientSecret(String clientSecret) {
    this.clientSecret = clientSecret;
  }

  public String getAudience() {
    return audience;
  }

  public void setAudience(String audience) {
    this.audience = audience;
  }

  public int getTimeout() {
    return timeout;
  }

  public void setTimeout(int timeout) {
    this.timeout = timeout;
  }
} 