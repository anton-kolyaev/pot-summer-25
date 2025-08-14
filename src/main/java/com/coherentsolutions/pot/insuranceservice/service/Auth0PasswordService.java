package com.coherentsolutions.pot.insuranceservice.service;

import com.coherentsolutions.pot.insuranceservice.config.Auth0Properties;
import com.coherentsolutions.pot.insuranceservice.exception.Auth0Exception;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * Service for handling Auth0 password change operations.
 *
 * <p>This service provides functionality to send password change emails
 * using Auth0's Tickets API /api/v2/tickets/password-change endpoint.
 */
@Slf4j
@Service
@ConditionalOnProperty(name = "auth0.enabled", havingValue = "true", matchIfMissing = false)
public class Auth0PasswordService {

  private final Auth0Properties auth0Properties;
  private final Auth0TicketService auth0TicketService;

  public Auth0PasswordService(Auth0Properties auth0Properties, Auth0TicketService auth0TicketService) {
    this.auth0Properties = auth0Properties;
    this.auth0TicketService = auth0TicketService;
  }

  /**
   * Sends a password change email to the specified user using Auth0 Tickets API.
   *
   * @param userEmail the email address of the user
   * @return the response message from Auth0
   * @throws Auth0Exception if the request fails
   */
  public String sendPasswordChangeEmail(String userEmail) {
    if (!auth0Properties.enabled()) {
      throw new Auth0Exception("Auth0 integration is disabled");
    }

    try {
      log.info("Creating password change ticket for user with email: {}", userEmail);
      
      // Create password change ticket using Auth0 Tickets API
      String ticketUrl = auth0TicketService.createPasswordChangeTicketByEmail(userEmail);
      
      log.info("Successfully created password change ticket for user: {}. Ticket URL: {}", userEmail, ticketUrl);
      
      // Auth0 will automatically send the email with the ticket URL
      // We return a success message similar to the old API
      return "We've just sent you an email to change your password.";
      
    } catch (Exception e) {
      log.error("Failed to send password change email to user: {}", userEmail, e);
      throw new Auth0Exception("Error sending password change email: " + e.getMessage(), e);
    }
  }
}
