package com.coherentsolutions.pot.insuranceservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.auth0.client.mgmt.ManagementAPI;
import com.auth0.client.mgmt.UsersEntity;
import com.auth0.client.mgmt.filter.UserFilter;
import com.auth0.exception.Auth0Exception;
import com.auth0.json.mgmt.users.User;
import com.auth0.json.mgmt.users.UsersPage;
import com.auth0.net.Request;
import com.auth0.net.Response;
import com.coherentsolutions.pot.insuranceservice.dto.auth0.Auth0UserDto;
import com.coherentsolutions.pot.insuranceservice.mapper.Auth0UserMapper;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for Auth0UserManagementService.
 *
 * <p>Tests cover all service methods with proper mocking of ManagementAPI and Auth0UserMapper.
 */
@ExtendWith(MockitoExtension.class)
class Auth0UserManagementServiceTest {

  @Mock
  private ManagementAPI managementAPI;

  @Mock
  private Auth0UserMapper auth0UserMapper;

  @Mock
  private UsersEntity usersEntity;

  @Mock
  private UsersPage usersPage;

  private Auth0UserManagementService auth0UserManagementService;

  @BeforeEach
  void setUp() {
    auth0UserManagementService = new Auth0UserManagementService(managementAPI, auth0UserMapper);
    when(managementAPI.users()).thenReturn(usersEntity);
  }

  @Test
  void testCreateUserWithValidUserReturnsUser() throws Auth0Exception {
    // Given
    User inputUser = new User();
    inputUser.setEmail("test@example.com");
    inputUser.setName("Test User");

    User expectedUser = new User();
    expectedUser.setId("auth0|123");
    expectedUser.setEmail("test@example.com");
    expectedUser.setName("Test User");

    // Mock the ManagementAPI chain using Request and Response mocks
    Request<User> userRequest = mock(Request.class);
    Response<User> userResponse = mock(Response.class);
    when(usersEntity.create(any(User.class))).thenReturn(userRequest);
    when(userRequest.execute()).thenReturn(userResponse);
    when(userResponse.getBody()).thenReturn(expectedUser);

    // When
    User result = auth0UserManagementService.createUser(inputUser);

    // Then
    assertNotNull(result);
    assertEquals("auth0|123", result.getId());
    assertEquals("test@example.com", result.getEmail());
    assertEquals("Test User", result.getName());
  }

  @Test
  void testCreateUserWithValidDtoReturnsUserDto() throws Auth0Exception {
    // Given
    Auth0UserDto inputDto = new Auth0UserDto("test@example.com", "password123", "Test User");
    User mappedUser = new User();
    mappedUser.setEmail("test@example.com");
    mappedUser.setName("Test User");

    User createdUser = new User();
    createdUser.setId("auth0|123");
    createdUser.setEmail("test@example.com");
    createdUser.setName("Test User");

    Auth0UserDto expectedDto = new Auth0UserDto("test@example.com", null, "Test User");
    expectedDto.setUserId("auth0|123");

    Request<User> userRequest = mock(Request.class);
    Response<User> userResponse = mock(Response.class);
    when(auth0UserMapper.toAuth0User(inputDto)).thenReturn(mappedUser);
    when(usersEntity.create(mappedUser)).thenReturn(userRequest);
    when(userRequest.execute()).thenReturn(userResponse);
    when(userResponse.getBody()).thenReturn(createdUser);
    when(auth0UserMapper.toDto(createdUser)).thenReturn(expectedDto);

    // When
    Auth0UserDto result = auth0UserManagementService.createUser(inputDto);

    // Then
    assertNotNull(result);
    assertEquals("auth0|123", result.getUserId());
    assertEquals("test@example.com", result.getEmail());
    assertEquals("Test User", result.getName());
    verify(auth0UserMapper).toAuth0User(inputDto);
    verify(auth0UserMapper).toDto(createdUser);
  }

