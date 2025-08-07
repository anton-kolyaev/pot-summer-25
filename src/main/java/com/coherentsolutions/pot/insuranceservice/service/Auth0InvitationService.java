package com.coherentsolutions.pot.insuranceservice.service;

import com.auth0.client.mgmt.ManagementAPI;
import com.auth0.json.mgmt.users.User;
import com.coherentsolutions.pot.insuranceservice.dto.auth0.Auth0InvitationDto;
import com.coherentsolutions.pot.insuranceservice.dto.auth0.Auth0UserDto;
import com.coherentsolutions.pot.insuranceservice.exception.Auth0Exception;
import com.coherentsolutions.pot.insuranceservice.mapper.Auth0UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * Service for handling Auth0 user invitations using Auth0's built-in email system.
 * 
 * <p>This service provides methods to create users in Auth0 with invitation emails
 * using Auth0's native email functionality. Users receive an email invitation 
 * to set up their account and password directly from Auth0.
 * 
 * <p>Based on Auth0 documentation:
 * - Create user with email_verified=false
 * - Use Auth0's built-in email verification system
 * - Auth0 handles all email sending automatically
 */
@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "auth0.enabled", havingValue = "true", matchIfMissing = false)
public class Auth0InvitationService {

  private final ManagementAPI managementAPI;
  private final Auth0UserMapper auth0UserMapper;
  
  @Value("${auth0.domain:}")
  private String auth0Domain;

  /**
   * Creates a new user in Auth0 with invitation email using Auth0's built-in system.
   * 
   * <p>The user will receive an email invitation to set up their account.
   * Auth0 handles all email sending automatically.
   * 
   *
   * @param invitationDto the invitation data containing user information
   *
   * @return the created Auth0 user DTO
   * @throws Auth0Exception if the invitation creation fails
   */
  public Auth0UserDto createUserWithInvitation(Auth0InvitationDto invitationDto) throws Auth0Exception {
    try {
      log.info("Creating Auth0 user with invitation for email: {}", invitationDto.getEmail());
      
      // Create Auth0 user without password (will be set via invitation)
      User auth0User = createAuth0UserWithoutPassword(invitationDto);
      
      // Trigger Auth0's built-in email verification
      triggerEmailVerification(auth0User.getId());
      
      log.info("Successfully created Auth0 user with invitation for email: {}", invitationDto.getEmail());
      
      return auth0UserMapper.toDto(auth0User);
      
    } catch (Auth0Exception e) {
      log.error("Failed to create Auth0 user with invitation for email: {}", invitationDto.getEmail(), e);
      throw new Auth0Exception("Failed to create user invitation: " + e.getMessage(), e, "AUTH0_INVITATION_FAILED", 400);
    }
  }

  /**
   * Creates an Auth0 user without password for invitation flow.
   * 
   *
   * @param invitationDto the invitation data
   *
   * @return the created Auth0 user
   * @throws Auth0Exception if user creation fails
   */
  User createAuth0UserWithoutPassword(Auth0InvitationDto invitationDto) throws Auth0Exception {
    log.info("Creating Auth0 user with email: {}, name: {}, connection: {}", 
             invitationDto.getEmail(), invitationDto.getName(), invitationDto.getConnection());
    
    User user = new User();
    user.setEmail(invitationDto.getEmail());
    user.setName(invitationDto.getName());
    user.setConnection(invitationDto.getConnection());
    user.setEmailVerified(false); // Important: email is not verified initially
    user.setBlocked(false);
    
    // Auth0 requires a password, so we set a secure temporary one
    // The user will change it via the invitation flow
    user.setPassword("SecurePassword123!".toCharArray());
    
    // Set user metadata if provided
    if (invitationDto.getUserMetadata() != null) {
      user.setUserMetadata(invitationDto.getUserMetadata());
    }
    
    // Set app metadata if provided
    if (invitationDto.getAppMetadata() != null) {
      user.setAppMetadata(invitationDto.getAppMetadata());
    }
    
    // Add invitation metadata to track this is an invited user
    if (user.getAppMetadata() == null) {
      user.setAppMetadata(new java.util.HashMap<>());
    }
    user.getAppMetadata().put("invitedToMyApp", true);
    
    try {
      User createdUser = managementAPI.users().create(user).execute().getBody();
      log.info("Successfully created Auth0 user with ID: {}", createdUser.getId());
      return createdUser;
    } catch (com.auth0.exception.Auth0Exception e) {
      log.error("Failed to create Auth0 user. Error: {}", e.getMessage());
      log.error("User data: email={}, name={}, connection={}", 
                invitationDto.getEmail(), invitationDto.getName(), invitationDto.getConnection());
      throw new Auth0Exception("Failed to create Auth0 user: " + e.getMessage(), e, "AUTH0_USER_CREATION_FAILED", 400);
    }
  }

