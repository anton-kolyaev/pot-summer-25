package com.coherentsolutions.pot.insuranceservice.service;

import com.auth0.client.mgmt.ManagementAPI;
import com.auth0.json.mgmt.users.User;
import com.coherentsolutions.pot.insuranceservice.dto.user.UserDto;
import com.coherentsolutions.pot.insuranceservice.exception.Auth0Exception;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * Service for updating Auth0 user metadata when local users are updated.
 *
 * <p>This service provides methods to synchronize user metadata between the local database
 * and Auth0 when user information is updated.
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
   * @param auth0UserId the Auth0 user ID
   * @param userDto the updated user data
   * @throws Auth0Exception if the update fails
   */
  public void updateUserMetadata(String auth0UserId, UserDto userDto) throws Auth0Exception {
    if (auth0UserId == null || auth0UserId.trim().isEmpty()) {
      log.warn("Cannot update Auth0 metadata: Auth0 user ID is null or empty for user: {}", userDto.getEmail());
      return;
    }

    log.info("Updating Auth0 user metadata for user: {} (Auth0 ID: {})", userDto.getEmail(), auth0UserId);

    try {
      // Build the user metadata
      Map<String, Object> userMetadata = buildUserMetadata(userDto);

      // Create a User object with only the metadata to update
      User auth0User = new User();
      auth0User.setUserMetadata(userMetadata);

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
   * Builds user metadata for Auth0 from UserDto.
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
    metadata.put("companyId", userDto.getCompanyId() != null ? userDto.getCompanyId().toString() : null);
    
    if (userDto.getFunctions() != null) {
      metadata.put("functions", userDto.getFunctions());
    }
    
    return metadata;
  }
}
