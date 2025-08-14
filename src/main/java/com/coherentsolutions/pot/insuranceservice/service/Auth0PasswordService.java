package com.coherentsolutions.pot.insuranceservice.service;

import com.coherentsolutions.pot.insuranceservice.config.Auth0Properties;
import com.coherentsolutions.pot.insuranceservice.exception.Auth0Exception;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

/**
 * Service for handling Auth0 password change operations.
 *
 * <p>This service provides functionality to send password change emails
 * using Auth0's /dbconnections/change_password endpoint.
 */
@Service
public class Auth0PasswordService {

  private final Auth0Properties auth0Properties;
  private final RestClient restClient;

  public Auth0PasswordService(Auth0Properties auth0Properties, RestClient restClient) {
    this.auth0Properties = auth0Properties;
    this.restClient = restClient;
  }

  /**
   * Sends a password change email to the specified user.
   *
   * @param userEmail the email address of the user
   * @return the response message from Auth0
   * @throws Auth0Exception if the request fails
   */
  public String sendPasswordChangeEmail(String userEmail) {
    if (!auth0Properties.enabled()) {
      throw new Auth0Exception("Auth0 integration is disabled");
    }

    String url = "https://" + auth0Properties.domain() + "/dbconnections/change_password";

    // Request body
    Map<String, String> body = new HashMap<>();
    body.put("client_id", auth0Properties.clientId());
    body.put("email", userEmail);
    body.put("connection", auth0Properties.connection());

    try {
      // Make POST request using RestClient
      ResponseEntity<String> response = restClient.post()
          .uri(url)
          .contentType(MediaType.APPLICATION_JSON)
          .body(body)
          .retrieve()
          .toEntity(String.class);

      if (response.getStatusCode() == HttpStatus.OK) {
        return response.getBody(); // Auth0 will return a message like "We've just sent you an email..."
      } else {
        throw new Auth0Exception("Failed to send change password email: " + response.getStatusCode());
      }
    } catch (Exception e) {
      throw new Auth0Exception("Error sending password change email: " + e.getMessage(), e);
    }
  }
}
