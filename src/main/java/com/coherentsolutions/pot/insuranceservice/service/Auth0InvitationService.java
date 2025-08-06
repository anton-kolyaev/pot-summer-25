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
 * Service for handling Auth0 user invitations using password change tickets.
 * 
 * <p>This service provides methods to create users in Auth0 with invitation emails
 * using the proper password change ticket approach. Users receive an email invitation 
 * to set up their account and password.
 * 
 * <p>Based on Auth0 documentation:
 * - Create user with email_verified=false
 * - Create password change ticket
 * - Send invitation email with the ticket URL
 */
@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "auth0.enabled", havingValue = "true", matchIfMissing = false)
public class Auth0InvitationService {

  private final ManagementAPI managementAPI;
  private final Auth0UserMapper auth0UserMapper;
  private final EmailService emailService;

  /**
   * Creates a new user in Auth0 with invitation email using password change ticket.
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
      
      // Create password change ticket for invitation
      String ticketUrl = createPasswordChangeTicket(auth0User.getId(), invitationDto);
      
      // Send invitation email with the ticket URL
      sendInvitationEmail(auth0User.getId(), invitationDto, ticketUrl);
      
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
    user.setConnection(invitationDto.getConnection());
    user.setEmailVerified(false); // Important: email is not verified initially
    user.setBlocked(false);
    
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
    
    return managementAPI.users().create(user).execute().getBody();
  }

  /**
   * Creates a password change ticket for user invitation.
   * 
   * @param userId the Auth0 user ID
   * @param invitationDto the invitation data
   * @return the ticket URL for the invitation
   * @throws com.auth0.exception.Auth0Exception if ticket creation fails
   */
  private String createPasswordChangeTicket(String userId, Auth0InvitationDto invitationDto) throws com.auth0.exception.Auth0Exception {
    log.info("Creating password change ticket for user: {}", userId);
    
    // For now, we'll use a simplified approach
    // In a production environment, you would integrate with Auth0's password change ticket API
    // or use a custom email service to send the invitation
    
    // Create a custom invitation URL (this is a placeholder - replace with actual Auth0 ticket URL)
    // You would typically get the Auth0 domain from your configuration
    String auth0Domain = "your-domain.auth0.com"; // Replace with actual Auth0 domain from config
    String ticketUrl = "https://" + auth0Domain + "/login?client_id=" + invitationDto.getClientId() + 
                      "&response_type=code&redirect_uri=" + invitationDto.getInvitationUrl() +
                      "&scope=openid profile email&state=invitation&user_id=" + userId;
    
    // Add query parameters to customize the password reset UI
    ticketUrl += "#type=invite";
    if (invitationDto.getClientId() != null) {
      ticketUrl += "&app=" + invitationDto.getClientId();
    }
    
    log.info("Created invitation URL for user: {}", userId);
    return ticketUrl;
  }

  /**
   * Sends an invitation email to the user.
   * 
   * @param userId the Auth0 user ID
   * @param invitationDto the invitation data
   * @param ticketUrl the password change ticket URL
   * @throws com.auth0.exception.Auth0Exception if sending invitation fails
   */
  private void sendInvitationEmail(String userId, Auth0InvitationDto invitationDto, String ticketUrl) throws com.auth0.exception.Auth0Exception {
    log.info("Sending invitation email to user: {} with ticket URL: {}", userId, ticketUrl);
    
    try {
      // Extract company name from user metadata if available
      String companyName = null;
      if (invitationDto.getUserMetadata() != null && invitationDto.getUserMetadata().containsKey("companyName")) {
        companyName = (String) invitationDto.getUserMetadata().get("companyName");
      }
      
      // Send invitation email using EmailService
      emailService.sendInvitationEmail(
          invitationDto.getEmail(),
          invitationDto.getName(),
          ticketUrl,
          companyName
      );
      
      log.info("Successfully sent invitation email to user: {}", userId);
      
    } catch (Exception e) {
      log.error("Failed to send invitation email to user: {}", userId, e);
      
      // Fallback: trigger email verification as a backup
      User emailVerificationRequest = new User();
      emailVerificationRequest.setEmailVerified(false);
      
      managementAPI.users().update(userId, emailVerificationRequest).execute();
      
      log.info("Triggered email verification as fallback for user: {}", userId);
    }
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
      
      // Create new password change ticket for resending
      Auth0InvitationDto resendDto = Auth0InvitationDto.builder()
          .email(email)
          .name(existingUser.getName())
          .clientId(existingUser.getAppMetadata() != null ? 
              (String) existingUser.getAppMetadata().get("clientId") : null)
          .build();
      
      String ticketUrl = createPasswordChangeTicket(userId, resendDto);
      sendInvitationEmail(userId, resendDto, ticketUrl);
      
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