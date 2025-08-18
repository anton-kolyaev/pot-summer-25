package com.coherentsolutions.pot.insuranceservice.service;

import com.auth0.client.mgmt.ManagementAPI;
import com.auth0.client.mgmt.filter.UserFilter;
import com.auth0.json.mgmt.users.User;
import com.auth0.json.mgmt.users.UsersPage;
import com.coherentsolutions.pot.insuranceservice.dto.user.UserDto;
import com.coherentsolutions.pot.insuranceservice.exception.Auth0Exception;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * Service for updating Auth0 user metadata when local users are updated.
 *
 * <p>This service provides methods to synchronize user metadata between the local database
 * and Auth0 when user information is updated. It handles both user_metadata (data users can modify)
 * and app_metadata (data controlled by administrators).
 */
@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "auth0.enabled", havingValue = "true", matchIfMissing = false)
public class Auth0UserMetadataService {

  private final ManagementAPI managementAPI;

  /**
   * Updates Auth0 user metadata for the given user.
   *
   * <p>This method updates both user_metadata (data users can modify) and app_metadata 
   * (data controlled by administrators) in Auth0 based on the provided UserDto.
   *
   * @param auth0UserId the Auth0 user ID
   * @param userDto the updated user data
   * @throws Auth0Exception if the update fails
   */
  public void updateUserMetadata(String auth0UserId, UserDto userDto) throws Auth0Exception {
    log.info("Updating Auth0 user metadata for user: {}", userDto.getEmail());

    try {
      // Find the Auth0 user by ID or email
      User auth0User = findAuth0UserByIdOrEmail(auth0UserId, userDto.getEmail());
      if (auth0User == null) {
        log.error("Cannot update Auth0 metadata: No Auth0 user found for user: {}", userDto.getEmail());
        return;
      }

      // Build the user metadata (data that users can modify)
      Map<String, Object> userMetadata = buildUserMetadata(userDto);
      
      // Build the app metadata (data controlled by admins)
      Map<String, Object> appMetadata = buildAppMetadata(userDto);

      // Update the user's metadata
      auth0User.setUserMetadata(userMetadata);
      auth0User.setAppMetadata(appMetadata);

      // Update the user in Auth0
      User updatedUser = managementAPI.users().update(auth0User.getId(), auth0User).execute().getBody();
      
      log.info("Successfully updated Auth0 user metadata for user: {} (Auth0 ID: {})", 
               userDto.getEmail(), auth0User.getId());

    } catch (Exception e) {
      log.error("Failed to update Auth0 user metadata for user: {} (Auth0 ID: {})", 
                userDto.getEmail(), auth0UserId, e);
      throw new Auth0Exception(
          "Failed to update Auth0 user metadata: " + e.getMessage(), 
          e, 
          "AUTH0_METADATA_UPDATE_FAILED", 
          400
      );
    }
  }

  /**
   * Finds Auth0 user by ID or email as a backup plan.
   *
   * @param auth0UserId the Auth0 user ID (can be null or empty)
   * @param email the email to search for as backup
   * @return the Auth0 User object if found, null otherwise
   * @throws Auth0Exception if the search fails
   */
  private User findAuth0UserByIdOrEmail(String auth0UserId, String email) throws Auth0Exception {
    // First try to find by Auth0 user ID if provided
    if (auth0UserId != null && !auth0UserId.trim().isEmpty()) {
      try {
        log.debug("Attempting to find Auth0 user by ID: {}", auth0UserId);
        User user = managementAPI.users().get(auth0UserId, null).execute().getBody();
        if (user != null) {
          log.debug("Found Auth0 user by ID: {}", auth0UserId);
          return user;
        }
      } catch (Exception e) {
        log.warn("Failed to find Auth0 user by ID: {}. Will try email as backup.", auth0UserId, e);
      }
    }

    // If not found by ID or ID is not provided, try to find by email
    if (email != null && !email.trim().isEmpty()) {
      log.warn("Auth0 user ID is null, empty, or invalid for user: {}. Attempting to find user by email as backup.", email);
      
      try {
        log.debug("Searching for Auth0 user by email: {}", email);
        
        // Create a filter to search by email
        UserFilter filter = new UserFilter().withQuery("email:" + email);
        
        // Search for users with the given email
        UsersPage usersPage = managementAPI.users().list(filter).execute().getBody();
        List<User> users = usersPage.getItems();
        
        if (users.isEmpty()) {
          log.warn("No Auth0 user found with email: {}", email);
          return null;
        }
        
        if (users.size() > 1) {
          log.warn("Multiple Auth0 users found with email: {}. Using the first one.", email);
        }
        
        User foundUser = users.get(0);
        log.info("Found Auth0 user by email for user: {} (Auth0 ID: {})", email, foundUser.getId());
        
        return foundUser;
        
      } catch (Exception e) {
        log.error("Failed to search for Auth0 user by email: {}", email, e);
        throw new Auth0Exception(
            "Failed to search for Auth0 user by email: " + e.getMessage(), 
            e, 
            "AUTH0_USER_SEARCH_FAILED", 
            400
        );
      }
    }

    log.error("Cannot find Auth0 user: both auth0UserId and email are null or empty");
    return null;
  }

  /**
   * Builds user metadata for Auth0 from UserDto.
   * User metadata contains data that users can modify themselves.
   *
   * @param userDto the user DTO
   * @return the user metadata map
   */
  private Map<String, Object> buildUserMetadata(UserDto userDto) {
    Map<String, Object> metadata = new HashMap<>();
    metadata.put("firstName", userDto.getFirstName());
    metadata.put("lastName", userDto.getLastName());
    metadata.put("username", userDto.getUsername());
    metadata.put("dateOfBirth", userDto.getDateOfBirth() != null ? userDto.getDateOfBirth().toString() : null);
    metadata.put("ssn", userDto.getSsn());
    
    return metadata;
  }

  /**
   * Builds app metadata for Auth0 from UserDto.
   * App metadata contains data controlled by administrators.
   *
   * @param userDto the user DTO
   * @return the app metadata map
   */
  private Map<String, Object> buildAppMetadata(UserDto userDto) {
    Map<String, Object> metadata = new HashMap<>();
    metadata.put("companyId", userDto.getCompanyId() != null ? userDto.getCompanyId().toString() : null);
    
    if (userDto.getFunctions() != null) {
      metadata.put("functions", userDto.getFunctions());
    }
    
    // Determine system roles based on user functions or other criteria
    List<String> roles = determineUserRoles(userDto);
    if (!roles.isEmpty()) {
      metadata.put("roles", roles);
    }
    
    return metadata;
  }

  /**
   * Determines system roles for a user based on their functions or other criteria.
   *
   * @param userDto the user DTO
   * @return list of system roles for the user
   */
  private List<String> determineUserRoles(UserDto userDto) {
    List<String> roles = new ArrayList<>();
    
    // Add APPLICATION_ADMIN role if user has multiple management functions
    // This is a simple heuristic - in a real system, you might have more sophisticated logic
    if (userDto.getFunctions() != null && userDto.getFunctions().size() >= 3) {
      // If user has 3 or more management functions, they get application admin role
      long managementFunctions = userDto.getFunctions().stream()
          .filter(function -> function.name().contains("MANAGER"))
          .count();
      
      if (managementFunctions >= 3) {
        roles.add("APPLICATION_ADMIN");
      }
    }
    
    return roles;
  }
}