  @Test
  void testCreateUserWhenAuth0ExceptionThrowsException() throws Auth0Exception {
    // Given
    User inputUser = new User();
    inputUser.setEmail("test@example.com");

    Request<User> userRequest = mock(Request.class);
    when(usersEntity.create(any(User.class))).thenReturn(userRequest);
    when(userRequest.execute()).thenThrow(new Auth0Exception("Auth0 error"));

    // When & Then
    assertThrows(Auth0Exception.class, () -> auth0UserManagementService.createUser(inputUser));
  }

  @Test
  void testGetUserByIdWithValidIdReturnsUser() throws Auth0Exception {
    // Given
    String userId = "auth0|123";
    User expectedUser = new User();
    expectedUser.setId(userId);
    expectedUser.setEmail("test@example.com");
    expectedUser.setName("Test User");

    Request<User> userRequest = mock(Request.class);
    Response<User> userResponse = mock(Response.class);
    when(usersEntity.get(eq(userId), eq(null))).thenReturn(userRequest);
    when(userRequest.execute()).thenReturn(userResponse);
    when(userResponse.getBody()).thenReturn(expectedUser);

    // When
    User result = auth0UserManagementService.getUserById(userId);

    // Then
    assertNotNull(result);
    assertEquals(userId, result.getId());
    assertEquals("test@example.com", result.getEmail());
    assertEquals("Test User", result.getName());
  }

  @Test
  void testGetUserByIdWhenUserNotFoundReturnsNull() throws Auth0Exception {
    // Given
    String userId = "auth0|nonexistent";

    Request<User> userRequest = mock(Request.class);
    Response<User> userResponse = mock(Response.class);
    when(usersEntity.get(eq(userId), eq(null))).thenReturn(userRequest);
    when(userRequest.execute()).thenReturn(userResponse);
    when(userResponse.getBody()).thenReturn(null);

    // When
    User result = auth0UserManagementService.getUserById(userId);

    // Then
    assertEquals(null, result);
  }

  @Test
  void testGetUserDtoByIdWithValidIdReturnsUserDto() throws Auth0Exception {
    // Given
    String userId = "auth0|123";
    User user = new User();
    user.setId(userId);
    user.setEmail("test@example.com");
    user.setName("Test User");

    Auth0UserDto expectedDto = new Auth0UserDto("test@example.com", null, "Test User");
    expectedDto.setUserId(userId);

    Request<User> userRequest = mock(Request.class);
    Response<User> userResponse = mock(Response.class);
    when(usersEntity.get(eq(userId), eq(null))).thenReturn(userRequest);
    when(userRequest.execute()).thenReturn(userResponse);
    when(userResponse.getBody()).thenReturn(user);
    when(auth0UserMapper.toDto(user)).thenReturn(expectedDto);

    // When
    Auth0UserDto result = auth0UserManagementService.getUserDtoById(userId);

    // Then
    assertNotNull(result);
    assertEquals(userId, result.getUserId());
    assertEquals("test@example.com", result.getEmail());
    assertEquals("Test User", result.getName());
    verify(auth0UserMapper).toDto(user);
  }

  @Test
  void testGetUserDtoByIdWithNullUserReturnsNull() throws Auth0Exception {
    // Given
    String userId = "auth0|nonexistent";

    Request<User> userRequest = mock(Request.class);
    Response<User> userResponse = mock(Response.class);
    when(usersEntity.get(eq(userId), eq(null))).thenReturn(userRequest);
    when(userRequest.execute()).thenReturn(userResponse);
    when(userResponse.getBody()).thenReturn(null);

    // When
    Auth0UserDto result = auth0UserManagementService.getUserDtoById(userId);

    // Then
    assertEquals(null, result);
  }

