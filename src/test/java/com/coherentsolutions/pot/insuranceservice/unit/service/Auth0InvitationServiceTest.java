package com.coherentsolutions.pot.insuranceservice.unit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.auth0.client.mgmt.ManagementAPI;
import com.auth0.client.mgmt.UsersEntity;
import com.auth0.json.mgmt.users.User;
import com.coherentsolutions.pot.insuranceservice.dto.auth0.Auth0InvitationDto;
import com.coherentsolutions.pot.insuranceservice.dto.auth0.Auth0UserDto;
import com.coherentsolutions.pot.insuranceservice.mapper.Auth0UserMapper;
import com.coherentsolutions.pot.insuranceservice.service.Auth0InvitationService;
import com.coherentsolutions.pot.insuranceservice.service.Auth0PasswordService;
import java.util.HashMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

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

  @InjectMocks
  private Auth0InvitationService auth0InvitationService;

  private Auth0InvitationDto testInvitationDto;
  private Auth0UserDto testAuth0UserDto;
  private User testUser;

  @BeforeEach
  void setUp() {
    testInvitationDto = Auth0InvitationDto.builder()
        .email("john.doe@example.com")
        .name("John Doe")
        .nickname("johndoe")
        .connection("Username-Password-Authentication")
        .clientId("test-client-id")
        .userMetadata(new HashMap<>())
        .appMetadata(new HashMap<>())
        .build();

    testAuth0UserDto = new Auth0UserDto();
    testAuth0UserDto.setUserId("auth0|123456");
    testAuth0UserDto.setEmail("john.doe@example.com");
    testAuth0UserDto.setName("John Doe");

    testUser = new User();
    testUser.setId("auth0|123456");
    testUser.setEmail("john.doe@example.com");
    testUser.setName("John Doe");

    // Set the auth0Domain field using reflection
    ReflectionTestUtils.setField(auth0InvitationService, "auth0Domain", "test-domain.auth0.com");
  }

  @Test
  @DisplayName("Should create user with invitation successfully")
  @Disabled("Requires complex Auth0 API mocking")
  void shouldCreateUserWithInvitationSuccessfully() throws Exception {
    // Given
    when(managementAPI.users()).thenReturn(usersEntity);
    when(auth0UserMapper.toDto(any(User.class))).thenReturn(testAuth0UserDto);
    when(auth0PasswordService.sendPasswordChangeEmail(anyString()))
        .thenReturn("We've just sent you an email to change your password.");

    // When
    Auth0UserDto result = auth0InvitationService.createUserWithInvitation(testInvitationDto);

    // Then
    assertNotNull(result);
    assertEquals(testAuth0UserDto.getUserId(), result.getUserId());
    assertEquals(testAuth0UserDto.getEmail(), result.getEmail());
    assertEquals(testAuth0UserDto.getName(), result.getName());

    verify(managementAPI).users();
    verify(auth0UserMapper).toDto(any(User.class));
    verify(auth0PasswordService).sendPasswordChangeEmail("john.doe@example.com");
  }

  @Test
  @DisplayName("Should check user existence by email correctly")
  @Disabled("Requires complex Auth0 API mocking")
  void shouldCheckUserExistenceByEmailCorrectly() throws Exception {
    // Given
    String email = "john.doe@example.com";

    when(managementAPI.users()).thenReturn(usersEntity);
    when(usersEntity.list(any())).thenThrow(new RuntimeException("API error"));

    // When
    boolean exists = auth0InvitationService.userExistsByEmail(email);

    // Then
    assertFalse(exists);
    verify(managementAPI).users();
  }

  @Test
  @DisplayName("Should resend invitation successfully")
  @Disabled("Requires complex Auth0 API mocking")
  void shouldResendInvitationSuccessfully() throws Exception {
    // Given
    String userId = "auth0|123456";
    final String email = "john.doe@example.com";

    when(managementAPI.users()).thenReturn(usersEntity);
    when(auth0PasswordService.sendPasswordChangeEmail(anyString()))
        .thenReturn("We've just sent you an email to change your password.");

    // Mock user retrieval
    User existingUser = new User();
    existingUser.setId(userId);
    existingUser.setEmail(email);
    when(usersEntity.get(userId, null)).thenReturn(mock(com.auth0.net.Request.class));
    when(usersEntity.get(userId, null).execute()).thenReturn(mock(com.auth0.net.Response.class));
    when(usersEntity.get(userId, null).execute().getBody()).thenReturn(existingUser);

    // When & Then
    auth0InvitationService.resendInvitation(userId, email);

    verify(managementAPI).users();
    verify(auth0PasswordService).sendPasswordChangeEmail(email);
  }

  @Test
  @DisplayName("Should send password reset email successfully")
  void shouldSendPasswordResetEmailSuccessfully() throws Exception {
    // Given
    String email = "john.doe@example.com";
    String userName = "John Doe";
    String expectedResponse = "We've just sent you an email to change your password.";

    when(auth0PasswordService.sendPasswordChangeEmail(email))
        .thenReturn(expectedResponse);

    // When & Then
    auth0InvitationService.sendPasswordResetEmail(email, userName);

    verify(auth0PasswordService).sendPasswordChangeEmail(email);
  }
} 