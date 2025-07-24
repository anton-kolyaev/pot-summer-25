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
 * <p>Tests cover validation, serialization, and data handling.
 */
class Auth0UserDtoTest {

  @Test
  void testConstructorWithValidDataCreatesDto() {
    // Arrange & Act
    Auth0UserDto dto = new Auth0UserDto("test@example.com", "password123", "Test User");

    // Assert
    assertNotNull(dto);
    assertEquals("test@example.com", dto.getEmail());
    assertEquals("password123", dto.getPassword());
    assertEquals("Test User", dto.getName());
  }

  @Test
  void testDefaultConstructorCreatesEmptyDto() {
    // Arrange & Act
    Auth0UserDto dto = new Auth0UserDto();

    // Assert
    assertNotNull(dto);
    assertEquals(false, dto.isEmailVerified());
    assertEquals(false, dto.isBlocked());
  }

  @Test
  void testSettersAndGettersWorkCorrectly() {
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
    userMetadata.put("key1", "value1");
    dto.setUserMetadata(userMetadata);

    Map<String, Object> appMetadata = new HashMap<>();
    appMetadata.put("key2", "value2");
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
  void testEmailVerifiedDefaultValueIsFalse() {
    // Arrange & Act
    Auth0UserDto dto = new Auth0UserDto();

    // Assert
    assertFalse(dto.isEmailVerified());
  }

  @Test
  void testBlockedDefaultValueIsFalse() {
    // Arrange & Act
    Auth0UserDto dto = new Auth0UserDto();

    // Assert
    assertFalse(dto.isBlocked());
  }

  @Test
  void testUserMetadataCanBeSetAndRetrieved() {
    // Arrange
    Auth0UserDto dto = new Auth0UserDto();
    Map<String, Object> metadata = new HashMap<>();
    metadata.put("department", "Engineering");
    metadata.put("role", "Developer");

    // Act
    dto.setUserMetadata(metadata);

    // Assert
    assertEquals(metadata, dto.getUserMetadata());
    assertEquals("Engineering", dto.getUserMetadata().get("department"));
    assertEquals("Developer", dto.getUserMetadata().get("role"));
  }

  @Test
  void testAppMetadataCanBeSetAndRetrieved() {
    // Arrange
    Auth0UserDto dto = new Auth0UserDto();
    Map<String, Object> metadata = new HashMap<>();
    metadata.put("permissions", Arrays.asList("read", "write"));
    metadata.put("level", 5);

    // Act
    dto.setAppMetadata(metadata);

    // Assert
    assertEquals(metadata, dto.getAppMetadata());
    assertEquals(Arrays.asList("read", "write"), dto.getAppMetadata().get("permissions"));
    assertEquals(5, dto.getAppMetadata().get("level"));
  }
} 