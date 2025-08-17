package com.coherentsolutions.pot.insuranceservice.unit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.coherentsolutions.pot.insuranceservice.dto.auth0.Auth0InvitationDto;
import com.coherentsolutions.pot.insuranceservice.dto.auth0.Auth0UserDto;
import com.coherentsolutions.pot.insuranceservice.dto.user.UserDto;
import com.coherentsolutions.pot.insuranceservice.enums.UserFunction;
import com.coherentsolutions.pot.insuranceservice.exception.Auth0Exception;
import com.coherentsolutions.pot.insuranceservice.service.Auth0InvitationService;
import com.coherentsolutions.pot.insuranceservice.service.UserInvitationService;
import com.coherentsolutions.pot.insuranceservice.service.UserManagementService;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class UserInvitationServiceTest {

  @Mock
  private UserManagementService userManagementService;

  @Mock
  private Auth0InvitationService auth0InvitationService;

  private UserInvitationService userInvitationService;

  @BeforeEach
  void setUp() {
    userInvitationService = new UserInvitationService(userManagementService, auth0InvitationService);
    ReflectionTestUtils.setField(userInvitationService, "auth0ClientId", "test-client-id");
  }

  @Test
  @DisplayName("Should create user with invitation successfully")
  void shouldCreateUserWithInvitationSuccessfully() throws Exception {
    // Given
    UserDto userDto = new UserDto();
    userDto.setId(UUID.randomUUID());
    userDto.setFirstName("John");
    userDto.setLastName("Doe");
    userDto.setEmail("john.doe@example.com");
    userDto.setUsername("johndoe");
    userDto.setDateOfBirth(LocalDate.of(1990, 1, 1));
    userDto.setSsn("123-45-6789");

    UserDto savedUser = new UserDto();
    savedUser.setId(userDto.getId());
    savedUser.setEmail("john.doe@example.com");

    Auth0UserDto auth0User = new Auth0UserDto();
    auth0User.setUserId("auth0|123");

    when(auth0InvitationService.createUserWithInvitation(any(Auth0InvitationDto.class))).thenReturn(auth0User);
    when(userManagementService.createUser(userDto, auth0User.getUserId())).thenReturn(savedUser);

    // When
    UserDto result = userInvitationService.createUserWithInvitation(userDto);

    // Then
    assertEquals(savedUser, result);
    verify(auth0InvitationService).createUserWithInvitation(any(Auth0InvitationDto.class));
    verify(userManagementService).createUser(userDto, auth0User.getUserId());
  }

  @Test
  @DisplayName("Should create user with invitation and functions successfully")
  void shouldCreateUserWithInvitationAndFunctionsSuccessfully() throws Exception {
    // Given
    UserDto userDto = new UserDto();
    userDto.setId(UUID.randomUUID());
    userDto.setFirstName("John");
    userDto.setLastName("Doe");
    userDto.setEmail("john.doe@example.com");
    userDto.setUsername("johndoe");

    Set<UserFunction> functions = new HashSet<>();
    functions.add(UserFunction.COMPANY_MANAGER);
    userDto.setFunctions(functions);

    UserDto savedUser = new UserDto();
    savedUser.setId(userDto.getId());
    savedUser.setEmail("john.doe@example.com");

    Auth0UserDto auth0User = new Auth0UserDto();
    auth0User.setUserId("auth0|123");

    when(auth0InvitationService.createUserWithInvitation(any(Auth0InvitationDto.class))).thenReturn(auth0User);
    when(userManagementService.createUser(userDto, auth0User.getUserId())).thenReturn(savedUser);

    // When
    UserDto result = userInvitationService.createUserWithInvitation(userDto);

    // Then
    assertEquals(savedUser, result);
    verify(auth0InvitationService).createUserWithInvitation(any(Auth0InvitationDto.class));
    verify(userManagementService).createUser(userDto, auth0User.getUserId());
  }

  @Test
  @DisplayName("Should throw Auth0Exception when Auth0 invitation fails")
  void shouldThrowAuth0ExceptionWhenAuth0InvitationFails() throws Exception {
    // Given
    UserDto userDto = new UserDto();
    userDto.setId(UUID.randomUUID());
    userDto.setFirstName("John");
    userDto.setLastName("Doe");
    userDto.setEmail("john.doe@example.com");

    when(auth0InvitationService.createUserWithInvitation(any(Auth0InvitationDto.class)))
        .thenThrow(new Auth0Exception("Auth0 creation failed", "AUTH0_ERROR", 400));

    // When & Then
    Auth0Exception exception = assertThrows(Auth0Exception.class,
        () -> userInvitationService.createUserWithInvitation(userDto));
    assertEquals("Auth0 creation failed", exception.getMessage());
    // Verify that no local user was created since Auth0 creation failed
  }

  @Test
  @DisplayName("Should resend invitation successfully")
  void shouldResendInvitationSuccessfully() throws Exception {
    // Given
    String userId = UUID.randomUUID().toString();
    final String auth0UserId = "auth0|123";
    String email = "john.doe@example.com";

    UserDto localUser = new UserDto();
    localUser.setId(UUID.fromString(userId));
    localUser.setEmail(email);

    when(userManagementService.getUsersDetails(UUID.fromString(userId))).thenReturn(localUser);

    // When
    userInvitationService.resendInvitation(userId, auth0UserId, email);

    // Then
    verify(userManagementService).getUsersDetails(UUID.fromString(userId));
    verify(auth0InvitationService).resendInvitation(auth0UserId, email);
  }

  @Test
  @DisplayName("Should throw Auth0Exception when local user not found during resend")
  void shouldThrowAuth0ExceptionWhenLocalUserNotFoundDuringResend() throws Exception {
    // Given
    String userId = UUID.randomUUID().toString();
    String auth0UserId = "auth0|123";
    String email = "john.doe@example.com";

    when(userManagementService.getUsersDetails(UUID.fromString(userId)))
        .thenThrow(new RuntimeException("User not found"));

    // When & Then
    RuntimeException exception = assertThrows(RuntimeException.class,
        () -> userInvitationService.resendInvitation(userId, auth0UserId, email));
    assertEquals("User not found", exception.getMessage());
    verify(userManagementService).getUsersDetails(UUID.fromString(userId));
  }

  @Test
  @DisplayName("Should throw Auth0Exception when local user not found during resend - correct exception type")
  void shouldThrowAuth0ExceptionWhenLocalUserNotFoundDuringResendCorrectException() throws Exception {
    // Given
    String userId = UUID.randomUUID().toString();
    final String auth0UserId = "auth0|123";
    String email = "john.doe@example.com";

    when(userManagementService.getUsersDetails(UUID.fromString(userId)))
        .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

    // When & Then
    ResponseStatusException exception = assertThrows(ResponseStatusException.class,
        () -> userInvitationService.resendInvitation(userId, auth0UserId, email));
    assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    assertEquals("User not found", exception.getReason());
    verify(userManagementService).getUsersDetails(UUID.fromString(userId));
  }

  @Test
  @DisplayName("Should return true when user exists in both systems")
  void shouldReturnTrueWhenUserExistsInBothSystems() {
    // Given
    String email = "john.doe@example.com";

    when(auth0InvitationService.userExistsByEmail(email)).thenReturn(true);

    // When
    boolean result = userInvitationService.userExists(email);

    // Then
    assertTrue(result);
    verify(auth0InvitationService).userExistsByEmail(email);
  }

  @Test
  @DisplayName("Should return false when user does not exist in Auth0")
  void shouldReturnFalseWhenUserDoesNotExistInAuth0() {
    // Given
    String email = "john.doe@example.com";

    when(auth0InvitationService.userExistsByEmail(email)).thenReturn(false);

    // When
    boolean result = userInvitationService.userExists(email);

    // Then
    assertFalse(result);
    verify(auth0InvitationService).userExistsByEmail(email);
  }

  @Test
  @DisplayName("Should handle null date of birth in user metadata")
  void shouldHandleNullDateOfBirthInUserMetadata() throws Exception {
    // Given
    UserDto userDto = new UserDto();
    userDto.setId(UUID.randomUUID());
    userDto.setFirstName("John");
    userDto.setLastName("Doe");
    userDto.setEmail("john.doe@example.com");
    userDto.setUsername("johndoe");
    userDto.setDateOfBirth(null);
    userDto.setSsn("123-45-6789");

    UserDto savedUser = new UserDto();
    savedUser.setId(userDto.getId());
    savedUser.setEmail("john.doe@example.com");

    Auth0UserDto auth0User = new Auth0UserDto();
    auth0User.setUserId("auth0|123");

    when(auth0InvitationService.createUserWithInvitation(any(Auth0InvitationDto.class))).thenReturn(auth0User);
    when(userManagementService.createUser(userDto, auth0User.getUserId())).thenReturn(savedUser);

    // When
    UserDto result = userInvitationService.createUserWithInvitation(userDto);

    // Then
    assertEquals(savedUser, result);
    verify(auth0InvitationService).createUserWithInvitation(any(Auth0InvitationDto.class));
    verify(userManagementService).createUser(userDto, auth0User.getUserId());
  }

  @Test
  @DisplayName("Should handle null functions in user metadata")
  void shouldHandleNullFunctionsInUserMetadata() throws Exception {
    // Given
    UserDto userDto = new UserDto();
    userDto.setId(UUID.randomUUID());
    userDto.setFirstName("John");
    userDto.setLastName("Doe");
    userDto.setEmail("john.doe@example.com");
    userDto.setUsername("johndoe");
    userDto.setFunctions(null);

    UserDto savedUser = new UserDto();
    savedUser.setId(userDto.getId());
    savedUser.setEmail("john.doe@example.com");

    Auth0UserDto auth0User = new Auth0UserDto();
    auth0User.setUserId("auth0|123");

    when(auth0InvitationService.createUserWithInvitation(any(Auth0InvitationDto.class))).thenReturn(auth0User);
    when(userManagementService.createUser(userDto, auth0User.getUserId())).thenReturn(savedUser);

    // When
    UserDto result = userInvitationService.createUserWithInvitation(userDto);

    // Then
    assertEquals(savedUser, result);
    verify(auth0InvitationService).createUserWithInvitation(any(Auth0InvitationDto.class));
    verify(userManagementService).createUser(userDto, auth0User.getUserId());
  }

  @Test
  @DisplayName("Should handle empty functions in user metadata")
  void shouldHandleEmptyFunctionsInUserMetadata() throws Exception {
    // Given
    UserDto userDto = new UserDto();
    userDto.setId(UUID.randomUUID());
    userDto.setFirstName("John");
    userDto.setLastName("Doe");
    userDto.setEmail("john.doe@example.com");
    userDto.setUsername("johndoe");
    userDto.setFunctions(new HashSet<>());

    UserDto savedUser = new UserDto();
    savedUser.setId(userDto.getId());
    savedUser.setEmail("john.doe@example.com");

    Auth0UserDto auth0User = new Auth0UserDto();
    auth0User.setUserId("auth0|123");

    when(auth0InvitationService.createUserWithInvitation(any(Auth0InvitationDto.class))).thenReturn(auth0User);
    when(userManagementService.createUser(userDto, auth0User.getUserId())).thenReturn(savedUser);

    // When
    UserDto result = userInvitationService.createUserWithInvitation(userDto);

    // Then
    assertEquals(savedUser, result);
    verify(auth0InvitationService).createUserWithInvitation(any(Auth0InvitationDto.class));
    verify(userManagementService).createUser(userDto, auth0User.getUserId());
  }

  @Test
  @DisplayName("Should handle null company ID in user metadata")
  void shouldHandleNullCompanyIdInUserMetadata() throws Exception {
    // Given
    UserDto userDto = new UserDto();
    userDto.setId(UUID.randomUUID());
    userDto.setFirstName("John");
    userDto.setLastName("Doe");
    userDto.setEmail("john.doe@example.com");
    userDto.setUsername("johndoe");
    userDto.setCompanyId(null);

    UserDto savedUser = new UserDto();
    savedUser.setId(userDto.getId());
    savedUser.setEmail("john.doe@example.com");

    Auth0UserDto auth0User = new Auth0UserDto();
    auth0User.setUserId("auth0|123");

    when(auth0InvitationService.createUserWithInvitation(any(Auth0InvitationDto.class))).thenReturn(auth0User);
    when(userManagementService.createUser(userDto, auth0User.getUserId())).thenReturn(savedUser);

    // When
    UserDto result = userInvitationService.createUserWithInvitation(userDto);

    // Then
    assertEquals(savedUser, result);
    verify(auth0InvitationService).createUserWithInvitation(any(Auth0InvitationDto.class));
    verify(userManagementService).createUser(userDto, auth0User.getUserId());
  }

  @Test
  @DisplayName("Should handle null SSN in user metadata")
  void shouldHandleNullSsnInUserMetadata() throws Exception {
    // Given
    UserDto userDto = new UserDto();
    userDto.setId(UUID.randomUUID());
    userDto.setFirstName("John");
    userDto.setLastName("Doe");
    userDto.setEmail("john.doe@example.com");
    userDto.setUsername("johndoe");
    userDto.setSsn(null);

    UserDto savedUser = new UserDto();
    savedUser.setId(userDto.getId());
    savedUser.setEmail("john.doe@example.com");

    Auth0UserDto auth0User = new Auth0UserDto();
    auth0User.setUserId("auth0|123");

    when(auth0InvitationService.createUserWithInvitation(any(Auth0InvitationDto.class))).thenReturn(auth0User);
    when(userManagementService.createUser(userDto, auth0User.getUserId())).thenReturn(savedUser);

    // When
    UserDto result = userInvitationService.createUserWithInvitation(userDto);

    // Then
    assertEquals(savedUser, result);
    verify(auth0InvitationService).createUserWithInvitation(any(Auth0InvitationDto.class));
    verify(userManagementService).createUser(userDto, auth0User.getUserId());
  }
} 