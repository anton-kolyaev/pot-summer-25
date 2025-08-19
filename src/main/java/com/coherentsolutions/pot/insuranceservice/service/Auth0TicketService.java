package com.coherentsolutions.pot.insuranceservice.service;

import com.auth0.client.mgmt.ManagementAPI;
import com.auth0.client.mgmt.filter.UserFilter;
import com.auth0.json.mgmt.users.UsersPage;
import com.coherentsolutions.pot.insuranceservice.config.Auth0Properties;
import com.coherentsolutions.pot.insuranceservice.exception.Auth0Exception;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

/**
 * Service for handling Auth0 Tickets API operations.
 *
 * <p>This service provides functionality to create password change tickets
 * using Auth0's Management API /api/v2/tickets/password-change endpoint.
 */
@Slf4j
@Service
@ConditionalOnProperty(name = "auth0.enabled", havingValue = "true", matchIfMissing = false)
public class Auth0TicketService {

  private final ManagementAPI managementAPI;
  private final Auth0Properties auth0Properties;
  private final RestClient restClient;

  public Auth0TicketService(ManagementAPI managementAPI, Auth0Properties auth0Properties, RestClient restClient) {
    this.managementAPI = managementAPI;
    this.auth0Properties = auth0Properties;
    this.restClient = restClient;
  }

  /**
   * Creates a password change ticket for the specified user.
   *
   * @param userId the Auth0 user ID
   * @param email the user's email address
   * @return the ticket URL for password change
   * @throws Auth0Exception if the ticket creation fails
   */
  public String createPasswordChangeTicket(String userId, String email) throws Auth0Exception {
    try {
      log.info("Creating password change ticket for user: {} with email: {}", userId, email);

      // Request body for password change ticket
      Map<String, Object> body = new HashMap<>();
      body.put("user_id", userId);
      body.put("client_id", auth0Properties.clientId());
      
      // For Tickets API, we need connection_id, not connection name
      // If connection_id is not available, we'll skip it and let Auth0 use the default
      String connectionId = auth0Properties.connectionId();
      if (connectionId != null && !connectionId.trim().isEmpty()) {
        body.put("connection_id", connectionId);
        log.info("Using connection_id: {}", connectionId);
      } else {
        log.warn("No connection_id configured, Auth0 will use default connection");
      }
      
      log.info("Creating ticket with body: {}", body);
      
      // Optional: Set ticket expiration (default is 24 hours)
      // body.put("ttl_seconds", 86400); // 24 hours
      
      // Optional: Set result URL (where user will be redirected after password change)
      // body.put("result_url", "https://your-app.com/password-changed");

      // Set headers with Management API token
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      headers.setBearerAuth(auth0Properties.apiToken());
      
      log.info("Using domain: {}, client_id: {}, connection: {}", 
               auth0Properties.domain(), auth0Properties.clientId(), auth0Properties.connection());

      HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

      // Make POST request to create ticket
      final String url = "https://" + auth0Properties.domain() + "/api/v2/tickets/password-change";
      ResponseEntity<Map> response = restClient.post()
          .uri(url)
          .headers(httpHeaders -> {
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);
            httpHeaders.setBearerAuth(auth0Properties.apiToken());
          })
          .body(body)
          .retrieve()
          .toEntity(Map.class);

      if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
        String ticketUrl = (String) response.getBody().get("ticket");
        if (ticketUrl != null) {
          log.info("Successfully created password change ticket for user: {}. Ticket URL: {}", userId, ticketUrl);
          return ticketUrl;
        } else {
          throw new Auth0Exception("Failed to create password change ticket: no ticket URL in response");
        }
      } else {
        throw new Auth0Exception("Failed to create password change ticket: " + response.getStatusCode());
      }

    } catch (Exception e) {
      log.error("Failed to create password change ticket for user: {} with email: {}", userId, email, e);
      throw new Auth0Exception("Failed to create password change ticket: " + e.getMessage(), e);
    }
  }

  /**
   * Creates a password change ticket by email (finds user by email first).
   *
   * @param email the user's email address
   * @return the ticket URL for password change
   * @throws Auth0Exception if the ticket creation fails
   */
  public String createPasswordChangeTicketByEmail(String email) throws Auth0Exception {
    try {
      log.info("Creating password change ticket by email: {}", email);

      // Find user by email
      UserFilter filter = new UserFilter().withQuery("email:" + email);
      log.info("Searching for user with filter: {}", filter.getAsMap());
      
      UsersPage users = managementAPI.users().list(filter).execute().getBody();
      log.info("Found {} users for email: {}", users != null ? users.getItems().size() : 0, email);
      
      if (users == null || users.getItems().isEmpty()) {
        log.error("No users found for email: {}", email);
        throw new Auth0Exception("User not found with email: " + email);
      }

      String userId = users.getItems().get(0).getId();
      log.info("Found user with ID: {} for email: {}", userId, email);
      
      return createPasswordChangeTicket(userId, email);

    } catch (Auth0Exception e) {
      log.error("Auth0 exception creating password change ticket by email: {}", email, e);
      throw new Auth0Exception("Failed to create password change ticket: " + e.getMessage(), e);
    } catch (Exception e) {
      log.error("Unexpected error creating password change ticket by email: {}", email, e);
      throw new Auth0Exception("Unexpected error creating password change ticket: " + e.getMessage(), e);
    }
  }
}