  @Test
  void testGetUsersWithFilterReturnsUserList() throws Auth0Exception {
    // Given
    UserFilter filter = new UserFilter();
    User user1 = new User();
    user1.setId("auth0|1");
    user1.setEmail("user1@example.com");
    User user2 = new User();
    user2.setId("auth0|2");
    user2.setEmail("user2@example.com");
    List<User> expectedUsers = Arrays.asList(user1, user2);

    Request<UsersPage> usersPageRequest = mock(Request.class);
    Response<UsersPage> usersPageResponse = mock(Response.class);
    when(usersEntity.list(filter)).thenReturn(usersPageRequest);
    when(usersPageRequest.execute()).thenReturn(usersPageResponse);
    when(usersPageResponse.getBody()).thenReturn(usersPage);
    when(usersPage.getItems()).thenReturn(expectedUsers);

    // When
    List<User> result = auth0UserManagementService.getUsers(filter);

    // Then
    assertNotNull(result);
    assertEquals(2, result.size());
    assertEquals("auth0|1", result.get(0).getId());
    assertEquals("auth0|2", result.get(1).getId());
  }

  @Test
  void testGetUsersWithNullFilterReturnsAllUsers() throws Auth0Exception {
    // Given
    UserFilter filter = null;
    User user1 = new User();
    user1.setId("auth0|1");
    user1.setEmail("user1@example.com");
    List<User> expectedUsers = Arrays.asList(user1);

    Request<UsersPage> usersPageRequest = mock(Request.class);
    Response<UsersPage> usersPageResponse = mock(Response.class);
    when(usersEntity.list(null)).thenReturn(usersPageRequest);
    when(usersPageRequest.execute()).thenReturn(usersPageResponse);
    when(usersPageResponse.getBody()).thenReturn(usersPage);
    when(usersPage.getItems()).thenReturn(expectedUsers);

    // When
    List<User> result = auth0UserManagementService.getUsers(filter);

    // Then
    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals("auth0|1", result.get(0).getId());
  }

  @Test
  void testGetUserDtosWithFilterReturnsUserDtoList() throws Auth0Exception {
    // Given
    UserFilter filter = new UserFilter();
    User user1 = new User();
    user1.setId("auth0|1");
    user1.setEmail("user1@example.com");
    User user2 = new User();
    user2.setId("auth0|2");
    user2.setEmail("user2@example.com");
    List<User> users = Arrays.asList(user1, user2);

    Auth0UserDto dto1 = new Auth0UserDto("user1@example.com", null, "User One");
    dto1.setUserId("auth0|1");
    Auth0UserDto dto2 = new Auth0UserDto("user2@example.com", null, "User Two");
    dto2.setUserId("auth0|2");
    List<Auth0UserDto> expectedDtos = Arrays.asList(dto1, dto2);

    Request<UsersPage> usersPageRequest = mock(Request.class);
    Response<UsersPage> usersPageResponse = mock(Response.class);
    when(usersEntity.list(filter)).thenReturn(usersPageRequest);
    when(usersPageRequest.execute()).thenReturn(usersPageResponse);
    when(usersPageResponse.getBody()).thenReturn(usersPage);
    when(usersPage.getItems()).thenReturn(users);
    when(auth0UserMapper.toDto(user1)).thenReturn(dto1);
    when(auth0UserMapper.toDto(user2)).thenReturn(dto2);

    // When
    List<Auth0UserDto> result = auth0UserManagementService.getUserDtos(filter);

    // Then
    assertNotNull(result);
    assertEquals(2, result.size());
    assertEquals("auth0|1", result.get(0).getUserId());
    assertEquals("auth0|2", result.get(1).getUserId());
    verify(auth0UserMapper).toDto(user1);
    verify(auth0UserMapper).toDto(user2);
  }

