package com.coherentsolutions.pot.insuranceservice.unit.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.auth0.client.mgmt.ManagementAPI;
import com.auth0.client.mgmt.UsersEntity;
import com.auth0.json.mgmt.users.User;
import com.coherentsolutions.pot.insuranceservice.dto.auth0.Auth0InvitationDto;
import com.coherentsolutions.pot.insuranceservice.dto.auth0.Auth0UserDto;
import com.coherentsolutions.pot.insuranceservice.exception.Auth0Exception;
import com.coherentsolutions.pot.insuranceservice.mapper.Auth0UserMapper;
import com.coherentsolutions.pot.insuranceservice.service.Auth0InvitationService;
import java.util.HashMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class Auth0InvitationServiceTest {

  @Mock
  private ManagementAPI managementAPI;

  @Mock
  private Auth0UserMapper auth0UserMapper;

  @Mock
  private UsersEntity usersEntity;

  @InjectMocks
  private Auth0InvitationService auth0InvitationService;

  private Auth0InvitationDto testInvitationDto;
  private Auth0UserDto testAuth0UserDto;

  @BeforeEach
  void setUp() {
    testInvitationDto = Auth0InvitationDto.builder()
        .email("john.doe@example.com")
        .name("John Doe")
        .nickname("johndoe")
        .userMetadata(new HashMap<>())
        .appMetadata(new HashMap<>())
        .build();

    testAuth0UserDto = new Auth0UserDto();
    testAuth0UserDto.setUserId("auth0|123456");
    testAuth0UserDto.setEmail("john.doe@example.com");
    testAuth0UserDto.setName("John Doe");
  }

  @Test
  @DisplayName("Should create user with invitation successfully")
  void shouldCreateUserWithInvitationSuccessfully() throws Exception {
    // Given
    when(managementAPI.users()).thenReturn(usersEntity);
    when(auth0UserMapper.toDto(any(User.class))).thenReturn(testAuth0UserDto);

    // When
    Auth0UserDto result = auth0InvitationService.createUserWithInvitation(testInvitationDto);

    // Then
    assertNotNull(result);
    assertEquals(testAuth0UserDto.getUserId(), result.getUserId());
    assertEquals(testAuth0UserDto.getEmail(), result.getEmail());
    assertEquals(testAuth0UserDto.getName(), result.getName());
    
    verify(managementAPI).users();
    verify(auth0UserMapper).toDto(any(User.class));
  }

  @Test
  @DisplayName("Should throw Auth0Exception when user creation fails")
  void shouldThrowAuth0ExceptionWhenUserCreationFails() throws Exception {
    // Given
    when(managementAPI.users()).thenReturn(usersEntity);
    when(usersEntity.create(any(User.class))).thenThrow(new RuntimeException("User creation failed"));

    // When & Then
    Auth0Exception exception = assertThrows(Auth0Exception.class,
        () -> auth0InvitationService.createUserWithInvitation(testInvitationDto));
    
    assertEquals("Failed to create user invitation: User creation failed", exception.getMessage());
    assertEquals("AUTH0_INVITATION_FAILED", exception.getErrorCode());
    assertEquals(400, exception.getHttpStatus());
    
    verify(managementAPI).users();
  }

  @Test
  @DisplayName("Should check user existence by email correctly")
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
} 