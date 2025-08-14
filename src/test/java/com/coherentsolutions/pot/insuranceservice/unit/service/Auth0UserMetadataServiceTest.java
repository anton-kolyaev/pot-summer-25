package com.coherentsolutions.pot.insuranceservice.unit.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.auth0.client.mgmt.ManagementAPI;
import com.auth0.client.mgmt.UsersEntity;
import com.auth0.exception.Auth0Exception;
import com.auth0.json.mgmt.users.User;
import com.auth0.net.Request;
import com.auth0.net.Response;
import com.coherentsolutions.pot.insuranceservice.dto.user.UserDto;
import com.coherentsolutions.pot.insuranceservice.enums.UserFunction;
import com.coherentsolutions.pot.insuranceservice.service.Auth0UserMetadataService;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class Auth0UserMetadataServiceTest {

  @Mock
  private ManagementAPI managementAPI;

  @Mock
  private UsersEntity usersEntity;

  @Mock
  private Request<User> request;

  @Mock
  private Response<User> response;

  private Auth0UserMetadataService auth0UserMetadataService;

  @BeforeEach
  void setUp() {
    auth0UserMetadataService = new Auth0UserMetadataService(managementAPI);
  }

  @Test
  @DisplayName("Should update user metadata successfully")
  void shouldUpdateUserMetadataSuccessfully() throws Exception {
    // Given
    String auth0UserId = "auth0|123";
    final UserDto userDto = buildUserDto();
    User mockUpdatedUser = new User();
    mockUpdatedUser.setId(auth0UserId);

    when(managementAPI.users()).thenReturn(usersEntity);
    when(usersEntity.update(eq(auth0UserId), any(User.class))).thenReturn(request);
    when(request.execute()).thenReturn(response);
    when(response.getBody()).thenReturn(mockUpdatedUser);

    // When
    assertDoesNotThrow(() -> auth0UserMetadataService.updateUserMetadata(auth0UserId, userDto));

    // Then
    verify(managementAPI).users();
    verify(usersEntity).update(eq(auth0UserId), argThat(user -> {
      assertNotNull(user.getUserMetadata());
      assertEquals("John", user.getUserMetadata().get("firstName"));
      assertEquals("Doe", user.getUserMetadata().get("lastName"));
      assertEquals("johndoe", user.getUserMetadata().get("username"));
      assertEquals("1990-01-01", user.getUserMetadata().get("dateOfBirth"));
      assertEquals("123-45-6789", user.getUserMetadata().get("ssn"));
      assertEquals(userDto.getCompanyId().toString(), user.getUserMetadata().get("companyId"));
      assertEquals(Set.of(UserFunction.COMPANY_MANAGER), user.getUserMetadata().get("functions"));
      return true;
    }));
  }

  @Test
  @DisplayName("Should handle null Auth0 user ID gracefully")
  void shouldHandleNullAuth0UserIdGracefully() {
    // Given
    UserDto userDto = buildUserDto();

    // When & Then
    assertDoesNotThrow(() -> auth0UserMetadataService.updateUserMetadata(null, userDto));
    verifyNoInteractions(managementAPI);
  }

  @Test
  @DisplayName("Should handle empty Auth0 user ID gracefully")
  void shouldHandleEmptyAuth0UserIdGracefully() {
    // Given
    UserDto userDto = buildUserDto();

    // When & Then
    assertDoesNotThrow(() -> auth0UserMetadataService.updateUserMetadata("", userDto));
    verifyNoInteractions(managementAPI);
  }

  @Test
  @DisplayName("Should throw custom exception when Auth0 update fails")
  void shouldThrowCustomExceptionWhenAuth0UpdateFails() throws Exception {
    // Given
    String auth0UserId = "auth0|123";
    UserDto userDto = buildUserDto();
    String errorMessage = "Auth0 API error";

    when(managementAPI.users()).thenReturn(usersEntity);
    when(usersEntity.update(eq(auth0UserId), any(User.class))).thenReturn(request);
    when(request.execute()).thenThrow(new Auth0Exception(errorMessage));

    // When & Then
    com.coherentsolutions.pot.insuranceservice.exception.Auth0Exception exception = assertThrows(
        com.coherentsolutions.pot.insuranceservice.exception.Auth0Exception.class,
        () -> auth0UserMetadataService.updateUserMetadata(auth0UserId, userDto)
    );

    assertEquals("Failed to update Auth0 user metadata: " + errorMessage, exception.getMessage());
    assertEquals("AUTH0_METADATA_UPDATE_FAILED", exception.getErrorCode());
    assertEquals(400, exception.getHttpStatus());
  }

  private UserDto buildUserDto() {
    return UserDto.builder()
        .id(UUID.randomUUID())
        .firstName("John")
        .lastName("Doe")
        .username("johndoe")
        .email("john.doe@example.com")
        .dateOfBirth(LocalDate.of(1990, 1, 1))
        .ssn("123-45-6789")
        .companyId(UUID.randomUUID())
        .functions(Set.of(UserFunction.COMPANY_MANAGER))
        .build();
  }
}
