package com.coherentsolutions.pot.insuranceservice.config;

import com.auth0.client.mgmt.ManagementAPI;
import com.auth0.exception.Auth0Exception;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Spring Boot Actuator Health Indicator for Auth0 connectivity.
 *
 * <p>This health indicator checks Auth0 configuration and connectivity,
 * integrating with Spring Boot's built-in health check system.
 */
@Component
@ConditionalOnProperty(name = "auth0.enabled", havingValue = "true", matchIfMissing = false)
public class Auth0HealthIndicator implements HealthIndicator {

  private final Auth0Properties auth0Properties;
  private final ManagementAPI managementAPI;

  public Auth0HealthIndicator(Auth0Properties auth0Properties, ManagementAPI managementAPI) {
    this.auth0Properties = auth0Properties;
    this.managementAPI = managementAPI;
  }

  @Override
  public Health health() {
    try {
      // Check configuration completeness
      boolean hasDomain = auth0Properties.domain() != null && !auth0Properties.domain().isEmpty();
      boolean hasApiToken = auth0Properties.apiToken() != null && !auth0Properties.apiToken().isEmpty();
      
      if (!hasDomain || !hasApiToken) {
        return Health.down()
            .withDetail("reason", "Incomplete configuration")
            .withDetail("domain_configured", hasDomain)
            .withDetail("api_token_configured", hasApiToken)
            .build();
      }

      // Test connectivity by making a lightweight API call
      // Using grants endpoint as suggested in the review comment for faster response
      managementAPI.grants().list(null, null).execute();
      
      return Health.up()
          .withDetail("domain", auth0Properties.domain())
          .withDetail("audience", auth0Properties.audience())
          .withDetail("timeout", auth0Properties.timeout())
          .build();
          
    } catch (Auth0Exception e) {
      return Health.down()
          .withDetail("reason", "Auth0 API connection failed")
          .withDetail("error", e.getMessage())
          .build();
    } catch (Exception e) {
      return Health.down()
          .withDetail("reason", "Unexpected error")
          .withDetail("error", e.getMessage())
          .build();
    }
  }
} 