  @Test
  void testUpdateUserWithValidDataReturnsUpdatedUser() throws Auth0Exception {
    // Given
    String userId = "auth0|123";
    User inputUser = new User();
    inputUser.setName("Updated Name");

    User expectedUser = new User();
    expectedUser.setId(userId);
    expectedUser.setEmail("test@example.com");
    expectedUser.setName("Updated Name");

    Request<User> userRequest = mock(Request.class);
    Response<User> userResponse = mock(Response.class);
    when(usersEntity.update(eq(userId), eq(inputUser))).thenReturn(userRequest);
    when(userRequest.execute()).thenReturn(userResponse);
    when(userResponse.getBody()).thenReturn(expectedUser);

    // When
    User result = auth0UserManagementService.updateUser(userId, inputUser);

    // Then
    assertNotNull(result);
    assertEquals(userId, result.getId());
    assertEquals("Updated Name", result.getName());
  }

  @Test
  void testUpdateUserWithValidDtoReturnsUpdatedUserDto() throws Auth0Exception {
    // Given
    String userId = "auth0|123";
    Auth0UserDto inputDto = new Auth0UserDto("test@example.com", "newpassword", "Updated Name");

    User existingUser = new User();
    existingUser.setId(userId);
    existingUser.setEmail("test@example.com");
    existingUser.setName("Old Name");

    User updatedUser = new User();
    updatedUser.setId(userId);
    updatedUser.setEmail("test@example.com");
    updatedUser.setName("Updated Name");

    Auth0UserDto expectedDto = new Auth0UserDto("test@example.com", null, "Updated Name");
    expectedDto.setUserId(userId);

    Request<User> getUserRequest = mock(Request.class);
    Response<User> getUserResponse = mock(Response.class);
    Request<User> updateUserRequest = mock(Request.class);
    Response<User> updateUserResponse = mock(Response.class);
    when(usersEntity.get(eq(userId), eq(null))).thenReturn(getUserRequest);
    when(getUserRequest.execute()).thenReturn(getUserResponse);
    when(getUserResponse.getBody()).thenReturn(existingUser);
    when(usersEntity.update(eq(userId), eq(existingUser))).thenReturn(updateUserRequest);
    when(updateUserRequest.execute()).thenReturn(updateUserResponse);
    when(updateUserResponse.getBody()).thenReturn(updatedUser);
    when(auth0UserMapper.toDto(updatedUser)).thenReturn(expectedDto);

    // When
    Auth0UserDto result = auth0UserManagementService.updateUser(userId, inputDto);

    // Then
    assertNotNull(result);
    assertEquals(userId, result.getUserId());
    assertEquals("Updated Name", result.getName());
    verify(auth0UserMapper).updateUserFromDto(inputDto, existingUser);
    verify(auth0UserMapper).toDto(updatedUser);
  }

  @Test
  void testUpdateUserWhenUserNotFoundThrowsException() throws Auth0Exception {
    // Given
    String userId = "auth0|nonexistent";
    Auth0UserDto inputDto = new Auth0UserDto("test@example.com", "password", "Test User");

    Request<User> userRequest = mock(Request.class);
    Response<User> userResponse = mock(Response.class);
    when(usersEntity.get(eq(userId), eq(null))).thenReturn(userRequest);
    when(userRequest.execute()).thenReturn(userResponse);
    when(userResponse.getBody()).thenReturn(null);

    // When & Then
    Auth0Exception exception = assertThrows(Auth0Exception.class, 
        () -> auth0UserManagementService.updateUser(userId, inputDto));
    assertTrue(exception.getMessage().contains("User not found"));
  }

  @Test
  void testDeleteUserWithValidIdExecutesSuccessfully() throws Auth0Exception {
    // Given
    String userId = "auth0|123";

    Request<Void> voidRequest = mock(Request.class);
    Response<Void> voidResponse = mock(Response.class);
    when(usersEntity.delete(userId)).thenReturn(voidRequest);
    when(voidRequest.execute()).thenReturn(voidResponse);

    // When
    auth0UserManagementService.deleteUser(userId);

    // Then
    // Verify the method was called - the void method doesn't return anything
  }

