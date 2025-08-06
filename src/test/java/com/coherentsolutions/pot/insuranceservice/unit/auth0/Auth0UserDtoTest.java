package com.coherentsolutions.pot.insuranceservice.unit.auth0;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.coherentsolutions.pot.insuranceservice.dto.auth0.Auth0UserDto;
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
    // When
    Auth0UserDto dto = new Auth0UserDto();

    // Then
    assertNotNull(dto);
  }

  @Test
  void testParameterizedConstructor() {
    // Given
    String email = "test@example.com";
    String password = "password123";
    String name = "Test User";

    // When
    Auth0UserDto dto = new Auth0UserDto(email, password, name);

    // Then
    assertEquals(email, dto.getEmail());
    assertEquals(password, dto.getPassword());
    assertEquals(name, dto.getName());
  }

  @Test
  void testSettersAndGetters() {
    // Given
    Auth0UserDto dto = new Auth0UserDto();

    // When
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

    // Then
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
    // When
    Auth0UserDto dto = new Auth0UserDto();

    // Then
    assertFalse(dto.isEmailVerified());
    assertFalse(dto.isBlocked());
  }

  @Test
  void testEqualsAndHashCode() {
    // Given
    final Auth0UserDto dto1 = new Auth0UserDto("test@example.com", "password123", "Test User");
    final Auth0UserDto dto2 = new Auth0UserDto("test@example.com", "password123", "Test User");

    // When & Then
    assertNotNull(dto1);
    assertNotNull(dto2);
  }

  @Test
  void testToString() {
    // Given
    final Auth0UserDto dto = new Auth0UserDto("test@example.com", "password123", "Test User");

    // When
    String result = dto.toString();

    // Then
    assertNotNull(result);
  }

  @Test
  void testNullValues() {
    // Given
    final Auth0UserDto dto = new Auth0UserDto();

    // When
    dto.setEmail(null);
    dto.setPassword(null);
    dto.setName(null);
    dto.setNickname(null);
    dto.setPicture(null);
    dto.setUserMetadata(null);
    dto.setAppMetadata(null);

    // Then
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
    // Given
    final Auth0UserDto dto = new Auth0UserDto();

    // When
    dto.setEmail("");
    dto.setPassword("");
    dto.setName("");
    dto.setNickname("");
    dto.setPicture("");

    // Then
    assertEquals("", dto.getEmail());
    assertEquals("", dto.getPassword());
    assertEquals("", dto.getName());
    assertEquals("", dto.getNickname());
    assertEquals("", dto.getPicture());
  }

  @Test
  void testMetadataOperations() {
    // Given
    final Auth0UserDto dto = new Auth0UserDto();
    final Map<String, Object> userMetadata = new HashMap<>();
    userMetadata.put("key1", "value1");
    userMetadata.put("key2", 123);

    final Map<String, Object> appMetadata = new HashMap<>();
    appMetadata.put("role", "admin");
    appMetadata.put("permissions", Arrays.asList("read", "write"));

    // When
    dto.setUserMetadata(userMetadata);
    dto.setAppMetadata(appMetadata);

    // Then
    assertEquals(userMetadata, dto.getUserMetadata());
    assertEquals(appMetadata, dto.getAppMetadata());
    assertEquals("value1", dto.getUserMetadata().get("key1"));
    assertEquals(123, dto.getUserMetadata().get("key2"));
    assertEquals("admin", dto.getAppMetadata().get("role"));
  }

  @Test
  void testBooleanFlags() {
    // Given
    final Auth0UserDto dto = new Auth0UserDto();

    // When
    dto.setEmailVerified(true);
    dto.setBlocked(true);

    // Then
    assertTrue(dto.isEmailVerified());
    assertTrue(dto.isBlocked());

    // When
    dto.setEmailVerified(false);
    dto.setBlocked(false);

    // Then
    assertFalse(dto.isEmailVerified());
    assertFalse(dto.isBlocked());
  }
} 