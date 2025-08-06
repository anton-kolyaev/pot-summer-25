package com.coherentsolutions.pot.insuranceservice.unit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.coherentsolutions.pot.insuranceservice.dto.auth0.Auth0InvitationDto;
import com.coherentsolutions.pot.insuranceservice.dto.auth0.Auth0UserDto;
import com.coherentsolutions.pot.insuranceservice.dto.user.UserDto;
import com.coherentsolutions.pot.insuranceservice.enums.UserFunction;
import com.coherentsolutions.pot.insuranceservice.enums.UserStatus;
import com.coherentsolutions.pot.insuranceservice.exception.Auth0Exception;
import com.coherentsolutions.pot.insuranceservice.service.Auth0InvitationService;
import com.coherentsolutions.pot.insuranceservice.service.UserInvitationService;
import com.coherentsolutions.pot.insuranceservice.service.UserManagementService;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserInvitationServiceTest {

  @Mock
  private UserManagementService userManagementService;

  @Mock
  private Auth0InvitationService auth0InvitationService;

  @InjectMocks
  private UserInvitationService userInvitationService;

  private UserDto testUserDto;
  private Auth0UserDto testAuth0UserDto;

  @BeforeEach
  void setUp() {
    testUserDto = UserDto.builder()
        .id(UUID.randomUUID())
        .firstName("John")
        .lastName("Doe")
        .username("johndoe")
        .email("john.doe@example.com")
        .dateOfBirth(LocalDate.of(1990, 1, 1))
        .ssn("123-45-6789")
        .addressData(List.of())
        .phoneData(List.of())
        .functions(Set.of(UserFunction.COMPANY_MANAGER))
        .status(UserStatus.ACTIVE)
        .companyId(UUID.randomUUID())
        .build();

    testAuth0UserDto = new Auth0UserDto();
    testAuth0UserDto.setUserId("auth0|123456");
    testAuth0UserDto.setEmail("john.doe@example.com");
    testAuth0UserDto.setName("John Doe");
  }

  @Test
  @DisplayName("Should create user with invitation successfully")
  void shouldCreateUserWithInvitationSuccessfully() throws Auth0Exception {
    // Given
    when(userManagementService.createUser(testUserDto)).thenReturn(testUserDto);
    when(auth0InvitationService.createUserWithInvitation(any(Auth0InvitationDto.class)))
        .thenReturn(testAuth0UserDto);

    // When
    UserDto result = userInvitationService.createUserWithInvitation(testUserDto);

    // Then
    assertNotNull(result);
    assertEquals(testUserDto.getId(), result.getId());
    assertEquals(testUserDto.getEmail(), result.getEmail());
    
    verify(userManagementService).createUser(testUserDto);
    verify(auth0InvitationService).createUserWithInvitation(any(Auth0InvitationDto.class));
  }

  @Test
  @DisplayName("Should throw Auth0Exception when Auth0 invitation fails")
  void shouldThrowAuth0ExceptionWhenAuth0InvitationFails() throws Auth0Exception {
    // Given
    when(userManagementService.createUser(testUserDto)).thenReturn(testUserDto);
    when(auth0InvitationService.createUserWithInvitation(any(Auth0InvitationDto.class)))
        .thenThrow(new Auth0Exception("Auth0 invitation failed"));

    // When & Then
    Auth0Exception exception = assertThrows(Auth0Exception.class,
        () -> userInvitationService.createUserWithInvitation(testUserDto));
    
    assertEquals("Auth0 invitation failed", exception.getMessage());
    
    verify(userManagementService).createUser(testUserDto);
    verify(auth0InvitationService).createUserWithInvitation(any(Auth0InvitationDto.class));
  }

  @Test
  @DisplayName("Should resend invitation successfully")
  void shouldResendInvitationSuccessfully() throws Auth0Exception {
    // Given
    String userId = UUID.randomUUID().toString();
    String auth0UserId = "auth0|123456";
    String email = "john.doe@example.com";
    
    when(userManagementService.getUsersDetails(any(UUID.class))).thenReturn(testUserDto);
    doNothing().when(auth0InvitationService).resendInvitation(auth0UserId, email);

    // When
    userInvitationService.resendInvitation(userId, auth0UserId, email);

    // Then
    verify(userManagementService).getUsersDetails(UUID.fromString(userId));
    verify(auth0InvitationService).resendInvitation(auth0UserId, email);
  }

  @Test
  @DisplayName("Should throw Auth0Exception when resending invitation fails")
  void shouldThrowAuth0ExceptionWhenResendingInvitationFails() throws Auth0Exception {
    // Given
    String userId = UUID.randomUUID().toString();
    String auth0UserId = "auth0|123456";
    String email = "john.doe@example.com";
    
    when(userManagementService.getUsersDetails(any(UUID.class))).thenReturn(testUserDto);
    doThrow(new Auth0Exception("Resend failed")).when(auth0InvitationService)
        .resendInvitation(auth0UserId, email);

    // When & Then
    Auth0Exception exception = assertThrows(Auth0Exception.class,
        () -> userInvitationService.resendInvitation(userId, auth0UserId, email));
    
    assertEquals("Resend failed", exception.getMessage());
    
    verify(userManagementService).getUsersDetails(UUID.fromString(userId));
    verify(auth0InvitationService).resendInvitation(auth0UserId, email);
  }

  @Test
  @DisplayName("Should check user existence correctly")
  void shouldCheckUserExistenceCorrectly() {
    // Given
    String email = "john.doe@example.com";
    when(auth0InvitationService.userExistsByEmail(email)).thenReturn(true);

    // When
    boolean exists = userInvitationService.userExists(email);

    // Then
    assertTrue(exists);
    verify(auth0InvitationService).userExistsByEmail(email);
  }

  @Test
  @DisplayName("Should build invitation DTO correctly")
  void shouldBuildInvitationDtoCorrectly() throws Auth0Exception {
    // Given
    when(userManagementService.createUser(testUserDto)).thenReturn(testUserDto);
    when(auth0InvitationService.createUserWithInvitation(any(Auth0InvitationDto.class)))
        .thenReturn(testAuth0UserDto);

    // When
    userInvitationService.createUserWithInvitation(testUserDto);

    // Then
    verify(auth0InvitationService).createUserWithInvitation(argThat(invitationDto -> {
      assertEquals(testUserDto.getEmail(), invitationDto.getEmail());
      assertEquals("John Doe", invitationDto.getName());
      assertNotNull(invitationDto.getUserMetadata());
      assertEquals(testUserDto.getFirstName(), invitationDto.getUserMetadata().get("firstName"));
      assertEquals(testUserDto.getLastName(), invitationDto.getUserMetadata().get("lastName"));
      assertEquals(testUserDto.getUsername(), invitationDto.getUserMetadata().get("username"));
      return true;
    }));
  }
} 