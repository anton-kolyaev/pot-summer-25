package com.coherentsolutions.pot.insuranceservice.unit.dto.auth0;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.coherentsolutions.pot.insuranceservice.dto.auth0.PasswordChangeRequestDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PasswordChangeRequestDtoTest {

  @Test
  @DisplayName("Should create PasswordChangeRequestDto with email")
  void shouldCreatePasswordChangeRequestDtoWithEmail() {
    // Given
    String email = "test@example.com";

    // When
    PasswordChangeRequestDto dto = new PasswordChangeRequestDto(email);

    // Then
    assertEquals(email, dto.email());
    assertNotNull(dto);
  }

  @Test
  @DisplayName("Should create PasswordChangeRequestDto with different email formats")
  void shouldCreatePasswordChangeRequestDtoWithDifferentEmailFormats() {
    // Given
    String email1 = "user@example.com";
    String email2 = "user.name@example.com";
    String email3 = "user+tag@example.com";
    final String email4 = "user123@example-domain.com";

    // When
    PasswordChangeRequestDto dto1 = new PasswordChangeRequestDto(email1);
    PasswordChangeRequestDto dto2 = new PasswordChangeRequestDto(email2);
    PasswordChangeRequestDto dto3 = new PasswordChangeRequestDto(email3);
    final PasswordChangeRequestDto dto4 = new PasswordChangeRequestDto(email4);

    // Then
    assertEquals(email1, dto1.email());
    assertEquals(email2, dto2.email());
    assertEquals(email3, dto3.email());
    assertEquals(email4, dto4.email());
  }

  @Test
  @DisplayName("Should create PasswordChangeRequestDto with empty email")
  void shouldCreatePasswordChangeRequestDtoWithEmptyEmail() {
    // Given
    String email = "";

    // When
    PasswordChangeRequestDto dto = new PasswordChangeRequestDto(email);

    // Then
    assertEquals(email, dto.email());
  }

  @Test
  @DisplayName("Should create PasswordChangeRequestDto with null email")
  void shouldCreatePasswordChangeRequestDtoWithNullEmail() {
    // Given
    String email = null;

    // When
    PasswordChangeRequestDto dto = new PasswordChangeRequestDto(email);

    // Then
    assertEquals(email, dto.email());
  }
}