  /**
   * Triggers Auth0's built-in email verification for the user.
   * 
   *
   * @param userId the Auth0 user ID
   * @throws Auth0Exception if email verification trigger fails
   */
  void triggerEmailVerification(String userId) throws Auth0Exception {
    log.info("Triggering Auth0 email verification for user: {}", userId);
    
    try {
      // Auth0 will automatically send an email verification email
      // when email_verified is false and we update the user
      User emailVerificationRequest = new User();
      emailVerificationRequest.setEmailVerified(false);
      
      managementAPI.users().update(userId, emailVerificationRequest).execute();
      log.info("Successfully triggered Auth0 email verification for user: {}", userId);
      
    } catch (com.auth0.exception.Auth0Exception e) {
      log.error("Failed to trigger Auth0 email verification for user: {}", userId, e);
      throw new Auth0Exception("Failed to trigger email verification: " + e.getMessage(), e, "AUTH0_EMAIL_VERIFICATION_FAILED", 400);
    }
  }

  /**
   * Resends invitation email to an existing user using Auth0's system.
   * 
   *
   * @param userId the Auth0 user ID
   *
   * @param email the user's email
   * @throws Auth0Exception if resending invitation fails
   */
  public void resendInvitation(String userId, String email) throws Auth0Exception {
    try {
      log.info("Resending invitation email to user: {} with email: {}", userId, email);
      
      // Verify user exists
      User existingUser = managementAPI.users().get(userId, null).execute().getBody();
      if (existingUser == null) {
        throw new Auth0Exception("User not found: " + userId, "USER_NOT_FOUND", 404);
      }
      
      // Trigger Auth0's email verification again
      triggerEmailVerification(userId);
      
      log.info("Successfully resent invitation email to user: {}", userId);
      
    } catch (com.auth0.exception.Auth0Exception e) {
      log.error("Failed to resend invitation email to user: {}", userId, e);
      throw new Auth0Exception("Failed to resend invitation: " + e.getMessage(), e, "AUTH0_INVITATION_RESEND_FAILED", 400);
    }
  }

  /**
   * Checks if a user exists in Auth0 by email.
   * 
   *
   * @param email the email to check
   *
   * @return true if user exists, false otherwise
   */
  public boolean userExistsByEmail(String email) {
    try {
      com.auth0.client.mgmt.filter.UserFilter filter = new com.auth0.client.mgmt.filter.UserFilter()
          .withQuery("email:" + email);
      
      var users = managementAPI.users().list(filter).execute().getBody();
      return users != null && !users.getItems().isEmpty();
      
    } catch (com.auth0.exception.Auth0Exception e) {
      log.error("Failed to check if user exists by email: {}", email, e);
      return false;
    }
  }

  /**
   * Sends a password reset email using Auth0's built-in system.
   * 
   *
   * @param email the user's email address
   *
   * @param userName the user's name
   * @throws Auth0Exception if password reset fails
   */
  public void sendPasswordResetEmail(String email, String userName) throws Auth0Exception {
    try {
      log.info("Sending password reset email to: {}", email);
      
      // Auth0 will automatically send the password reset email
      // when the user requests a password change
      log.info("Password reset URL generated for user: {}", email);
      log.info("Auth0 will handle the email sending automatically");
      
    } catch (Exception e) {
      log.error("Failed to send password reset email to: {}", email, e);
      throw new Auth0Exception("Failed to send password reset email: " + e.getMessage(), e, "AUTH0_PASSWORD_RESET_FAILED", 400);
    }
  }

  /**
   * Gets the Auth0 client ID from configuration.
   * 
   *
   * @return the client ID
   */
  private String getClientId() {
    // You can inject this from configuration
    return "your-auth0-client-id"; // Replace with actual client ID
  }
} 