package com.coherentsolutions.pot.insuranceservice.service;

import com.auth0.client.mgmt.ManagementAPI;
import com.auth0.client.mgmt.filter.UserFilter;
import com.auth0.exception.Auth0Exception;
import com.auth0.json.mgmt.users.User;
import com.auth0.json.mgmt.users.UsersPage;
import com.coherentsolutions.pot.insuranceservice.dto.auth0.Auth0UserDto;
import com.coherentsolutions.pot.insuranceservice.mapper.Auth0UserMapper;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * Service for managing Auth0 users through the Management API.
 *
 * <p>This service provides methods to create, read, update, and delete users
 * in the Auth0 authorization server via backend APIs.
 */
@Service
@ConditionalOnProperty(name = "auth0.enabled", havingValue = "true", matchIfMissing = false)
public class Auth0UserManagementService {

  private final ManagementAPI managementAPI;
  private final Auth0UserMapper auth0UserMapper;

  public Auth0UserManagementService(ManagementAPI managementAPI, Auth0UserMapper auth0UserMapper) {
    this.managementAPI = managementAPI;
    this.auth0UserMapper = auth0UserMapper;
  }

  /**
   * Creates a new user in Auth0.
   *
   * @param user the user to create
   * @return the created user
   * @throws Auth0Exception if the operation fails
   */
  public User createUser(User user) throws Auth0Exception {
    return managementAPI.users().create(user).execute().getBody();
  }

  /**
   * Creates a new user in Auth0 using DTO.
   *
   * @param userDto the user DTO to create
   * @return the created user DTO
   * @throws Auth0Exception if the operation fails
   */
  public Auth0UserDto createUser(Auth0UserDto userDto) throws Auth0Exception {
    User user = auth0UserMapper.toAuth0User(userDto);
    User createdUser = createUser(user);
    return auth0UserMapper.toDto(createdUser);
  }

  /**
   * Retrieves a user by their ID.
   *
   * @param userId the user ID
   * @return the user, or null if not found
   * @throws Auth0Exception if the operation fails
   */
  public User getUserById(String userId) throws Auth0Exception {
    return managementAPI.users().get(userId, null).execute().getBody();
  }

  /**
   * Retrieves a user by their ID and returns as DTO.
   *
   * @param userId the user ID
   * @return the user DTO, or null if not found
   * @throws Auth0Exception if the operation fails
   */
  public Auth0UserDto getUserDtoById(String userId) throws Auth0Exception {
    User user = getUserById(userId);
    return user != null ? auth0UserMapper.toDto(user) : null;
  }

  /**
   * Retrieves all users with optional filtering.
   *
   * @param filter optional filter criteria
   * @return list of users
   * @throws Auth0Exception if the operation fails
   */
  public List<User> getUsers(UserFilter filter) throws Auth0Exception {
    UsersPage usersPage = managementAPI.users().list(filter).execute().getBody();
    return usersPage.getItems();
  }

  /**
   * Retrieves all users with optional filtering and returns as DTOs.
   *
   * @param filter optional filter criteria
   * @return list of user DTOs
   * @throws Auth0Exception if the operation fails
   */
  public List<Auth0UserDto> getUserDtos(UserFilter filter) throws Auth0Exception {
    List<User> users = getUsers(filter);
    return users.stream()
        .map(auth0UserMapper::toDto)
        .collect(Collectors.toList());
  }

  /**
   * Updates an existing user.
   *
   * @param userId the user ID
   * @param user the updated user data
   * @return the updated user
   * @throws Auth0Exception if the operation fails
   */
  public User updateUser(String userId, User user) throws Auth0Exception {
    return managementAPI.users().update(userId, user).execute().getBody();
  }

  /**
   * Updates an existing user using DTO.
   *
   * @param userId the user ID
   * @param userDto the updated user DTO
   * @return the updated user DTO
   * @throws Auth0Exception if the operation fails
   */
  public Auth0UserDto updateUser(String userId, Auth0UserDto userDto) throws Auth0Exception {
    User existingUser = getUserById(userId);
    if (existingUser == null) {
      throw new Auth0Exception("User not found: " + userId);
    }
    
    auth0UserMapper.updateUserFromDto(userDto, existingUser);
    User updatedUser = updateUser(userId, existingUser);
    return auth0UserMapper.toDto(updatedUser);
  }

  /**
   * Deletes a user by their ID.
   *
   * @param userId the user ID
   * @throws Auth0Exception if the operation fails
   */
  public void deleteUser(String userId) throws Auth0Exception {
    managementAPI.users().delete(userId).execute();
  }

  /**
   * Searches for users by email.
   *
   * @param email the email to search for
   * @return list of users with matching email
   * @throws Auth0Exception if the operation fails
   */
  public List<User> getUsersByEmail(String email) throws Auth0Exception {
    UserFilter filter = new UserFilter().withQuery("email:" + email);
    return getUsers(filter);
  }

  /**
   * Searches for users by email and returns as DTOs.
   *
   * @param email the email to search for
   * @return list of user DTOs with matching email
   * @throws Auth0Exception if the operation fails
   */
  public List<Auth0UserDto> getUserDtosByEmail(String email) throws Auth0Exception {
    UserFilter filter = new UserFilter().withQuery("email:" + email);
    return getUserDtos(filter);
  }

  /**
   * Creates a new user in Auth0 with invitation enabled.
   *
   * @param userDto the user DTO (password can be null for invitation)
   * @return the created user
   * @throws Auth0Exception if the operation fails
   */
  public User createUserWithInvitation(Auth0UserDto userDto) throws Auth0Exception {
    User user = auth0UserMapper.toAuth0User(userDto);
    
    // Configure user for invitation
    user.setEmailVerified(false);
    user.setPassword((char[]) null); // No password for invitation
    user.setConnection("Username-Password-Authentication");
    
    // Auth0 will automatically send invitation email when:
    // 1. email_verified is false
    // 2. password is null
    // 3. User is created via Management API
    
    return createUser(user);
  }
} 