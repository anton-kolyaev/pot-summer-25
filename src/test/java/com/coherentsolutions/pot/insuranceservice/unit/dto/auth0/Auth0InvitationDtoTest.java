package com.coherentsolutions.pot.insuranceservice.unit.dto.auth0;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.coherentsolutions.pot.insuranceservice.dto.auth0.Auth0InvitationDto;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class Auth0InvitationDtoTest {

  @Test
  @DisplayName("Should create Auth0InvitationDto with builder")
  void shouldCreateAuth0InvitationDtoWithBuilder() {
    // Given
    final String email = "test@example.com";
    final String name = "Test User";
    final String connection = "Username-Password-Authentication";
    final String clientId = "test-client-id";
    Map<String, Object> userMetadata = new HashMap<>();
    userMetadata.put("firstName", "Test");
    userMetadata.put("lastName", "User");
    Map<String, Object> appMetadata = new HashMap<>();
    appMetadata.put("role", "user");

    // When
    Auth0InvitationDto dto = Auth0InvitationDto.builder()
        .email(email)
        .name(name)
        .connection(connection)
        .clientId(clientId)
        .userMetadata(userMetadata)
        .appMetadata(appMetadata)
        .build();

    // Then
    assertEquals(email, dto.getEmail());
    assertEquals(name, dto.getName());
    assertEquals(connection, dto.getConnection());
    assertEquals(clientId, dto.getClientId());
    assertEquals(userMetadata, dto.getUserMetadata());
    assertEquals(appMetadata, dto.getAppMetadata());
  }

  @Test
  @DisplayName("Should create Auth0InvitationDto with minimal fields")
  void shouldCreateAuth0InvitationDtoWithMinimalFields() {
    // Given
    String email = "test@example.com";
    String name = "Test User";
    String connection = "Username-Password-Authentication";

    // When
    Auth0InvitationDto dto = Auth0InvitationDto.builder()
        .email(email)
        .name(name)
        .connection(connection)
        .build();

    // Then
    assertEquals(email, dto.getEmail());
    assertEquals(name, dto.getName());
    assertEquals(connection, dto.getConnection());
    assertNull(dto.getClientId());
    assertNull(dto.getUserMetadata());
    assertNull(dto.getAppMetadata());
  }

  @Test
  @DisplayName("Should create Auth0InvitationDto with null metadata")
  void shouldCreateAuth0InvitationDtoWithNullMetadata() {
    // Given
    String email = "test@example.com";
    String name = "Test User";
    String connection = "Username-Password-Authentication";
    String clientId = "test-client-id";

    // When
    Auth0InvitationDto dto = Auth0InvitationDto.builder()
        .email(email)
        .name(name)
        .connection(connection)
        .clientId(clientId)
        .userMetadata(null)
        .appMetadata(null)
        .build();

    // Then
    assertEquals(email, dto.getEmail());
    assertEquals(name, dto.getName());
    assertEquals(connection, dto.getConnection());
    assertEquals(clientId, dto.getClientId());
    assertNull(dto.getUserMetadata());
    assertNull(dto.getAppMetadata());
  }

  @Test
  @DisplayName("Should create Auth0InvitationDto with empty metadata")
  void shouldCreateAuth0InvitationDtoWithEmptyMetadata() {
    // Given
    String email = "test@example.com";
    String name = "Test User";
    String connection = "Username-Password-Authentication";
    Map<String, Object> userMetadata = new HashMap<>();
    Map<String, Object> appMetadata = new HashMap<>();

    // When
    Auth0InvitationDto dto = Auth0InvitationDto.builder()
        .email(email)
        .name(name)
        .connection(connection)
        .userMetadata(userMetadata)
        .appMetadata(appMetadata)
        .build();

    // Then
    assertEquals(email, dto.getEmail());
    assertEquals(name, dto.getName());
    assertEquals(connection, dto.getConnection());
    assertEquals(userMetadata, dto.getUserMetadata());
    assertEquals(appMetadata, dto.getAppMetadata());
    assertNotNull(dto.getUserMetadata());
    assertNotNull(dto.getAppMetadata());
  }
}
