package com.coherentsolutions.pot.insuranceservice.service;

import com.auth0.exception.Auth0Exception;
import com.coherentsolutions.pot.insuranceservice.dto.auth0.Auth0UserDto;
import com.coherentsolutions.pot.insuranceservice.dto.user.UserDto;
import com.coherentsolutions.pot.insuranceservice.enums.UserStatus;
import com.coherentsolutions.pot.insuranceservice.mapper.UserMapper;
import com.coherentsolutions.pot.insuranceservice.model.User;
import com.coherentsolutions.pot.insuranceservice.model.UserFunctionAssignment;
import com.coherentsolutions.pot.insuranceservice.repository.UserRepository;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for handling user invitations via email.
 *
 * <p>This service coordinates the process of creating users in both the local database
 * and Auth0, with Auth0 handling the email invitation process.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserInvitationService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final Auth0UserManagementService auth0UserManagementService;

  /**
   * Creates a new user with invitation via email.
   *
   * <p>This method:
   * 1. Saves the user to the local database with PENDING status
   * 2. Creates the user in Auth0 with invitation enabled
   * 3. Auth0 sends an invitation email to the user
   *
   * @param userDto the user data to create
   * @return the created user DTO
   * @throws Auth0Exception if Auth0 user creation fails
   */
  @Transactional
  public UserDto inviteUser(UserDto userDto) throws Auth0Exception {
    log.info("Starting user invitation process for email: {}", userDto.getEmail());

    // Create user in local database with PENDING status
    User user = userMapper.toEntity(userDto);
    user.setStatus(UserStatus.PENDING);

    // Set up function assignments
    if (user.getFunctions() != null) {
      for (UserFunctionAssignment ufa : user.getFunctions()) {
        ufa.setUser(user);
      }
    }

    // Save to local database
    User savedUser = userRepository.save(user);
    log.info("User saved to local database with ID: {}", savedUser.getId());

    // Create Auth0 user with invitation
    Auth0UserDto auth0UserDto = new Auth0UserDto(
        userDto.getEmail(), 
        null, // No password for invitation
        userDto.getFirstName() + " " + userDto.getLastName()
    );
    
    // Set user metadata to link with local user ID
    auth0UserDto.setUserMetadata(Map.of("localUserId", savedUser.getId().toString()));
    
    try {
      auth0UserManagementService.createUserWithInvitation(auth0UserDto);
      log.info("Auth0 user created with invitation for email: {}", userDto.getEmail());
    } catch (Auth0Exception e) {
      log.error("Failed to create Auth0 user for email: {}", userDto.getEmail(), e);
      // Rollback local user creation if Auth0 fails
      userRepository.delete(savedUser);
      throw e;
    }

    return userMapper.toDto(savedUser);
  }

  /**
   * Activates a user after they complete the invitation process.
   *
   * @param userId the local user ID
   * @return the updated user DTO
   */
  @Transactional
  public UserDto activateUser(UUID userId) {
    User user = userRepository.findByIdOrThrow(userId);
    
    if (user.getStatus() != UserStatus.PENDING) {
      throw new IllegalStateException("User is not in PENDING status");
    }
    
    user.setStatus(UserStatus.ACTIVE);
    User updatedUser = userRepository.save(user);
    
    log.info("User activated: {}", userId);
    return userMapper.toDto(updatedUser);
  }

  /**
   * Checks if a user exists by email.
   *
   * @param email the email to check
   * @return true if user exists, false otherwise
   */
  public boolean userExistsByEmail(String email) {
    return userRepository.existsByEmail(email);
  }

  /**
   * Gets user by email.
   *
   * @param email the email to search for
   * @return the user DTO, or null if not found
   */
  public UserDto getUserByEmail(String email) {
    return userRepository.findByEmail(email)
        .map(userMapper::toDto)
        .orElse(null);
  }
} 