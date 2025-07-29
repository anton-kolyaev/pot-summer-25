package com.coherentsolutions.pot.insuranceservice.controller;

import com.auth0.client.mgmt.ManagementAPI;
import com.auth0.exception.Auth0Exception;
import com.coherentsolutions.pot.insuranceservice.config.Auth0Properties;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.HashMap;
import java.util.Map;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Health check controller for Auth0 connectivity.
 *
 * <p>This controller provides endpoints to test Auth0 connectivity and configuration
 * for production debugging and monitoring.
 */
@RestController
@RequestMapping("/api/v1/auth0/health")
@ConditionalOnProperty(name = "auth0.enabled", havingValue = "true", matchIfMissing = false)
@Tag(name = "Auth0 Health Check", description = "Health check endpoints for Auth0 connectivity")
public class Auth0HealthController {

  private final Auth0Properties auth0Properties;
  private final ManagementAPI managementAPI;

  public Auth0HealthController(Auth0Properties auth0Properties, ManagementAPI managementAPI) {
    this.auth0Properties = auth0Properties;
    this.managementAPI = managementAPI;
  }

  /**
   * Checks Auth0 configuration and connectivity.
   *
   * @return health status with configuration details
   */
  @GetMapping("/config")
  @Operation(summary = "Check Auth0 configuration", 
             description = "Returns Auth0 configuration status and details")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Configuration check successful"),
      @ApiResponse(responseCode = "500", description = "Configuration error")
  })
  public ResponseEntity<Map<String, Object>> checkConfiguration() {
    Map<String, Object> response = new HashMap<>();
    
    try {
      // Check configuration completeness
      boolean hasDomain = auth0Properties.domain() != null && !auth0Properties.domain().isEmpty();
      boolean hasApiToken = auth0Properties.apiToken() != null && !auth0Properties.apiToken().isEmpty();
      
      response.put("domain_configured", hasDomain);
      response.put("api_token_configured", hasApiToken);
      response.put("timeout", auth0Properties.timeout());
      response.put("audience", auth0Properties.audience());
      
      // Overall configuration status
      boolean fullyConfigured = hasDomain && hasApiToken;
      response.put("fully_configured", fullyConfigured);
      
      if (fullyConfigured) {
        response.put("status", "CONFIGURED");
        response.put("message", "Auth0 is properly configured");
      } else {
        response.put("status", "INCOMPLETE");
        response.put("message", "Auth0 configuration is incomplete");
      }
      
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      response.put("status", "ERROR");
      response.put("message", "Error checking configuration: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
  }

  /**
   * Tests Auth0 API connectivity using a lightweight API call.
   *
   * @return connectivity test results
   */
  @GetMapping("/connectivity")
  @Operation(summary = "Test Auth0 connectivity", 
             description = "Tests connectivity to Auth0 Management API using grants endpoint")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Connectivity test successful"),
      @ApiResponse(responseCode = "500", description = "Connectivity test failed")
  })
  public ResponseEntity<Map<String, Object>> testConnectivity() {
    Map<String, Object> response = new HashMap<>();
    
    try {
      // Test connectivity using grants endpoint as suggested in the review comment for faster response
      managementAPI.grants().list(null, null).execute();
      
      response.put("status", "CONNECTED");
      response.put("message", "Successfully connected to Auth0 Management API");
      response.put("test_method", "grants_endpoint");
      response.put("timestamp", System.currentTimeMillis());
      
      return ResponseEntity.ok(response);
    } catch (Auth0Exception e) {
      response.put("status", "CONNECTION_FAILED");
      response.put("message", "Failed to connect to Auth0: " + e.getMessage());
      response.put("error_code", e.getStatusCode());
      response.put("timestamp", System.currentTimeMillis());
      
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    } catch (Exception e) {
      response.put("status", "ERROR");
      response.put("message", "Unexpected error: " + e.getMessage());
      response.put("timestamp", System.currentTimeMillis());
      
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
  }

  /**
   * Returns comprehensive health status including configuration and connectivity.
   *
   * @return complete health status
   */
  @GetMapping
  @Operation(summary = "Complete Auth0 health check", 
             description = "Performs comprehensive health check including configuration and connectivity")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Health check successful"),
      @ApiResponse(responseCode = "500", description = "Health check failed")
  })
  public ResponseEntity<Map<String, Object>> healthCheck() {
    Map<String, Object> response = new HashMap<>();
    
    try {
      // Check configuration completeness
      boolean hasDomain = auth0Properties.domain() != null && !auth0Properties.domain().isEmpty();
      boolean hasApiToken = auth0Properties.apiToken() != null && !auth0Properties.apiToken().isEmpty();
      boolean fullyConfigured = hasDomain && hasApiToken;
      
      response.put("configuration", Map.of(
          "domain_configured", hasDomain,
          "api_token_configured", hasApiToken,
          "fully_configured", fullyConfigured,
          "timeout", auth0Properties.timeout(),
          "audience", auth0Properties.audience()
      ));
      
      if (!fullyConfigured) {
        response.put("status", "INCOMPLETE_CONFIG");
        response.put("message", "Auth0 configuration is incomplete - check domain and api_token properties");
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
      }
      
      // Test connectivity
      try {
        managementAPI.grants().list(null, null).execute();
        response.put("connectivity", Map.of(
            "status", "CONNECTED",
            "test_method", "grants_endpoint"
        ));
        response.put("status", "HEALTHY");
        response.put("message", "Auth0 is properly configured and connected");
      } catch (Auth0Exception e) {
        response.put("connectivity", Map.of(
            "status", "CONNECTION_FAILED",
            "error", e.getMessage(),
            "error_code", e.getStatusCode(),
            "test_method", "grants_endpoint"
        ));
        response.put("status", "CONNECTIVITY_ERROR");
        response.put("message", "Auth0 is configured but connection failed - check API token and network connectivity");
      }
      
      response.put("timestamp", System.currentTimeMillis());
      return ResponseEntity.ok(response);
      
    } catch (Exception e) {
      response.put("status", "ERROR");
      response.put("message", "Health check failed with unexpected error: " + e.getMessage());
      response.put("timestamp", System.currentTimeMillis());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
  }
} 