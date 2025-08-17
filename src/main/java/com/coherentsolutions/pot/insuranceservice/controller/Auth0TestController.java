package com.coherentsolutions.pot.insuranceservice.controller;

import com.coherentsolutions.pot.insuranceservice.service.Auth0TicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Test controller for Auth0 functionality.
 * This controller is only available when Auth0 is enabled.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth/test")
@Tag(name = "Auth0 Test", description = "Test endpoints for Auth0 functionality")
@ConditionalOnProperty(name = "auth0.enabled", havingValue = "true", matchIfMissing = false)
public class Auth0TestController {

  private final Auth0TicketService auth0TicketService;

  /**
   * Test endpoint to create a password change ticket.
   *
   * @param email the email address
   * @return the ticket URL
   */
  @GetMapping("/password-change-ticket")
  @Operation(
      summary = "Create password change ticket",
      description = "Creates a password change ticket for the specified email"
  )
  public ResponseEntity<String> createPasswordChangeTicket(@RequestParam String email) {
    String ticketUrl = auth0TicketService.createPasswordChangeTicketByEmail(email);
    return ResponseEntity.ok(ticketUrl);
  }
}
