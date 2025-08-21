package com.coherentsolutions.pot.insuranceservice.service;

import com.coherentsolutions.pot.insuranceservice.repository.UserRepository;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service for resolving local user UUID from Auth0 user ID for auditing purposes.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserAuditorService {

  private final UserRepository userRepository;

  /**
   * Finds local user UUID by Auth0 user ID.
   *
   * @param auth0UserId the Auth0 user ID
   * @return Optional containing the local user UUID if found
   */
  public Optional<UUID> findLocalUserIdByAuth0Id(String auth0UserId) {
    try {
      return userRepository.findByAuth0UserId(auth0UserId)
          .map(user -> {
            log.debug("Found local user UUID {} for Auth0 ID {}", user.getId(), auth0UserId);
            return user.getId();
          });
    } catch (Exception e) {
      log.error("Error finding local user UUID for Auth0 ID: {}", auth0UserId, e);
      return Optional.empty();
    }
  }
}
