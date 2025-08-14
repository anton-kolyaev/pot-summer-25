package com.coherentsolutions.pot.insuranceservice.unit.dto.auth0;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.coherentsolutions.pot.insuranceservice.dto.auth0.Auth0UserDto;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class Auth0UserDtoTest {

  @Test
  @DisplayName("Should create Auth0UserDto with all fields")
  void shouldCreateAuth0UserDtoWithAllFields() {
    // Given
    final String userId = "auth0|123456789";
    final String email = "test@example.com";
    final String password = "password123";
    final String name = "Test User";
    final String nickname = "testuser";
    final String picture = "https://example.com/picture.jpg";
    Map<String, Object> userMetadata = new HashMap<>();
    userMetadata.put("firstName", "Test");
    userMetadata.put("lastName", "User");
    Map<String, Object> appMetadata = new HashMap<>();
    appMetadata.put("role", "user");
    boolean emailVerified = true;
    boolean blocked = false;
    String connection = "Username-Password-Authentication";

    // When
    Auth0UserDto dto = new Auth0UserDto();
    dto.setUserId(userId);
    dto.setEmail(email);
    dto.setPassword(password);
    dto.setName(name);
    dto.setNickname(nickname);
    dto.setPicture(picture);
    dto.setUserMetadata(userMetadata);
    dto.setAppMetadata(appMetadata);
    dto.setEmailVerified(emailVerified);
    dto.setBlocked(blocked);
    dto.setConnection(connection);

    // Then
    assertEquals(userId, dto.getUserId());
    assertEquals(email, dto.getEmail());
    assertEquals(password, dto.getPassword());
    assertEquals(name, dto.getName());
    assertEquals(nickname, dto.getNickname());
    assertEquals(picture, dto.getPicture());
    assertEquals(userMetadata, dto.getUserMetadata());
    assertEquals(appMetadata, dto.getAppMetadata());
    assertEquals(emailVerified, dto.isEmailVerified());
    assertEquals(blocked, dto.isBlocked());
    assertEquals(connection, dto.getConnection());
  }

  @Test
  @DisplayName("Should create Auth0UserDto with constructor")
  void shouldCreateAuth0UserDtoWithConstructor() {
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
    assertNull(dto.getUserId());
    assertNull(dto.getNickname());
    assertNull(dto.getPicture());
    assertNull(dto.getUserMetadata());
    assertNull(dto.getAppMetadata());
    assertFalse(dto.isEmailVerified());
    assertFalse(dto.isBlocked());
    assertEquals("Username-Password-Authentication", dto.getConnection());
  }

  @Test
  @DisplayName("Should create Auth0UserDto with default values")
  void shouldCreateAuth0UserDtoWithDefaultValues() {
    // When
    Auth0UserDto dto = new Auth0UserDto();

    // Then
    assertNull(dto.getUserId());
    assertNull(dto.getEmail());
    assertNull(dto.getPassword());
    assertNull(dto.getName());
    assertNull(dto.getNickname());
    assertNull(dto.getPicture());
    assertNull(dto.getUserMetadata());
    assertNull(dto.getAppMetadata());
    assertFalse(dto.isEmailVerified());
    assertFalse(dto.isBlocked());
    assertEquals("Username-Password-Authentication", dto.getConnection());
  }

  @Test
  @DisplayName("Should create Auth0UserDto with null metadata")
  void shouldCreateAuth0UserDtoWithNullMetadata() {
    // Given
    String email = "test@example.com";
    String password = "password123";
    String name = "Test User";

    // When
    Auth0UserDto dto = new Auth0UserDto();
    dto.setEmail(email);
    dto.setPassword(password);
    dto.setName(name);
    dto.setUserMetadata(null);
    dto.setAppMetadata(null);

    // Then
    assertEquals(email, dto.getEmail());
    assertEquals(password, dto.getPassword());
    assertEquals(name, dto.getName());
    assertNull(dto.getUserMetadata());
    assertNull(dto.getAppMetadata());
  }

  @Test
  @DisplayName("Should create Auth0UserDto with empty metadata")
  void shouldCreateAuth0UserDtoWithEmptyMetadata() {
    // Given
    String email = "test@example.com";
    String password = "password123";
    String name = "Test User";
    Map<String, Object> userMetadata = new HashMap<>();
    Map<String, Object> appMetadata = new HashMap<>();

    // When
    Auth0UserDto dto = new Auth0UserDto();
    dto.setEmail(email);
    dto.setPassword(password);
    dto.setName(name);
    dto.setUserMetadata(userMetadata);
    dto.setAppMetadata(appMetadata);

    // Then
    assertEquals(email, dto.getEmail());
    assertEquals(password, dto.getPassword());
    assertEquals(name, dto.getName());
    assertEquals(userMetadata, dto.getUserMetadata());
    assertEquals(appMetadata, dto.getAppMetadata());
    assertNotNull(dto.getUserMetadata());
    assertNotNull(dto.getAppMetadata());
  }
}
