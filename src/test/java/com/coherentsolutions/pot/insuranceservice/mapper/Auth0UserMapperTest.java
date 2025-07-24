package com.coherentsolutions.pot.insuranceservice.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.auth0.json.mgmt.users.User;
import com.coherentsolutions.pot.insuranceservice.dto.auth0.Auth0UserDto;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for Auth0UserMapper.
 *
 * <p>Tests cover mapping between Auth0UserDto and Auth0 User objects.
 */
class Auth0UserMapperTest {

  private Auth0UserMapper auth0UserMapper;

  @BeforeEach
  void setUp() {
    auth0UserMapper = new Auth0UserMapperImpl();
  }

  @Test
  void testToAuth0UserWithValidDtoReturnsUser() {
    // Arrange
    Auth0UserDto dto = new Auth0UserDto();
    dto.setEmail("test@example.com");
    dto.setPassword("password123");
    dto.setName("Test User");
    dto.setNickname("testuser");
    dto.setPicture("https://example.com/picture.jpg");
    dto.setEmailVerified(true);
    dto.setBlocked(false);

    Map<String, Object> userMetadata = new HashMap<>();
    userMetadata.put("department", "Engineering");
    dto.setUserMetadata(userMetadata);

    Map<String, Object> appMetadata = new HashMap<>();
    appMetadata.put("role", "Developer");
    dto.setAppMetadata(appMetadata);

    // Act
    User result = auth0UserMapper.toAuth0User(dto);

    // Assert
    assertNotNull(result);
    assertEquals("test@example.com", result.getEmail());
    assertEquals("Test User", result.getName());
    assertEquals("testuser", result.getNickname());
    assertEquals("https://example.com/picture.jpg", result.getPicture());
    assertEquals(userMetadata, result.getUserMetadata());
    assertEquals(appMetadata, result.getAppMetadata());
  }

  @Test
  void testToAuth0UserWithNullPassword() {
    // Arrange
    Auth0UserDto dto = new Auth0UserDto();
    dto.setEmail("test@example.com");
    dto.setName("Test User");
    dto.setPassword(null);

    // Act
    User result = auth0UserMapper.toAuth0User(dto);

    // Assert
    assertNotNull(result);
    assertEquals("test@example.com", result.getEmail());
    assertEquals("Test User", result.getName());
  }

  @Test
  void testToAuth0UserWithEmptyPassword() {
    // Arrange
    Auth0UserDto dto = new Auth0UserDto();
    dto.setEmail("test@example.com");
    dto.setName("Test User");
    dto.setPassword("");

    // Act
    User result = auth0UserMapper.toAuth0User(dto);

    // Assert
    assertNotNull(result);
    assertEquals("test@example.com", result.getEmail());
    assertEquals("Test User", result.getName());
  }

  @Test
  void testToDtoWithValidUserReturnsDto() {
    // Arrange
    User user = new User();
    user.setEmail("test@example.com");
    user.setName("Test User");
    user.setNickname("testuser");
    user.setPicture("https://example.com/picture.jpg");

    Map<String, Object> userMetadata = new HashMap<>();
    userMetadata.put("department", "Engineering");
    user.setUserMetadata(userMetadata);

    Map<String, Object> appMetadata = new HashMap<>();
    appMetadata.put("role", "Developer");
    user.setAppMetadata(appMetadata);

    // Act
    Auth0UserDto result = auth0UserMapper.toDto(user);

    // Assert
    assertNotNull(result);
    assertEquals("test@example.com", result.getEmail());
    assertEquals("Test User", result.getName());
    assertEquals("testuser", result.getNickname());
    assertEquals("https://example.com/picture.jpg", result.getPicture());
    assertEquals(userMetadata, result.getUserMetadata());
    assertEquals(appMetadata, result.getAppMetadata());
    // Password should be ignored when mapping from Auth0 User to DTO
    assertNull(result.getPassword());
  }

  @Test
  void testToDtoWithNullUserReturnsNull() {
    // Act
    Auth0UserDto result = auth0UserMapper.toDto(null);

    // Assert
    assertNull(result);
  }

