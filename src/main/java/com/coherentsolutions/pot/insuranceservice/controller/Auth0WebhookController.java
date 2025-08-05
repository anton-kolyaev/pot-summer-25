package com.coherentsolutions.pot.insuranceservice.controller;

import com.coherentsolutions.pot.insuranceservice.service.UserInvitationService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for handling Auth0 webhooks.
 * 
 * This controller receives webhooks from Auth0 when users complete
 * invitation processes or other account-related events.
 */
@RestController
@RequestMapping("/v1/webhooks/auth0")
@RequiredArgsConstructor
@Slf4j
public class Auth0WebhookController {

  private final UserInvitationService userInvitationService;
  private final ObjectMapper objectMapper;

  /**
   * Handles Auth0 webhook for user invitation completion.
   * 
   * This endpoint is called by Auth0 when a user completes the invitation process
   * and sets their password. The webhook should be configured in Auth0 dashboard
   * to trigger on user creation and password change events.
   */
  @PostMapping("/user-invitation-completed")
  public ResponseEntity<String> handleUserInvitationCompleted(@RequestBody String payload) {
    try {
      log.info("Received Auth0 webhook: {}", payload);
      
      JsonNode jsonNode = objectMapper.readTree(payload);
      String event = jsonNode.path("event").asText();
      
      if ("post-user-registration".equals(event) || "post-user-password-change".equals(event)) {
        JsonNode userNode = jsonNode.path("user");
        String email = userNode.path("email").asText();
        
        // Extract local user ID from user metadata
        JsonNode userMetadata = userNode.path("user_metadata");
        String localUserIdStr = userMetadata.path("localUserId").asText();
        
        if (localUserIdStr != null && !localUserIdStr.isEmpty()) {
          UUID localUserId = UUID.fromString(localUserIdStr);
          
          // Activate the user in our local database
          userInvitationService.activateUser(localUserId);
          log.info("User activated via webhook: {}", localUserId);
          
          return ResponseEntity.ok("User activated successfully");
        } else {
          log.warn("No localUserId found in user metadata for email: {}", email);
          return ResponseEntity.badRequest().body("No localUserId found in user metadata");
        }
      }
      
      return ResponseEntity.ok("Event processed");
      
    } catch (Exception e) {
      log.error("Error processing Auth0 webhook", e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("Error processing webhook: " + e.getMessage());
    }
  }

  /**
   * Handles Auth0 webhook for user email verification.
   */
  @PostMapping("/user-email-verified")
  public ResponseEntity<String> handleUserEmailVerified(@RequestBody String payload) {
    try {
      log.info("Received Auth0 email verification webhook: {}", payload);
      
      JsonNode jsonNode = objectMapper.readTree(payload);
      String event = jsonNode.path("event").asText();
      
      if ("post-user-registration".equals(event)) {
        JsonNode userNode = jsonNode.path("user");
        String email = userNode.path("email").asText();
        boolean emailVerified = userNode.path("email_verified").asBoolean();
        
        if (emailVerified) {
          log.info("User email verified: {}", email);
          // You might want to update user status or send additional notifications
        }
      }
      
      return ResponseEntity.ok("Email verification processed");
      
    } catch (Exception e) {
      log.error("Error processing Auth0 email verification webhook", e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("Error processing webhook: " + e.getMessage());
    }
  }
} 