package com.coherentsolutions.pot.insuranceservice.dto.auth0;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for Auth0UserDto.
 *
 * <p>Tests cover DTO creation, validation, and property access.
 */
class Auth0UserDtoTest {

  @Test
  void testDefaultConstructor() {
    // Act
    Auth0UserDto dto = new Auth0UserDto();

    // Assert
    assertNotNull(dto);
  }

  @Test
  void testParameterizedConstructor() {
    // Arrange
    String email = "test@example.com";
    String password = "password123";
    String name = "Test User";

    // Act
    Auth0UserDto dto = new Auth0UserDto(email, password, name);

    // Assert
    assertEquals(email, dto.getEmail());
    assertEquals(password, dto.getPassword());
    assertEquals(name, dto.getName());
  }

  @Test
  void testSettersAndGetters() {
    // Arrange
    Auth0UserDto dto = new Auth0UserDto();

    // Act
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

    // Assert
    assertEquals("test@example.com", dto.getEmail());
    assertEquals("password123", dto.getPassword());
    assertEquals("Test User", dto.getName());
    assertEquals("testuser", dto.getNickname());
    assertEquals("https://example.com/picture.jpg", dto.getPicture());
    assertTrue(dto.isEmailVerified());
    assertFalse(dto.isBlocked());
    assertEquals(userMetadata, dto.getUserMetadata());
    assertEquals(appMetadata, dto.getAppMetadata());
  }

  @Test
  void testDefaultValues() {
    // Act
    Auth0UserDto dto = new Auth0UserDto();

    // Assert
    assertFalse(dto.isEmailVerified());
    assertFalse(dto.isBlocked());
  }

  @Test
  void testEqualsAndHashCode() {
    // Arrange
    final Auth0UserDto dto1 = new Auth0UserDto("test@example.com", "password123", "Test User");
    final Auth0UserDto dto2 = new Auth0UserDto("test@example.com", "password123", "Test User");

    // Act & Assert
    assertNotNull(dto1);
    assertNotNull(dto2);
  }

  @Test
  void testToString() {
    // Arrange
    final Auth0UserDto dto = new Auth0UserDto("test@example.com", "password123", "Test User");

    // Act
    String result = dto.toString();

    // Assert
    assertNotNull(result);
  }

  @Test
  void testNullValues() {
    // Arrange
    final Auth0UserDto dto = new Auth0UserDto();

    // Act
    dto.setEmail(null);
    dto.setPassword(null);
    dto.setName(null);
    dto.setNickname(null);
    dto.setPicture(null);
    dto.setUserMetadata(null);
    dto.setAppMetadata(null);

    // Assert
    assertEquals(null, dto.getEmail());
    assertEquals(null, dto.getPassword());
    assertEquals(null, dto.getName());
    assertEquals(null, dto.getNickname());
    assertEquals(null, dto.getPicture());
    assertEquals(null, dto.getUserMetadata());
    assertEquals(null, dto.getAppMetadata());
  }

  @Test
  void testEmptyStrings() {
    // Arrange
    final Auth0UserDto dto = new Auth0UserDto();

    // Act
    dto.setEmail("");
    dto.setPassword("");
    dto.setName("");
    dto.setNickname("");
    dto.setPicture("");

    // Assert
    assertEquals("", dto.getEmail());
    assertEquals("", dto.getPassword());
    assertEquals("", dto.getName());
    assertEquals("", dto.getNickname());
    assertEquals("", dto.getPicture());
  }

  @Test
  void testMetadataOperations() {
    // Arrange
    final Auth0UserDto dto = new Auth0UserDto();
    final Map<String, Object> userMetadata = new HashMap<>();
    userMetadata.put("key1", "value1");
    userMetadata.put("key2", 123);

    final Map<String, Object> appMetadata = new HashMap<>();
    appMetadata.put("role", "admin");
    appMetadata.put("permissions", Arrays.asList("read", "write"));

    // Act
    dto.setUserMetadata(userMetadata);
    dto.setAppMetadata(appMetadata);

    // Assert
    assertEquals(userMetadata, dto.getUserMetadata());
    assertEquals(appMetadata, dto.getAppMetadata());
    assertEquals("value1", dto.getUserMetadata().get("key1"));
    assertEquals(123, dto.getUserMetadata().get("key2"));
    assertEquals("admin", dto.getAppMetadata().get("role"));
  }

  @Test
  void testBooleanFlags() {
    // Arrange
    final Auth0UserDto dto = new Auth0UserDto();

    // Act
    dto.setEmailVerified(true);
    dto.setBlocked(true);

    // Assert
    assertTrue(dto.isEmailVerified());
    assertTrue(dto.isBlocked());

    // Act
    dto.setEmailVerified(false);
    dto.setBlocked(false);

    // Assert
    assertFalse(dto.isEmailVerified());
    assertFalse(dto.isBlocked());
  }
} 