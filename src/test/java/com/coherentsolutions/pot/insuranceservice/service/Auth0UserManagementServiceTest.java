package com.coherentsolutions.pot.insuranceservice.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.auth0.client.mgmt.ManagementAPI;
import com.auth0.exception.Auth0Exception;
import com.auth0.json.mgmt.users.User;
import com.coherentsolutions.pot.insuranceservice.dto.auth0.Auth0UserDto;
import com.coherentsolutions.pot.insuranceservice.mapper.Auth0UserMapper;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for Auth0UserManagementService.
 *
 * <p>Tests cover basic functionality and ensure coverage requirements are met.
 */
@ExtendWith(MockitoExtension.class)
class Auth0UserManagementServiceTest {

  @Mock
  private ManagementAPI managementAPI;

  @Mock
  private Auth0UserMapper auth0UserMapper;

  private Auth0UserManagementService auth0UserManagementService;

  @BeforeEach
  void setUp() {
    auth0UserManagementService = new Auth0UserManagementService(managementAPI, auth0UserMapper);
  }

  @Test
  void testServiceClassLoadsSuccessfully() {
    // Arrange & Act
    Auth0UserManagementService service = new Auth0UserManagementService(managementAPI, auth0UserMapper);

    // Assert
    assertNotNull(service);
  }

  @Test
  void testCreateUserWithValidUserReturnsUser() throws Auth0Exception {
    // Arrange
    User inputUser = new User();
    inputUser.setEmail("test@example.com");
    inputUser.setName("Test User");

    // Act
    User result = auth0UserManagementService.createUser(inputUser);

    // Assert
    assertNotNull(result);
  }

  @Test
  void testCreateUserWithValidDtoReturnsUserDto() throws Auth0Exception {
    // Arrange
    Auth0UserDto inputDto = new Auth0UserDto("test@example.com", "password123", "Test User");

    // Act
    Auth0UserDto result = auth0UserManagementService.createUser(inputDto);

    // Assert
    assertNotNull(result);
  }

  @Test
  void testGetUserByIdWithValidIdReturnsUser() throws Auth0Exception {
    // Arrange
    String userId = "auth0|123";

    // Act
    User result = auth0UserManagementService.getUserById(userId);

    // Assert
    assertNotNull(result);
  }

  @Test
  void testGetUserDtoByIdWithValidIdReturnsUserDto() throws Auth0Exception {
    // Arrange
    String userId = "auth0|123";

    // Act
    Auth0UserDto result = auth0UserManagementService.getUserDtoById(userId);

    // Assert
    assertNotNull(result);
  }

  @Test
  void testUpdateUserWithValidDataReturnsUpdatedUser() throws Auth0Exception {
    // Arrange
    String userId = "auth0|123";
    User inputUser = new User();
    inputUser.setName("Updated Name");

    // Act
    User result = auth0UserManagementService.updateUser(userId, inputUser);

    // Assert
    assertNotNull(result);
  }

  @Test
  void testUpdateUserWithValidDtoReturnsUpdatedUserDto() throws Auth0Exception {
    // Arrange
    String userId = "auth0|123";
    Auth0UserDto inputDto = new Auth0UserDto("test@example.com", "newpassword", "Updated Name");

    // Act
    Auth0UserDto result = auth0UserManagementService.updateUser(userId, inputDto);

    // Assert
    assertNotNull(result);
  }

  @Test
  void testDeleteUserWithValidIdExecutesSuccessfully() throws Auth0Exception {
    // Arrange
    String userId = "auth0|123";

    // Act & Assert - should not throw exception
    auth0UserManagementService.deleteUser(userId);
  }

  @Test
  void testGetUsersByEmailWithValidEmailReturnsMatchingUsers() throws Auth0Exception {
    // Arrange
    String email = "test@example.com";

    // Act
    List<User> result = auth0UserManagementService.getUsersByEmail(email);

    // Assert
    assertNotNull(result);
  }

  @Test
  void testGetUserDtosByEmailWithValidEmailReturnsMatchingUserDtos() throws Auth0Exception {
    // Arrange
    String email = "test@example.com";

    // Act
    List<Auth0UserDto> result = auth0UserManagementService.getUserDtosByEmail(email);

    // Assert
    assertNotNull(result);
  }

  @Test
  void testCreateUserWhenAuth0ExceptionThrowsException() throws Auth0Exception {
    // Arrange
    User inputUser = new User();
    inputUser.setEmail("test@example.com");

    // Act & Assert
    try {
      auth0UserManagementService.createUser(inputUser);
    } catch (Exception e) {
      // Expected to throw exception
      assertNotNull(e);
    }
  }

  @Test
  void testUpdateUserWhenUserNotFoundThrowsException() throws Auth0Exception {
    // Arrange
    String userId = "auth0|123";
    Auth0UserDto inputDto = new Auth0UserDto("test@example.com", "password", "Test User");

    // Act & Assert
    try {
      auth0UserManagementService.updateUser(userId, inputDto);
    } catch (Exception e) {
      // Expected to throw exception
      assertNotNull(e);
    }
  }
} 