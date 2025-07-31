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
    // Given
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

    // When
    User result = auth0UserMapper.toAuth0User(dto);

    // Then
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
    // Given
    Auth0UserDto dto = new Auth0UserDto();
    dto.setEmail("test@example.com");
    dto.setName("Test User");
    dto.setPassword(null);

    // When
    User result = auth0UserMapper.toAuth0User(dto);

    // Then
    assertNotNull(result);
    assertEquals("test@example.com", result.getEmail());
    assertEquals("Test User", result.getName());
  }

  @Test
  void testToAuth0UserWithEmptyPassword() {
    // Given
    Auth0UserDto dto = new Auth0UserDto();
    dto.setEmail("test@example.com");
    dto.setName("Test User");
    dto.setPassword("");

    // When
    User result = auth0UserMapper.toAuth0User(dto);

    // Then
    assertNotNull(result);
    assertEquals("test@example.com", result.getEmail());
    assertEquals("Test User", result.getName());
  }

  @Test
  void testToDtoWithValidUserReturnsDto() {
    // Given
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

    // When
    Auth0UserDto result = auth0UserMapper.toDto(user);

    // Then
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
    // When
    Auth0UserDto result = auth0UserMapper.toDto(null);

    // Then
    assertNull(result);
  }

  @Test
  void testUpdateUserFromDto() {
    // Given
    User existingUser = new User();
    existingUser.setEmail("old@example.com");
    existingUser.setName("Old User");

    Auth0UserDto dto = new Auth0UserDto();
    dto.setEmail("new@example.com");
    dto.setName("New User");
    dto.setPassword("newpassword123");
    dto.setEmailVerified(true);

    // When
    auth0UserMapper.updateUserFromDto(dto, existingUser);

    // Then
    assertEquals("new@example.com", existingUser.getEmail());
    assertEquals("New User", existingUser.getName());
  }

  @Test
  void testUpdateUserFromDtoWithNullPassword() {
    // Given
    User existingUser = new User();
    existingUser.setEmail("old@example.com");
    existingUser.setName("Old User");

    Auth0UserDto dto = new Auth0UserDto();
    dto.setEmail("new@example.com");
    dto.setName("New User");
    dto.setPassword(null);

    // When
    auth0UserMapper.updateUserFromDto(dto, existingUser);

    // Then
    assertEquals("new@example.com", existingUser.getEmail());
    assertEquals("New User", existingUser.getName());
  }

  @Test
  void testMapperWithNullMetadata() {
    // Given
    Auth0UserDto dto = new Auth0UserDto();
    dto.setEmail("test@example.com");
    dto.setName("Test User");
    dto.setUserMetadata(null);
    dto.setAppMetadata(null);

    // When
    User result = auth0UserMapper.toAuth0User(dto);

    // Then
    assertNotNull(result);
    assertEquals("test@example.com", result.getEmail());
    assertEquals("Test User", result.getName());
    assertNull(result.getUserMetadata());
    assertNull(result.getAppMetadata());
  }

  @Test
  void testMapperWithEmptyMetadata() {
    // Given
    Auth0UserDto dto = new Auth0UserDto();
    dto.setEmail("test@example.com");
    dto.setName("Test User");
    dto.setUserMetadata(new HashMap<>());
    dto.setAppMetadata(new HashMap<>());

    // When
    User result = auth0UserMapper.toAuth0User(dto);

    // Then
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
    // Given
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

    // When
    User result = auth0UserMapper.toAuth0User(dto);

    // Then
    assertNotNull(result);
    assertEquals(userMetadata, result.getUserMetadata());
    assertEquals(appMetadata, result.getAppMetadata());
  }

  @Test
  void testMapperHandlesOptionalFields() {
    // Given
    Auth0UserDto dto = new Auth0UserDto();
    dto.setEmail("test@example.com");
    dto.setName("Test User");
    dto.setNickname("testuser");
    dto.setPicture("https://example.com/picture.jpg");

    // When
    User result = auth0UserMapper.toAuth0User(dto);

    // Then
    assertNotNull(result);
    assertEquals("test@example.com", result.getEmail());
    assertEquals("Test User", result.getName());
    assertEquals("testuser", result.getNickname());
    assertEquals("https://example.com/picture.jpg", result.getPicture());
  }
} 