  @Test
  void testUpdateUserFromDto() {
    // Arrange
    User existingUser = new User();
    existingUser.setEmail("old@example.com");
    existingUser.setName("Old User");

    Auth0UserDto dto = new Auth0UserDto();
    dto.setEmail("new@example.com");
    dto.setName("New User");
    dto.setPassword("newpassword123");
    dto.setEmailVerified(true);

    // Act
    auth0UserMapper.updateUserFromDto(dto, existingUser);

    // Assert
    assertEquals("new@example.com", existingUser.getEmail());
    assertEquals("New User", existingUser.getName());
  }

  @Test
  void testUpdateUserFromDtoWithNullPassword() {
    // Arrange
    User existingUser = new User();
    existingUser.setEmail("old@example.com");
    existingUser.setName("Old User");

    Auth0UserDto dto = new Auth0UserDto();
    dto.setEmail("new@example.com");
    dto.setName("New User");
    dto.setPassword(null);

    // Act
    auth0UserMapper.updateUserFromDto(dto, existingUser);

    // Assert
    assertEquals("new@example.com", existingUser.getEmail());
    assertEquals("New User", existingUser.getName());
  }

  @Test
  void testMapperWithNullMetadata() {
    // Arrange
    Auth0UserDto dto = new Auth0UserDto();
    dto.setEmail("test@example.com");
    dto.setName("Test User");
    dto.setUserMetadata(null);
    dto.setAppMetadata(null);

    // Act
    User result = auth0UserMapper.toAuth0User(dto);

    // Assert
    assertNotNull(result);
    assertEquals("test@example.com", result.getEmail());
    assertEquals("Test User", result.getName());
    assertNull(result.getUserMetadata());
    assertNull(result.getAppMetadata());
  }

  @Test
  void testMapperWithEmptyMetadata() {
    // Arrange
    Auth0UserDto dto = new Auth0UserDto();
    dto.setEmail("test@example.com");
    dto.setName("Test User");
    dto.setUserMetadata(new HashMap<>());
    dto.setAppMetadata(new HashMap<>());

    // Act
    User result = auth0UserMapper.toAuth0User(dto);

    // Assert
    assertNotNull(result);
    assertEquals("test@example.com", result.getEmail());
    assertEquals("Test User", result.getName());
    assertNotNull(result.getUserMetadata());
    assertNotNull(result.getAppMetadata());
    assertTrue(result.getUserMetadata().isEmpty());
    assertTrue(result.getAppMetadata().isEmpty());
  }

  @Test
  void testMapperWithComplexMetadata() {
    // Arrange
    Auth0UserDto dto = new Auth0UserDto();
    dto.setEmail("test@example.com");
    dto.setName("Test User");

    Map<String, Object> userMetadata = new HashMap<>();
    userMetadata.put("string", "value");
    userMetadata.put("number", 123);
    userMetadata.put("boolean", true);
    userMetadata.put("array", Arrays.asList("item1", "item2"));
    dto.setUserMetadata(userMetadata);

    Map<String, Object> appMetadata = new HashMap<>();
    appMetadata.put("role", "admin");
    appMetadata.put("permissions", Arrays.asList("read", "write", "delete"));
    dto.setAppMetadata(appMetadata);

    // Act
    User result = auth0UserMapper.toAuth0User(dto);

    // Assert
    assertNotNull(result);
    assertEquals(userMetadata, result.getUserMetadata());
    assertEquals(appMetadata, result.getAppMetadata());
  }

  @Test
  void testMapperHandlesOptionalFields() {
    // Arrange
    Auth0UserDto dto = new Auth0UserDto();
    dto.setEmail("test@example.com");
    dto.setName("Test User");
    dto.setNickname("testuser");
    dto.setPicture("https://example.com/picture.jpg");

    // Act
    User result = auth0UserMapper.toAuth0User(dto);

    // Assert
    assertNotNull(result);
    assertEquals("test@example.com", result.getEmail());
    assertEquals("Test User", result.getName());
    assertEquals("testuser", result.getNickname());
    assertEquals("https://example.com/picture.jpg", result.getPicture());
  }
} 