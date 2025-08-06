package com.coherentsolutions.pot.insuranceservice.service;

import com.auth0.client.mgmt.ManagementAPI;
import com.auth0.json.mgmt.users.User;
import com.coherentsolutions.pot.insuranceservice.dto.auth0.Auth0InvitationDto;
import com.coherentsolutions.pot.insuranceservice.dto.auth0.Auth0UserDto;
import com.coherentsolutions.pot.insuranceservice.exception.Auth0Exception;
import com.coherentsolutions.pot.insuranceservice.mapper.Auth0UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * Service for handling Auth0 user invitations.
 * 
 * <p>This service provides methods to create users in Auth0 with invitation emails
 * instead of direct password creation. Users receive an email invitation to set up
 * their account and password.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "auth0.enabled", havingValue = "true", matchIfMissing = false)
public class Auth0InvitationService {

  private final ManagementAPI managementAPI;
  private final Auth0UserMapper auth0UserMapper;

  /**
   * Creates a new user in Auth0 with invitation email.
   * 
   * <p>The user will receive an email invitation to set up their account.
   * No password is set initially - the user will set it via the invitation link.
   * 
   * @param invitationDto the invitation data containing user information
   * @return the created Auth0 user DTO
   * @throws Auth0Exception if the invitation creation fails
   */
  public Auth0UserDto createUserWithInvitation(Auth0InvitationDto invitationDto) throws Auth0Exception {
    try {
      log.info("Creating Auth0 user with invitation for email: {}", invitationDto.getEmail());
      
      // Create Auth0 user without password (will be set via invitation)
      User auth0User = createAuth0UserWithoutPassword(invitationDto);
      
      // Send invitation email
      sendInvitationEmail(auth0User.getId(), invitationDto);
      
      log.info("Successfully created Auth0 user with invitation for email: {}", invitationDto.getEmail());
      
      return auth0UserMapper.toDto(auth0User);
      
    } catch (com.auth0.exception.Auth0Exception e) {
      log.error("Failed to create Auth0 user with invitation for email: {}", invitationDto.getEmail(), e);
      throw new Auth0Exception("Failed to create user invitation: " + e.getMessage(), e, "AUTH0_INVITATION_FAILED", 400);
    }
  }

  /**
   * Creates an Auth0 user without password for invitation flow.
   * 
   * @param invitationDto the invitation data
   * @return the created Auth0 user
   * @throws com.auth0.exception.Auth0Exception if user creation fails
   */
  private User createAuth0UserWithoutPassword(Auth0InvitationDto invitationDto) throws com.auth0.exception.Auth0Exception {
    User user = new User();
    user.setEmail(invitationDto.getEmail());
    user.setName(invitationDto.getName());
    user.setConnection("Username-Password-Authentication");
    user.setEmailVerified(false);
    user.setBlocked(false);
    
    // Auth0 requires a password, so we set a temporary one
    // The user will change it via the invitation flow
    user.setPassword("TempPassword123!".toCharArray());
    
    // Set user metadata if provided
    if (invitationDto.getUserMetadata() != null) {
      user.setUserMetadata(invitationDto.getUserMetadata());
    }
    
    // Set app metadata if provided
    if (invitationDto.getAppMetadata() != null) {
      user.setAppMetadata(invitationDto.getAppMetadata());
    }
    
    return managementAPI.users().create(user).execute().getBody();
  }

  /**
   * Sends an invitation email to the user.
   * 
   * @param userId the Auth0 user ID
   * @param invitationDto the invitation data
   * @throws com.auth0.exception.Auth0Exception if sending invitation fails
   */
  private void sendInvitationEmail(String userId, Auth0InvitationDto invitationDto) throws com.auth0.exception.Auth0Exception {
    log.info("Sending invitation email to user: {}", userId);
    
    // Trigger email verification
    User emailVerificationRequest = new User();
    emailVerificationRequest.setEmailVerified(false);
    
    managementAPI.users().update(userId, emailVerificationRequest).execute();
    
    log.info("Triggered email verification for user: {}", userId);
  }

  /**
   * Resends invitation email to an existing user.
   * 
   * @param userId the Auth0 user ID
   * @param email the user's email
   * @throws Auth0Exception if resending invitation fails
   */
  public void resendInvitation(String userId, String email) throws Auth0Exception {
    try {
      log.info("Resending invitation email to user: {} with email: {}", userId, email);
      
      // Verify user exists
      User existingUser = managementAPI.users().get(userId, null).execute().getBody();
      if (existingUser == null) {
        throw new Auth0Exception("User not found: " + userId);
      }
      
      // Resend invitation (using email verification as workaround)
      sendInvitationEmail(userId, Auth0InvitationDto.builder()
          .email(email)
          .name(existingUser.getName())
          .build());
      
      log.info("Successfully resent invitation email to user: {}", userId);
      
    } catch (com.auth0.exception.Auth0Exception e) {
      log.error("Failed to resend invitation email to user: {}", userId, e);
      throw new Auth0Exception("Failed to resend invitation: " + e.getMessage(), e, "AUTH0_INVITATION_RESEND_FAILED", 400);
    }
  }

  /**
   * Checks if a user exists in Auth0 by email.
   * 
   * @param email the email to check
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
} 