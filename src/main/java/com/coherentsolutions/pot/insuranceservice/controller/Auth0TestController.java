package com.coherentsolutions.pot.insuranceservice.controller;

import com.coherentsolutions.pot.insuranceservice.config.Auth0Properties;
import com.coherentsolutions.pot.insuranceservice.service.Auth0TicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Test controller for Auth0 Tickets API functionality.
 * This controller is for testing purposes only and should be removed in production.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/test/auth0")
@Tag(name = "Auth0 Test", description = "Test endpoints for Auth0 functionality")
@ConditionalOnProperty(name = "auth0.enabled", havingValue = "true", matchIfMissing = false)
public class Auth0TestController {

  private final Auth0TicketService auth0TicketService;
  private final Auth0Properties auth0Properties;

  /**
   * Test endpoint to create a password change ticket.
   *
   * @param email the email address to test
   * @return the ticket URL or error message
   */
  @PostMapping("/test-password-ticket")
  @Operation(
      summary = "Test password change ticket creation",
      description = "Creates a password change ticket for testing purposes"
  )
  public ResponseEntity<String> testPasswordTicket(@RequestParam String email) {
    try {
      log.info("Testing password change ticket creation for email: {}", email);
      
      String ticketUrl = auth0TicketService.createPasswordChangeTicketByEmail(email);
      
      log.info("Successfully created test ticket for email: {}. URL: {}", email, ticketUrl);
      
      return ResponseEntity.ok("Ticket created successfully. URL: " + ticketUrl);
      
    } catch (Exception e) {
      log.error("Failed to create test ticket for email: {}", email, e);
      return ResponseEntity.badRequest().body("Failed to create ticket: " + e.getMessage());
    }
  }

  /**
   * Test endpoint to get Auth0 configuration info.
   *
   * @return the configuration information
   */
  @PostMapping("/test-config")
  @Operation(
      summary = "Test Auth0 configuration",
      description = "Shows current Auth0 configuration for debugging"
  )
  public ResponseEntity<String> testConfig() {
    try {
      log.info("Testing Auth0 configuration");
      log.info("Domain: {}", auth0Properties.domain());
      log.info("Client ID: {}", auth0Properties.clientId());
      log.info("Connection: {}", auth0Properties.connection());
      log.info("Connection ID: {}", auth0Properties.connectionId());
      log.info("Enabled: {}", auth0Properties.enabled());
      
      return ResponseEntity.ok("Auth0 configuration logged - check application logs");
      
    } catch (Exception e) {
      log.error("Failed to test Auth0 configuration", e);
      return ResponseEntity.badRequest().body("Failed to test configuration: " + e.getMessage());
    }
  }
}
