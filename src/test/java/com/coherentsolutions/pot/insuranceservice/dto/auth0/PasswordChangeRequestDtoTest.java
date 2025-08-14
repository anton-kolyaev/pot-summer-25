package com.coherentsolutions.pot.insuranceservice.dto.auth0;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PasswordChangeRequestDtoTest {

  private Validator validator;

  @BeforeEach
  void setUp() {
    validator = Validation.buildDefaultValidatorFactory().getValidator();
  }

  @Test
  void constructorWithValidEmailCreatesDto() {
    // Given
    String email = "test@example.com";

    // When
    PasswordChangeRequestDto dto = new PasswordChangeRequestDto(email);

    // Then
    assertEquals(email, dto.email());
  }

  @Test
  void validationWithValidEmailPasses() {
    // Given
    PasswordChangeRequestDto dto = new PasswordChangeRequestDto("test@example.com");

    // When
    Set<ConstraintViolation<PasswordChangeRequestDto>> violations = validator.validate(dto);

    // Then
    assertTrue(violations.isEmpty());
  }

  @Test
  void validationWithInvalidEmailFails() {
    // Given
    PasswordChangeRequestDto dto = new PasswordChangeRequestDto("invalid-email");

    // When
    Set<ConstraintViolation<PasswordChangeRequestDto>> violations = validator.validate(dto);

    // Then
    assertFalse(violations.isEmpty());
    assertTrue(violations.stream()
        .anyMatch(violation -> violation.getMessage().contains("valid email address")));
  }

  @Test
  void validationWithEmptyEmailFails() {
    // Given
    PasswordChangeRequestDto dto = new PasswordChangeRequestDto("");

    // When
    Set<ConstraintViolation<PasswordChangeRequestDto>> violations = validator.validate(dto);

    // Then
    assertFalse(violations.isEmpty());
    assertTrue(violations.stream()
        .anyMatch(violation -> violation.getMessage().contains("required")));
  }

  @Test
  void validationWithNullEmailFails() {
    // Given
    PasswordChangeRequestDto dto = new PasswordChangeRequestDto(null);

    // When
    Set<ConstraintViolation<PasswordChangeRequestDto>> violations = validator.validate(dto);

    // Then
    assertFalse(violations.isEmpty());
    assertTrue(violations.stream()
        .anyMatch(violation -> violation.getMessage().contains("required")));
  }
}
