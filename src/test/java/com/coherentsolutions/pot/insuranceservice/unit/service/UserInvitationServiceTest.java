package com.coherentsolutions.pot.insuranceservice.unit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.auth0.exception.Auth0Exception;
import com.coherentsolutions.pot.insuranceservice.dto.auth0.Auth0UserDto;
import com.coherentsolutions.pot.insuranceservice.dto.user.UserDto;
import com.coherentsolutions.pot.insuranceservice.enums.UserFunction;
import com.coherentsolutions.pot.insuranceservice.enums.UserStatus;
import com.coherentsolutions.pot.insuranceservice.mapper.UserMapper;
import com.coherentsolutions.pot.insuranceservice.model.User;
import com.coherentsolutions.pot.insuranceservice.model.UserFunctionAssignment;
import com.coherentsolutions.pot.insuranceservice.repository.UserRepository;
import com.coherentsolutions.pot.insuranceservice.service.Auth0UserManagementService;
import com.coherentsolutions.pot.insuranceservice.service.UserInvitationService;
import java.time.LocalDate;
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
@DisplayName("User Invitation Service Tests")
public class UserInvitationServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private UserMapper userMapper;

  @Mock
  private Auth0UserManagementService auth0UserManagementService;

  @InjectMocks
  private UserInvitationService userInvitationService;

  private User user;
  private UserDto userDto;
  private UUID userId;

  @BeforeEach
  void setUp() {
    userId = UUID.randomUUID();
    
    user = new User();
    user.setId(userId);
    user.setFirstName("John");
    user.setLastName("Doe");
    user.setEmail("john.doe@example.com");
    user.setStatus(UserStatus.PENDING);

    userDto = UserDto.builder()
        .firstName("John")
        .lastName("Doe")
        .email("john.doe@example.com")
        .dateOfBirth(LocalDate.of(1990, 1, 1))
        .ssn("123-45-6789")
        .functions(Set.of(UserFunction.COMPANY_MANAGER))
        .build();
  }

  @Test
  @DisplayName("Should invite user successfully")
  void shouldInviteUserSuccessfully() throws Auth0Exception {
    // Given
    when(userMapper.toEntity(userDto)).thenReturn(user);
    when(userRepository.save(user)).thenReturn(user);
    when(userMapper.toDto(user)).thenReturn(userDto);
    when(auth0UserManagementService.createUserWithInvitation(any(Auth0UserDto.class)))
        .thenReturn(null); // Auth0 returns null for successful creation

    // When
    UserDto result = userInvitationService.inviteUser(userDto);

    // Then
    assertNotNull(result);
    assertEquals("john.doe@example.com", result.getEmail());
    assertEquals("John", result.getFirstName());
    assertEquals("Doe", result.getLastName());
    
    verify(userRepository).save(user);
    verify(auth0UserManagementService).createUserWithInvitation(any(Auth0UserDto.class));
    verify(userMapper).toDto(user);
  }

  @Test
  @DisplayName("Should rollback local user creation when Auth0 fails")
  void shouldRollbackWhenAuth0Fails() throws Auth0Exception {
    // Given
    when(userMapper.toEntity(userDto)).thenReturn(user);
    when(userRepository.save(user)).thenReturn(user);
    when(auth0UserManagementService.createUserWithInvitation(any(Auth0UserDto.class)))
        .thenThrow(new Auth0Exception("Auth0 error"));

    // When / Then
    assertThrows(Auth0Exception.class, () -> userInvitationService.inviteUser(userDto));
    
    verify(userRepository).save(user);
    verify(userRepository).delete(user);
    verify(auth0UserManagementService).createUserWithInvitation(any(Auth0UserDto.class));
  }

  @Test
  @DisplayName("Should activate user successfully")
  void shouldActivateUserSuccessfully() {
    // Given
    User pendingUser = new User();
    pendingUser.setId(userId);
    pendingUser.setStatus(UserStatus.PENDING);

    UserDto expectedDto = UserDto.builder()
        .id(userId)
        .status(UserStatus.ACTIVE)
        .build();

    when(userRepository.findByIdOrThrow(userId)).thenReturn(pendingUser);
    when(userRepository.save(pendingUser)).thenReturn(pendingUser);
    when(userMapper.toDto(pendingUser)).thenReturn(expectedDto);

    // When
    UserDto result = userInvitationService.activateUser(userId);

    // Then
    assertNotNull(result);
    assertEquals(UserStatus.ACTIVE, result.getStatus());
    verify(userRepository).save(pendingUser);
    verify(userMapper).toDto(pendingUser);
  }

  @Test
  @DisplayName("Should throw exception when activating non-pending user")
  void shouldThrowExceptionWhenActivatingNonPendingUser() {
    // Given
    User activeUser = new User();
    activeUser.setId(userId);
    activeUser.setStatus(UserStatus.ACTIVE);

    when(userRepository.findByIdOrThrow(userId)).thenReturn(activeUser);

    // When / Then
    assertThrows(IllegalStateException.class, () -> userInvitationService.activateUser(userId));
    verify(userRepository, times(0)).save(any(User.class));
  }

  @Test
  @DisplayName("Should check if user exists by email")
  void shouldCheckUserExistsByEmail() {
    // Given
    String email = "test@example.com";
    when(userRepository.existsByEmail(email)).thenReturn(true);

    // When
    boolean exists = userInvitationService.userExistsByEmail(email);

    // Then
    assertEquals(true, exists);
    verify(userRepository).existsByEmail(email);
  }

  @Test
  @DisplayName("Should get user by email")
  void shouldGetUserByEmail() {
    // Given
    String email = "test@example.com";
    when(userRepository.findByEmail(email)).thenReturn(java.util.Optional.of(user));
    when(userMapper.toDto(user)).thenReturn(userDto);

    // When
    UserDto result = userInvitationService.getUserByEmail(email);

    // Then
    assertNotNull(result);
    assertEquals(email, result.getEmail());
    verify(userRepository).findByEmail(email);
    verify(userMapper).toDto(user);
  }
} 