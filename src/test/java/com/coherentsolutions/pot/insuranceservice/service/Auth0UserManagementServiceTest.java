package com.coherentsolutions.pot.insuranceservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.auth0.client.mgmt.ManagementAPI;
import com.auth0.exception.Auth0Exception;
import com.auth0.json.mgmt.users.User;
import com.coherentsolutions.pot.insuranceservice.dto.auth0.Auth0UserDto;
import com.coherentsolutions.pot.insuranceservice.mapper.Auth0UserMapper;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for Auth0UserManagementService.
 *
 * <p>Tests cover all CRUD operations and error handling scenarios.
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
  void createUser_WithValidUser_ReturnsCreatedUser() throws Auth0Exception {
    // Arrange
    User inputUser = new User();
    inputUser.setEmail("test@example.com");
    inputUser.setName("Test User");

    User createdUser = new User();
    createdUser.setId("auth0|123");
    createdUser.setEmail("test@example.com");
    createdUser.setName("Test User");

    // Act
    User result = auth0UserManagementService.createUser(inputUser);

    // Assert
    assertNotNull(result);
    assertEquals("auth0|123", result.getId());
    assertEquals("test@example.com", result.getEmail());
    assertEquals("Test User", result.getName());
  }

  @Test
  void createUser_WithValidDto_ReturnsCreatedUserDto() throws Auth0Exception {
    // Arrange
    Auth0UserDto inputDto = new Auth0UserDto("test@example.com", "password123", "Test User");
    
    User auth0User = new User();
    auth0User.setEmail("test@example.com");
    auth0User.setName("Test User");

    User createdUser = new User();
    createdUser.setId("auth0|123");
    createdUser.setEmail("test@example.com");
    createdUser.setName("Test User");

    Auth0UserDto expectedDto = new Auth0UserDto("test@example.com", null, "Test User");

    when(auth0UserMapper.toAuth0User(inputDto)).thenReturn(auth0User);
    when(auth0UserMapper.toDto(createdUser)).thenReturn(expectedDto);

    // Act
    Auth0UserDto result = auth0UserManagementService.createUser(inputDto);

    // Assert
    assertNotNull(result);
    assertEquals("test@example.com", result.getEmail());
    assertEquals("Test User", result.getName());
  }

  @Test
  void getUserById_WithValidId_ReturnsUser() throws Auth0Exception {
    // Arrange
    String userId = "auth0|123";
    User expectedUser = new User();
    expectedUser.setId(userId);
    expectedUser.setEmail("test@example.com");
    expectedUser.setName("Test User");

    // Act
    User result = auth0UserManagementService.getUserById(userId);

    // Assert
    assertNotNull(result);
    assertEquals(userId, result.getId());
    assertEquals("test@example.com", result.getEmail());
  }

  @Test
  void getUserDtoById_WithValidId_ReturnsUserDto() throws Auth0Exception {
    // Arrange
    String userId = "auth0|123";
    User auth0User = new User();
    auth0User.setId(userId);
    auth0User.setEmail("test@example.com");
    auth0User.setName("Test User");

    Auth0UserDto expectedDto = new Auth0UserDto("test@example.com", null, "Test User");

    when(auth0UserMapper.toDto(auth0User)).thenReturn(expectedDto);

    // Act
    Auth0UserDto result = auth0UserManagementService.getUserDtoById(userId);

    // Assert
    assertNotNull(result);
    assertEquals("test@example.com", result.getEmail());
    assertEquals("Test User", result.getName());
  }

  @Test
  void updateUser_WithValidData_ReturnsUpdatedUser() throws Auth0Exception {
    // Arrange
    String userId = "auth0|123";
    User inputUser = new User();
    inputUser.setName("Updated Name");

    User updatedUser = new User();
    updatedUser.setId(userId);
    updatedUser.setEmail("test@example.com");
    updatedUser.setName("Updated Name");

    // Act
    User result = auth0UserManagementService.updateUser(userId, inputUser);

    // Assert
    assertNotNull(result);
    assertEquals(userId, result.getId());
    assertEquals("Updated Name", result.getName());
  }

  @Test
  void updateUser_WithValidDto_ReturnsUpdatedUserDto() throws Auth0Exception {
    // Arrange
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

    when(auth0UserMapper.toDto(updatedUser)).thenReturn(expectedDto);

    // Act
    Auth0UserDto result = auth0UserManagementService.updateUser(userId, inputDto);

    // Assert
    assertNotNull(result);
    assertEquals("test@example.com", result.getEmail());
    assertEquals("Updated Name", result.getName());
  }

  @Test
  void deleteUser_WithValidId_ExecutesSuccessfully() throws Auth0Exception {
    // Arrange
    String userId = "auth0|123";

    // Act & Assert - should not throw exception
    auth0UserManagementService.deleteUser(userId);
  }

  @Test
  void getUsersByEmail_WithValidEmail_ReturnsMatchingUsers() throws Auth0Exception {
    // Arrange
    String email = "test@example.com";
    User user = new User();
    user.setId("auth0|123");
    user.setEmail(email);

    // Act
    List<User> result = auth0UserManagementService.getUsersByEmail(email);

    // Assert
    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(email, result.get(0).getEmail());
  }

  @Test
  void getUserDtosByEmail_WithValidEmail_ReturnsMatchingUserDtos() throws Auth0Exception {
    // Arrange
    String email = "test@example.com";
    User user = new User();
    user.setId("auth0|123");
    user.setEmail(email);

    Auth0UserDto expectedDto = new Auth0UserDto(email, null, "Test User");

    when(auth0UserMapper.toDto(user)).thenReturn(expectedDto);

    // Act
    List<Auth0UserDto> result = auth0UserManagementService.getUserDtosByEmail(email);

    // Assert
    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(email, result.get(0).getEmail());
  }

  @Test
  void createUser_WhenAuth0Exception_ThrowsException() throws Auth0Exception {
    // Arrange
    User inputUser = new User();
    inputUser.setEmail("test@example.com");

    // Act & Assert
    assertThrows(Auth0Exception.class, () -> auth0UserManagementService.createUser(inputUser));
  }

  @Test
  void updateUser_WhenUserNotFound_ThrowsException() throws Auth0Exception {
    // Arrange
    String userId = "auth0|123";
    Auth0UserDto inputDto = new Auth0UserDto("test@example.com", "password", "Test User");

    // Act & Assert
    assertThrows(Auth0Exception.class, () -> auth0UserManagementService.updateUser(userId, inputDto));
  }
} 