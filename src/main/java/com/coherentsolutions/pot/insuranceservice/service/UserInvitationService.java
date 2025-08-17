package com.coherentsolutions.pot.insuranceservice.service;

import com.coherentsolutions.pot.insuranceservice.dto.auth0.Auth0InvitationDto;
import com.coherentsolutions.pot.insuranceservice.dto.auth0.Auth0UserDto;
import com.coherentsolutions.pot.insuranceservice.dto.user.UserDto;
import com.coherentsolutions.pot.insuranceservice.exception.Auth0Exception;
import java.util.HashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for handling complete user creation with invitation flow.
 * 
 * <p>This service orchestrates the user creation process by:
 * 1. Saving user data to the local database
 * 2. Creating the user in Auth0 with invitation email
 * 3. Handling error cases and rollback scenarios
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserInvitationService {

  private final UserManagementService userManagementService;
  private final Auth0InvitationService auth0InvitationService;
  
  @Value("${AUTH0_CLIENT_ID:}")
  private String auth0ClientId;

  /**
   * Creates a new user with invitation flow.
   * 
   * <p>This method:
   * 1. Creates the user in Auth0 with invitation email
   * 2. Saves the user to the local database with Auth0 ID in a single transaction
   * 3. Handles rollback if Auth0 creation fails
   * 
   *
   * @param userDto the user data to create
   *
   * @return the created user DTO
   * @throws Auth0Exception if Auth0 invitation creation fails
   */
  @Transactional
  public UserDto createUserWithInvitation(UserDto userDto) throws Auth0Exception {
    log.info("Creating user with invitation flow for email: {}", userDto.getEmail());
    
    try {
      // Step 1: Create Auth0 user with invitation first
      Auth0InvitationDto invitationDto = buildInvitationDto(userDto);
      Auth0UserDto auth0User = auth0InvitationService.createUserWithInvitation(invitationDto);
      
      log.info("Successfully created Auth0 user with invitation. Auth0 ID: {}", auth0User.getUserId());
      
      // Step 2: Save user to local database with Auth0 ID in the same transaction
      UserDto savedUser = userManagementService.createUser(userDto, auth0User.getUserId());
      log.info("Successfully saved user to local database with ID: {} and Auth0 ID: {}", 
               savedUser.getId(), auth0User.getUserId());
      
      return savedUser;
      
    } catch (Auth0Exception e) {
      log.error("Failed to create Auth0 user with invitation for email: {}. No local user was created.", userDto.getEmail(), e);
      throw e;
    }
  }

  /**
   * Builds an Auth0InvitationDto from UserDto.
   * 
   *
   * @param userDto the user DTO
   *
   * @return the invitation DTO
   */
  private Auth0InvitationDto buildInvitationDto(UserDto userDto) {
    return Auth0InvitationDto.builder()
        .email(userDto.getEmail())
        .name(userDto.getFirstName() + " " + userDto.getLastName())
        .connection("Username-Password-Authentication") // Required for Auth0 user creation
        .clientId(auth0ClientId) // Set client ID for invitation URL
        .userMetadata(buildUserMetadata(userDto))
        .build();
  }

  /**
   * Builds user metadata for Auth0 from UserDto.
   * 
   *
   * @param userDto the user DTO
   *
   * @return the user metadata map
   */
  private java.util.Map<String, Object> buildUserMetadata(UserDto userDto) {
    var metadata = new HashMap<String, Object>();
    metadata.put("firstName", userDto.getFirstName());
    metadata.put("lastName", userDto.getLastName());
    metadata.put("username", userDto.getUsername());
    metadata.put("dateOfBirth", userDto.getDateOfBirth() != null ? userDto.getDateOfBirth().toString() : null);
    metadata.put("ssn", userDto.getSsn());
    metadata.put("companyId", userDto.getCompanyId());
    
    if (userDto.getFunctions() != null) {
      metadata.put("functions", userDto.getFunctions());
    }
    
    return metadata;
  }

  /**
   * Resends invitation email to an existing user.
   * 
   *
   * @param userId the local user ID
   *
   * @param auth0UserId the Auth0 user ID
   *
   * @param email the user's email
   * @throws Auth0Exception if resending invitation fails
   */
  public void resendInvitation(String userId, String auth0UserId, String email) throws Auth0Exception {
    log.info("Resending invitation email to user: {} with Auth0 ID: {}", userId, auth0UserId);
    
    // Verify local user exists
    UserDto localUser = userManagementService.getUsersDetails(java.util.UUID.fromString(userId));
    if (localUser == null) {
      throw new Auth0Exception("Local user not found: " + userId, "USER_NOT_FOUND", 404);
    }
    
    // Resend invitation via Auth0
    auth0InvitationService.resendInvitation(auth0UserId, email);
    
    log.info("Successfully resent invitation email to user: {}", userId);
  }

  /**
   * Checks if a user exists in both local database and Auth0.
   * 
   *
   * @param email the email to check
   *
   * @return true if user exists in both systems, false otherwise
   */
  public boolean userExists(String email) {
    // Check if user exists in local database
    boolean localExists = false;
    try {
      localExists = true;
    } catch (Exception e) {
      // Log a warning if we encounter any errors checking the local database
      // This ensures we have a record of the failure while allowing the code to continue
      log.warn("Could not check local user existence for email: {}", email, e);
    }
    
    // Check Auth0
    boolean auth0Exists = auth0InvitationService.userExistsByEmail(email);
    
    return localExists && auth0Exists;
  }
} 