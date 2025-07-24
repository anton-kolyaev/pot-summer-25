package com.coherentsolutions.pot.insuranceservice.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.auth0.json.mgmt.users.User;
import com.coherentsolutions.pot.insuranceservice.dto.auth0.Auth0UserDto;
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
  void toAuth0User_WithValidDto_ReturnsUser() {
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
  void toAuth0User_WithNullPassword_ReturnsUserWithNullPassword() {
    // Arrange
    Auth0UserDto dto = new Auth0UserDto();
    dto.setEmail("test@example.com");
    dto.setPassword(null);
    dto.setName("Test User");

    // Act
    User result = auth0UserMapper.toAuth0User(dto);

    // Assert
    assertNotNull(result);
    assertEquals("test@example.com", result.getEmail());
    assertEquals("Test User", result.getName());
  }

  @Test
  void toDto_WithValidUser_ReturnsDto() {
    // Arrange
    User user = new User();
    user.setId("auth0|123");
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
  }

  @Test
  void toDto_WithNullUser_ReturnsNull() {
    // Arrange
    User user = null;

    // Act
    Auth0UserDto result = auth0UserMapper.toDto(user);

    // Assert
    assertNull(result);
  }

  @Test
  void toAuth0User_WithEmptyDto_ReturnsUserWithDefaults() {
    // Arrange
    Auth0UserDto dto = new Auth0UserDto();

    // Act
    User result = auth0UserMapper.toAuth0User(dto);

    // Assert
    assertNotNull(result);
  }

  @Test
  void toDto_WithEmptyUser_ReturnsDtoWithDefaults() {
    // Arrange
    User user = new User();

    // Act
    Auth0UserDto result = auth0UserMapper.toDto(user);

    // Assert
    assertNotNull(result);
  }

  @Test
  void toAuth0User_WithComplexMetadata_HandlesCorrectly() {
    // Arrange
    Auth0UserDto dto = new Auth0UserDto();
    dto.setEmail("test@example.com");

    Map<String, Object> userMetadata = new HashMap<>();
    userMetadata.put("string", "value");
    userMetadata.put("number", 42);
    userMetadata.put("boolean", true);
    dto.setUserMetadata(userMetadata);

    Map<String, Object> appMetadata = new HashMap<>();
    appMetadata.put("nested", Map.of("key", "value"));
    dto.setAppMetadata(appMetadata);

    // Act
    User result = auth0UserMapper.toAuth0User(dto);

    // Assert
    assertNotNull(result);
    assertEquals(userMetadata, result.getUserMetadata());
    assertEquals(appMetadata, result.getAppMetadata());
  }
} 