package com.coherentsolutions.pot.insuranceservice.controller;

import com.auth0.exception.Auth0Exception;
import com.coherentsolutions.pot.insuranceservice.dto.user.UserDto;
import com.coherentsolutions.pot.insuranceservice.dto.user.UserFilter;
import com.coherentsolutions.pot.insuranceservice.service.UserInvitationService;
import com.coherentsolutions.pot.insuranceservice.service.UserManagementService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 * REST controller for managing users by admin.
 * Provides endpoints to create users, update users, and query users with
 * filters.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/users")
public class AdminUserManagementController {

  private final UserManagementService userManagementService;
  private final UserInvitationService userInvitationService;

  /**
   * Creates a new user with invitation via email.
   * 
   * This endpoint:
   * 1. Saves the user to the local database with INACTIVE status
   * 2. Creates the user in Auth0 with invitation enabled (no password)
   * 3. Auth0 sends an invitation email to the user
   * 4. User activates account and creates password via email link
   */
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public UserDto createUser(@Valid @RequestBody UserDto userDto) {
    try {
      return userInvitationService.inviteUser(userDto);
    } catch (Auth0Exception e) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, 
          "Failed to create user with invitation: " + e.getMessage());
    }
  }

  /**
   * Retrieves a paginated list of users filtered by given criteria.
   */
  @GetMapping
  public Page<UserDto> getUsersWithFilters(UserFilter filter, Pageable pageable) {
    return userManagementService.getUsersWithFilters(filter, pageable);
  }

  /**
   * Updates an existing user.
   */
  @PutMapping("/{id}")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public UserDto updateUser(@PathVariable("id") UUID id, @Valid @RequestBody UserDto request) {
    return userManagementService.updateUser(id, request);
  }

  /**
   * Deactivates the user with the given ID.
   */
  @DeleteMapping("/{id}")
  public UserDto deactivateUser(@PathVariable("id") UUID id) {
    return userManagementService.deactivateUser(id);
  }

  /**
   * Reactivates the user with the given ID.
   */
  @PutMapping("/{id}/reactivation")
  public UserDto reactivateUser(@PathVariable("id") UUID id) {
    return userManagementService.reactivateUser(id);
  }

  /**
   * Activates a user by changing their status from INACTIVE to ACTIVE.
   * This endpoint can be used to manually activate users after they complete
   * the invitation process or for administrative purposes.
   */
  @PutMapping("/{id}/activate")
  public UserDto activateUser(@PathVariable("id") UUID id) {
    return userInvitationService.activateUser(id);
  }

  /**
   * Retrieves user details by ID. If the user does not exist, throws {@link
   * ResponseStatusException} with 404 NOT FOUND.
   */
  @GetMapping("/{id}")
  public UserDto viewUsersDetails(@PathVariable("id") UUID id) {
    return userManagementService.getUsersDetails(id);
  }
}
