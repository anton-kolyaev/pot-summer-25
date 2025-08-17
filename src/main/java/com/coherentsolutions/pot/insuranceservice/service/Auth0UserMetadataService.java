package com.coherentsolutions.pot.insuranceservice.service;

import com.auth0.client.mgmt.ManagementAPI;
import com.auth0.client.mgmt.filter.UserFilter;
import com.auth0.json.mgmt.users.User;
import com.auth0.json.mgmt.users.UsersPage;
import com.coherentsolutions.pot.insuranceservice.dto.user.UserDto;
import com.coherentsolutions.pot.insuranceservice.exception.Auth0Exception;
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
    if (auth0UserId == null || auth0UserId.trim().isEmpty()) {
      log.warn("Auth0 user ID is null or empty for user: {}. Attempting to find user by email as backup.", userDto.getEmail());
      
      // Try to find the user by email as a backup plan
      String foundAuth0UserId = findAuth0UserIdByEmail(userDto.getEmail());
      if (foundAuth0UserId != null) {
        log.info("Found Auth0 user by email for user: {} (Auth0 ID: {})", userDto.getEmail(), foundAuth0UserId);
        auth0UserId = foundAuth0UserId;
      } else {
        log.error("Cannot update Auth0 metadata: Auth0 user ID is null/empty and no user found by email for user: {}", userDto.getEmail());
        return;
      }
    }

    log.info("Updating Auth0 user metadata for user: {} (Auth0 ID: {})", userDto.getEmail(), auth0UserId);

    try {
      // Build the user metadata (data that users can modify)
      Map<String, Object> userMetadata = buildUserMetadata(userDto);
      
      // Build the app metadata (data controlled by admins)
      Map<String, Object> appMetadata = buildAppMetadata(userDto);

      // Create a User object with both types of metadata
      User auth0User = new User();
      auth0User.setUserMetadata(userMetadata);
      auth0User.setAppMetadata(appMetadata);

      // Update the user in Auth0
      User updatedUser = managementAPI.users().update(auth0UserId, auth0User).execute().getBody();
      
      log.info("Successfully updated Auth0 user metadata for user: {} (Auth0 ID: {})", 
               userDto.getEmail(), auth0UserId);

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
   * Finds Auth0 user ID by email as a backup plan when the Auth0 user ID is not available.
   *
   * @param email the email to search for
   * @return the Auth0 user ID if found, null otherwise
   * @throws Auth0Exception if the search fails
   */
  public String findAuth0UserIdByEmail(String email) throws Auth0Exception {
    if (email == null || email.trim().isEmpty()) {
      log.warn("Cannot search for Auth0 user: email is null or empty");
      return null;
    }

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
      
      String auth0UserId = users.get(0).getId();
      log.debug("Found Auth0 user ID: {} for email: {}", auth0UserId, email);
      
      return auth0UserId;
      
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
    
    return metadata;
  }
}
