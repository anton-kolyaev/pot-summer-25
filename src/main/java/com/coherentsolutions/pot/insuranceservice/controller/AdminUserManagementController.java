package com.coherentsolutions.pot.insuranceservice.controller;

import com.coherentsolutions.pot.insuranceservice.dto.user.UserDto;
import com.coherentsolutions.pot.insuranceservice.dto.user.UserFilter;
import com.coherentsolutions.pot.insuranceservice.exception.Auth0Exception;
import com.coherentsolutions.pot.insuranceservice.service.UserInvitationService;
import com.coherentsolutions.pot.insuranceservice.service.UserManagementService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 * REST controller for managing users by admin. Provides endpoints to create users, update users,
 * and query users with filters.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/companies/{companyId}/users")
public class AdminUserManagementController {

  private final UserManagementService userManagementService;
  private final UserInvitationService userInvitationService;

  /**
   * Creates a new user with invitation flow.
   * 
   * <p>This endpoint:
   * 1. Saves the user data to the local database
   * 2. Creates the user in Auth0 with invitation email
   * 3. The invited user will receive an email to set up their account
   */
  @PreAuthorize("@companyAdminSecurityService.canAccessCompanyResource(#companyId, 'ROLE_FUNC_COMPANY_USER_MANAGER')")
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public UserDto createUser(@PathVariable UUID companyId, @Valid @RequestBody UserDto userDto) {
    try {
      return userInvitationService.createUserWithInvitation(userDto);
    } catch (Auth0Exception e) {
      throw new RuntimeException("Failed to create user with invitation: " + e.getMessage(), e);
    }
  }

  /**
   * Retrieves a paginated list of users belonging to a specific company filtered by given
   * criteria.
   */
  @PreAuthorize("@companyAdminSecurityService.canAccessCompanyResource(#companyId, 'ROLE_FUNC_COMPANY_USER_MANAGER')")
  @GetMapping
  public Page<UserDto> getUsersOfCompany(@PathVariable UUID companyId, UserFilter filter,
      Pageable pageable) {
    filter.setCompanyId(companyId);
    return userManagementService.getUsersWithFilters(filter, pageable);
  }

  /**
   * Updates an existing user.
   */
  @PreAuthorize("@companyAdminSecurityService.canAccessCompanyResource(#companyId, 'ROLE_FUNC_COMPANY_USER_MANAGER')")
  @PutMapping("/{id}")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public UserDto updateUser(@PathVariable UUID companyId, @PathVariable("id") UUID id,
      @Valid @RequestBody UserDto request) {
    return userManagementService.updateUser(id, request);
  }

  /**
   * Deactivates the user with the given ID.
   */
  @PreAuthorize("@companyAdminSecurityService.canAccessCompanyResource(#companyId, 'ROLE_FUNC_COMPANY_USER_MANAGER')")
  @DeleteMapping("/{id}")
  public UserDto deactivateUser(@PathVariable UUID companyId, @PathVariable("id") UUID id) {
    return userManagementService.deactivateUser(id);
  }

  /**
   * Reactivates the user with the given ID.
   */
  @PreAuthorize("@companyAdminSecurityService.canAccessCompanyResource(#companyId, 'ROLE_FUNC_COMPANY_USER_MANAGER')")
  @PutMapping("/{id}/reactivation")
  public UserDto reactivateUser(@PathVariable UUID companyId, @PathVariable("id") UUID id) {
    return userManagementService.reactivateUser(id);
  }

  /**

   * Resends invitation email to the user.
   * 
   *
   * @param id the user ID
   *
   * @param auth0UserId the Auth0 user ID
   *
   * @param email the user's email
   */
  @PostMapping("/{id}/resend-invitation")
  @ResponseStatus(HttpStatus.OK)
  public void resendInvitation(
      @PathVariable("id") UUID id,
      @RequestParam("auth0UserId") String auth0UserId,
      @RequestParam("email") String email) {
    try {
      userInvitationService.resendInvitation(id.toString(), auth0UserId, email);
    } catch (Auth0Exception e) {
      throw new RuntimeException("Failed to resend invitation: " + e.getMessage(), e);
    }
  }

  /**
   * Retrieves user details by ID. If the user does not exist, throws {@link
   * ResponseStatusException} with 404 NOT FOUND.
   */
  @PreAuthorize("@companyAdminSecurityService.canAccessCompanyResource(#companyId, 'ROLE_FUNC_COMPANY_USER_MANAGER')")
  @GetMapping("/{id}")
  public UserDto viewUsersDetails(@PathVariable UUID companyId, @PathVariable("id") UUID id) {
    return userManagementService.getUsersDetails(id);
  }
}
