package com.coherentsolutions.pot.insuranceservice.unit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
import com.coherentsolutions.pot.insuranceservice.dto.auth0.Auth0InvitationDto;
import com.coherentsolutions.pot.insuranceservice.dto.auth0.Auth0UserDto;
import com.coherentsolutions.pot.insuranceservice.mapper.Auth0UserMapper;
import com.coherentsolutions.pot.insuranceservice.service.Auth0InvitationService;
import com.coherentsolutions.pot.insuranceservice.service.Auth0PasswordService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class Auth0InvitationServiceTest {

  @Mock
  private ManagementAPI managementAPI;

  @Mock
  private Auth0UserMapper auth0UserMapper;

  @Mock
  private Auth0PasswordService auth0PasswordService;

  @Mock
  private UsersEntity usersEntity;

  @Mock
  private Request<User> createUserRequest;

  @Mock
  private Request<User> getUserRequest;

  @Mock
  private Request<User> updateUserRequest;

  @Mock
  private Request<UsersPage> listUsersRequest;

  @Mock
  private Response<User> createUserResponse;

  @Mock
  private Response<User> getUserResponse;

  @Mock
  private Response<User> updateUserResponse;

  @Mock
  private Response<UsersPage> listUsersResponse;

  private Auth0InvitationService auth0InvitationService;

  @BeforeEach
  void setUp() {
    auth0InvitationService = new Auth0InvitationService(managementAPI, auth0UserMapper, auth0PasswordService);
  }

  @Test
  @DisplayName("Should create user with invitation successfully")
  void shouldCreateUserWithInvitationSuccessfully() throws Exception {
    // Given
    Auth0InvitationDto invitationDto = new Auth0InvitationDto();
    invitationDto.setEmail("test@example.com");
    invitationDto.setName("Test User");
    invitationDto.setConnection("Username-Password-Authentication");

    User createdUser = new User();
    createdUser.setId("auth0|123");
    createdUser.setEmail("test@example.com");

    Auth0UserDto expectedDto = new Auth0UserDto();
    expectedDto.setUserId("auth0|123");
    expectedDto.setEmail("test@example.com");

    when(managementAPI.users()).thenReturn(usersEntity);
    when(usersEntity.create(any(User.class))).thenReturn(createUserRequest);
    when(createUserRequest.execute()).thenReturn(createUserResponse);
    when(createUserResponse.getBody()).thenReturn(createdUser);
    when(usersEntity.update(eq("auth0|123"), any(User.class))).thenReturn(updateUserRequest);
    when(updateUserRequest.execute()).thenReturn(updateUserResponse);
    when(auth0PasswordService.sendPasswordChangeEmail("test@example.com")).thenReturn("Success");
    when(auth0UserMapper.toDto(createdUser)).thenReturn(expectedDto);

    // When
    Auth0UserDto result = auth0InvitationService.createUserWithInvitation(invitationDto);

    // Then
    assertEquals(expectedDto, result);
    verify(auth0PasswordService).sendPasswordChangeEmail("test@example.com");
  }

  @Test
  @DisplayName("Should create user with invitation and metadata successfully")
  void shouldCreateUserWithInvitationAndMetadataSuccessfully() throws Exception {
    // Given
    Auth0InvitationDto invitationDto = new Auth0InvitationDto();
    invitationDto.setEmail("test@example.com");
    invitationDto.setName("Test User");
    invitationDto.setConnection("Username-Password-Authentication");

    Map<String, Object> userMetadata = new HashMap<>();
    userMetadata.put("key", "value");
    invitationDto.setUserMetadata(userMetadata);

    Map<String, Object> appMetadata = new HashMap<>();
    appMetadata.put("appKey", "appValue");
    invitationDto.setAppMetadata(appMetadata);

    User createdUser = new User();
    createdUser.setId("auth0|123");

    Auth0UserDto expectedDto = new Auth0UserDto();
    expectedDto.setUserId("auth0|123");

    when(managementAPI.users()).thenReturn(usersEntity);
    when(usersEntity.create(any(User.class))).thenReturn(createUserRequest);
    when(createUserRequest.execute()).thenReturn(createUserResponse);
    when(createUserResponse.getBody()).thenReturn(createdUser);
    when(usersEntity.update(eq("auth0|123"), any(User.class))).thenReturn(updateUserRequest);
    when(updateUserRequest.execute()).thenReturn(updateUserResponse);
    when(auth0PasswordService.sendPasswordChangeEmail("test@example.com")).thenReturn("Success");
    when(auth0UserMapper.toDto(createdUser)).thenReturn(expectedDto);

    // When
    Auth0UserDto result = auth0InvitationService.createUserWithInvitation(invitationDto);

    // Then
    assertEquals(expectedDto, result);
  }

  @Test
  @DisplayName("Should throw Auth0Exception when user creation fails")
  void shouldThrowAuth0ExceptionWhenUserCreationFails() throws Exception {
    // Given
    Auth0InvitationDto invitationDto = new Auth0InvitationDto();
    invitationDto.setEmail("test@example.com");
    invitationDto.setName("Test User");
    invitationDto.setConnection("Username-Password-Authentication");

    when(managementAPI.users()).thenReturn(usersEntity);
    when(usersEntity.create(any(User.class))).thenReturn(createUserRequest);
    when(createUserRequest.execute()).thenThrow(new Auth0Exception("User creation failed"));

    // When & Then
    com.coherentsolutions.pot.insuranceservice.exception.Auth0Exception exception = assertThrows(
        com.coherentsolutions.pot.insuranceservice.exception.Auth0Exception.class,
        () -> auth0InvitationService.createUserWithInvitation(invitationDto));
    assertEquals("Failed to create user invitation: Failed to create Auth0 user: User creation failed", exception.getMessage());
  }

  @Test
  @DisplayName("Should throw Auth0Exception when email verification fails")
  void shouldThrowAuth0ExceptionWhenEmailVerificationFails() throws Exception {
    // Given
    Auth0InvitationDto invitationDto = new Auth0InvitationDto();
    invitationDto.setEmail("test@example.com");
    invitationDto.setName("Test User");
    invitationDto.setConnection("Username-Password-Authentication");

    User createdUser = new User();
    createdUser.setId("auth0|123");

    when(managementAPI.users()).thenReturn(usersEntity);
    when(usersEntity.create(any(User.class))).thenReturn(createUserRequest);
    when(createUserRequest.execute()).thenReturn(createUserResponse);
    when(createUserResponse.getBody()).thenReturn(createdUser);
    when(usersEntity.update(eq("auth0|123"), any(User.class))).thenReturn(updateUserRequest);
    when(updateUserRequest.execute()).thenThrow(new Auth0Exception("Email verification failed"));

    // When & Then
    com.coherentsolutions.pot.insuranceservice.exception.Auth0Exception exception = assertThrows(
        com.coherentsolutions.pot.insuranceservice.exception.Auth0Exception.class,
        () -> auth0InvitationService.createUserWithInvitation(invitationDto));
    assertEquals("Failed to create user invitation: Failed to trigger email verification: Email verification failed", exception.getMessage());
  }

  @Test
  @DisplayName("Should handle password reset email failure gracefully")
  void shouldHandlePasswordResetEmailFailureGracefully() throws Exception {
    // Given
    Auth0InvitationDto invitationDto = new Auth0InvitationDto();
    invitationDto.setEmail("test@example.com");
    invitationDto.setName("Test User");
    invitationDto.setConnection("Username-Password-Authentication");

    User createdUser = new User();
    createdUser.setId("auth0|123");

    Auth0UserDto expectedDto = new Auth0UserDto();
    expectedDto.setUserId("auth0|123");

    when(managementAPI.users()).thenReturn(usersEntity);
    when(usersEntity.create(any(User.class))).thenReturn(createUserRequest);
    when(createUserRequest.execute()).thenReturn(createUserResponse);
    when(createUserResponse.getBody()).thenReturn(createdUser);
    when(usersEntity.update(eq("auth0|123"), any(User.class))).thenReturn(updateUserRequest);
    when(updateUserRequest.execute()).thenReturn(updateUserResponse);
    when(auth0PasswordService.sendPasswordChangeEmail("test@example.com")).thenThrow(new RuntimeException("Password reset failed"));
    when(auth0UserMapper.toDto(createdUser)).thenReturn(expectedDto);

    // When
    Auth0UserDto result = auth0InvitationService.createUserWithInvitation(invitationDto);

    // Then - Should not throw exception, user creation should succeed
    assertEquals(expectedDto, result);
  }

  @Test
  @DisplayName("Should resend invitation successfully")
  void shouldResendInvitationSuccessfully() throws Exception {
    // Given
    String userId = "auth0|123";
    String email = "test@example.com";

    User existingUser = new User();
    existingUser.setId(userId);
    existingUser.setEmail(email);

    when(managementAPI.users()).thenReturn(usersEntity);
    when(usersEntity.get(userId, null)).thenReturn(getUserRequest);
    when(getUserRequest.execute()).thenReturn(getUserResponse);
    when(getUserResponse.getBody()).thenReturn(existingUser);
    when(usersEntity.update(eq(userId), any(User.class))).thenReturn(updateUserRequest);
    when(updateUserRequest.execute()).thenReturn(updateUserResponse);
    when(auth0PasswordService.sendPasswordChangeEmail(email)).thenReturn("Success");

    // When
    auth0InvitationService.resendInvitation(userId, email);

    // Then
    verify(auth0PasswordService).sendPasswordChangeEmail(email);
  }

  @Test
  @DisplayName("Should throw Auth0Exception when user not found during resend")
  void shouldThrowAuth0ExceptionWhenUserNotFoundDuringResend() throws Exception {
    // Given
    String userId = "auth0|123";
    String email = "test@example.com";

    when(managementAPI.users()).thenReturn(usersEntity);
    when(usersEntity.get(userId, null)).thenReturn(getUserRequest);
    when(getUserRequest.execute()).thenReturn(getUserResponse);
    when(getUserResponse.getBody()).thenReturn(null);

    // When & Then
    com.coherentsolutions.pot.insuranceservice.exception.Auth0Exception exception = assertThrows(
        com.coherentsolutions.pot.insuranceservice.exception.Auth0Exception.class,
        () -> auth0InvitationService.resendInvitation(userId, email));
    assertEquals("User not found: auth0|123", exception.getMessage());
  }

  @Test
  @DisplayName("Should throw Auth0Exception when resend invitation fails")
  void shouldThrowAuth0ExceptionWhenResendInvitationFails() throws Exception {
    // Given
    String userId = "auth0|123";
    String email = "test@example.com";

    when(managementAPI.users()).thenReturn(usersEntity);
    when(usersEntity.get(userId, null)).thenReturn(getUserRequest);
    when(getUserRequest.execute()).thenThrow(new Auth0Exception("Get user failed"));

    // When & Then
    com.coherentsolutions.pot.insuranceservice.exception.Auth0Exception exception = assertThrows(
        com.coherentsolutions.pot.insuranceservice.exception.Auth0Exception.class,
        () -> auth0InvitationService.resendInvitation(userId, email));
    assertEquals("Failed to resend invitation: Get user failed", exception.getMessage());
  }

  @Test
  @DisplayName("Should return true when user exists by email")
  void shouldReturnTrueWhenUserExistsByEmail() throws Exception {
    // Given
    String email = "test@example.com";

    UsersPage usersPage = mock(UsersPage.class);
    List<User> users = new ArrayList<>();
    User user = new User();
    user.setEmail(email);
    users.add(user);
    when(usersPage.getItems()).thenReturn(users);

    when(managementAPI.users()).thenReturn(usersEntity);
    when(usersEntity.list(any(UserFilter.class))).thenReturn(listUsersRequest);
    when(listUsersRequest.execute()).thenReturn(listUsersResponse);
    when(listUsersResponse.getBody()).thenReturn(usersPage);

    // When
    boolean result = auth0InvitationService.userExistsByEmail(email);

    // Then
    assertTrue(result);
  }

  @Test
  @DisplayName("Should return false when user does not exist by email")
  void shouldReturnFalseWhenUserDoesNotExistByEmail() throws Exception {
    // Given
    String email = "test@example.com";

    UsersPage usersPage = mock(UsersPage.class);
    when(usersPage.getItems()).thenReturn(new ArrayList<>());

    when(managementAPI.users()).thenReturn(usersEntity);
    when(usersEntity.list(any(UserFilter.class))).thenReturn(listUsersRequest);
    when(listUsersRequest.execute()).thenReturn(listUsersResponse);
    when(listUsersResponse.getBody()).thenReturn(usersPage);

    // When
    boolean result = auth0InvitationService.userExistsByEmail(email);

    // Then
    assertFalse(result);
  }

  @Test
  @DisplayName("Should return false when checking user existence fails")
  void shouldReturnFalseWhenCheckingUserExistenceFails() throws Exception {
    // Given
    String email = "test@example.com";

    when(managementAPI.users()).thenReturn(usersEntity);
    when(usersEntity.list(any(UserFilter.class))).thenReturn(listUsersRequest);
    when(listUsersRequest.execute()).thenThrow(new Auth0Exception("Check failed"));

    // When
    boolean result = auth0InvitationService.userExistsByEmail(email);

    // Then
    assertFalse(result);
  }

  @Test
  @DisplayName("Should send password reset email successfully")
  void shouldSendPasswordResetEmailSuccessfully() throws Exception {
    // Given
    String email = "test@example.com";
    String userName = "Test User";

    when(auth0PasswordService.sendPasswordChangeEmail(email)).thenReturn("Success");

    // When
    auth0InvitationService.sendPasswordResetEmail(email, userName);

    // Then
    verify(auth0PasswordService).sendPasswordChangeEmail(email);
  }

  @Test
  @DisplayName("Should throw Auth0Exception when password reset email fails")
  void shouldThrowAuth0ExceptionWhenPasswordResetEmailFails() throws Exception {
    // Given
    String email = "test@example.com";
    String userName = "Test User";

    when(auth0PasswordService.sendPasswordChangeEmail(email)).thenThrow(new RuntimeException("Password reset failed"));

    // When & Then
    com.coherentsolutions.pot.insuranceservice.exception.Auth0Exception exception = assertThrows(
        com.coherentsolutions.pot.insuranceservice.exception.Auth0Exception.class,
        () -> auth0InvitationService.sendPasswordResetEmail(email, userName));
    assertEquals("Failed to send password reset email: Password reset failed", exception.getMessage());
  }

  @Test
  @DisplayName("Should handle null user metadata")
  void shouldHandleNullUserMetadata() throws Exception {
    // Given
    Auth0InvitationDto invitationDto = new Auth0InvitationDto();
    invitationDto.setEmail("test@example.com");
    invitationDto.setName("Test User");
    invitationDto.setConnection("Username-Password-Authentication");
    invitationDto.setUserMetadata(null);

    User createdUser = new User();
    createdUser.setId("auth0|123");

    Auth0UserDto expectedDto = new Auth0UserDto();
    expectedDto.setUserId("auth0|123");

    when(managementAPI.users()).thenReturn(usersEntity);
    when(usersEntity.create(any(User.class))).thenReturn(createUserRequest);
    when(createUserRequest.execute()).thenReturn(createUserResponse);
    when(createUserResponse.getBody()).thenReturn(createdUser);
    when(usersEntity.update(eq("auth0|123"), any(User.class))).thenReturn(updateUserRequest);
    when(updateUserRequest.execute()).thenReturn(updateUserResponse);
    when(auth0PasswordService.sendPasswordChangeEmail("test@example.com")).thenReturn("Success");
    when(auth0UserMapper.toDto(createdUser)).thenReturn(expectedDto);

    // When
    Auth0UserDto result = auth0InvitationService.createUserWithInvitation(invitationDto);

    // Then
    assertEquals(expectedDto, result);
  }

  @Test
  @DisplayName("Should handle null app metadata")
  void shouldHandleNullAppMetadata() throws Exception {
    // Given
    Auth0InvitationDto invitationDto = new Auth0InvitationDto();
    invitationDto.setEmail("test@example.com");
    invitationDto.setName("Test User");
    invitationDto.setConnection("Username-Password-Authentication");
    invitationDto.setAppMetadata(null);

    User createdUser = new User();
    createdUser.setId("auth0|123");

    Auth0UserDto expectedDto = new Auth0UserDto();
    expectedDto.setUserId("auth0|123");

    when(managementAPI.users()).thenReturn(usersEntity);
    when(usersEntity.create(any(User.class))).thenReturn(createUserRequest);
    when(createUserRequest.execute()).thenReturn(createUserResponse);
    when(createUserResponse.getBody()).thenReturn(createdUser);
    when(usersEntity.update(eq("auth0|123"), any(User.class))).thenReturn(updateUserRequest);
    when(updateUserRequest.execute()).thenReturn(updateUserResponse);
    when(auth0PasswordService.sendPasswordChangeEmail("test@example.com")).thenReturn("Success");
    when(auth0UserMapper.toDto(createdUser)).thenReturn(expectedDto);

    // When
    Auth0UserDto result = auth0InvitationService.createUserWithInvitation(invitationDto);

    // Then
    assertEquals(expectedDto, result);
  }

  @Test
  @DisplayName("Should handle null response body when checking user existence")
  void shouldHandleNullResponseBodyWhenCheckingUserExistence() throws Exception {
    // Given
    String email = "test@example.com";

    when(managementAPI.users()).thenReturn(usersEntity);
    when(usersEntity.list(any(UserFilter.class))).thenReturn(listUsersRequest);
    when(listUsersRequest.execute()).thenReturn(listUsersResponse);
    when(listUsersResponse.getBody()).thenReturn(null);

    // When
    boolean result = auth0InvitationService.userExistsByEmail(email);

    // Then
    assertFalse(result);
  }
} 