  @Test
  void testDeleteUserWhenAuth0ExceptionThrowsException() throws Auth0Exception {
    // Given
    String userId = "auth0|nonexistent";

    Request<Void> voidRequest = mock(Request.class);
    when(usersEntity.delete(userId)).thenReturn(voidRequest);
    when(voidRequest.execute()).thenThrow(new Auth0Exception("User not found"));

    // When & Then
    assertThrows(Auth0Exception.class, () -> auth0UserManagementService.deleteUser(userId));
  }

  @Test
  void testGetUsersByEmailWithValidEmailReturnsMatchingUsers() throws Auth0Exception {
    // Given
    String email = "test@example.com";
    User user = new User();
    user.setId("auth0|123");
    user.setEmail(email);
    user.setName("Test User");
    List<User> expectedUsers = Arrays.asList(user);

    Request<UsersPage> usersPageRequest = mock(Request.class);
    Response<UsersPage> usersPageResponse = mock(Response.class);
    when(usersEntity.list(any(UserFilter.class))).thenReturn(usersPageRequest);
    when(usersPageRequest.execute()).thenReturn(usersPageResponse);
    when(usersPageResponse.getBody()).thenReturn(usersPage);
    when(usersPage.getItems()).thenReturn(expectedUsers);

    // When
    List<User> result = auth0UserManagementService.getUsersByEmail(email);

    // Then
    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(email, result.get(0).getEmail());
  }

  @Test
  void testGetUserDtosByEmailWithValidEmailReturnsMatchingUserDtos() throws Auth0Exception {
    // Given
    String email = "test@example.com";
    User user = new User();
    user.setId("auth0|123");
    user.setEmail(email);
    user.setName("Test User");
    List<User> users = Arrays.asList(user);

    Auth0UserDto expectedDto = new Auth0UserDto(email, null, "Test User");
    expectedDto.setUserId("auth0|123");

    Request<UsersPage> usersPageRequest = mock(Request.class);
    Response<UsersPage> usersPageResponse = mock(Response.class);
    when(usersEntity.list(any(UserFilter.class))).thenReturn(usersPageRequest);
    when(usersPageRequest.execute()).thenReturn(usersPageResponse);
    when(usersPageResponse.getBody()).thenReturn(usersPage);
    when(usersPage.getItems()).thenReturn(users);
    when(auth0UserMapper.toDto(user)).thenReturn(expectedDto);

    // When
    List<Auth0UserDto> result = auth0UserManagementService.getUserDtosByEmail(email);

    // Then
    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(email, result.get(0).getEmail());
    assertEquals("auth0|123", result.get(0).getUserId());
    verify(auth0UserMapper).toDto(user);
  }

  @Test
  void testGetUserDtosWithNullFilterReturnsAllUserDtos() throws Auth0Exception {
    // Given
    UserFilter filter = null;
    User user = new User();
    user.setId("auth0|123");
    user.setEmail("test@example.com");
    List<User> users = Arrays.asList(user);

    Auth0UserDto expectedDto = new Auth0UserDto("test@example.com", null, "Test User");
    expectedDto.setUserId("auth0|123");

    Request<UsersPage> usersPageRequest = mock(Request.class);
    Response<UsersPage> usersPageResponse = mock(Response.class);
    when(usersEntity.list(null)).thenReturn(usersPageRequest);
    when(usersPageRequest.execute()).thenReturn(usersPageResponse);
    when(usersPageResponse.getBody()).thenReturn(usersPage);
    when(usersPage.getItems()).thenReturn(users);
    when(auth0UserMapper.toDto(user)).thenReturn(expectedDto);

    // When
    List<Auth0UserDto> result = auth0UserManagementService.getUserDtos(filter);

    // Then
    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals("auth0|123", result.get(0).getUserId());
    verify(auth0UserMapper).toDto(user);
  }
} 