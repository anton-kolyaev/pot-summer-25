package com.coherentsolutions.pot.insuranceservice.unit.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.coherentsolutions.pot.insuranceservice.exception.Auth0Exception;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for Auth0Exception.
 *
 * <p>Tests cover exception creation and property access.
 */
class Auth0ExceptionTest {

  @Test
  void testConstructorWithMessage() {
    // Given
    String message = "Test error message";

    // When
    Auth0Exception exception = new Auth0Exception(message);

    // Then
    assertEquals(message, exception.getMessage());
    assertEquals("AUTH0_ERROR", exception.getErrorCode());
    assertEquals(500, exception.getHttpStatus());
  }

  @Test
  void testConstructorWithMessageAndCause() {
    // Given
    String message = "Test error message";
    Throwable cause = new RuntimeException("Original cause");

    // When
    Auth0Exception exception = new Auth0Exception(message, cause);

    // Then
    assertEquals(message, exception.getMessage());
    assertEquals(cause, exception.getCause());
    assertEquals("AUTH0_ERROR", exception.getErrorCode());
    assertEquals(500, exception.getHttpStatus());
  }

  @Test
  void testConstructorWithMessageErrorCodeAndHttpStatus() {
    // Given
    String message = "Test error message";
    String errorCode = "AUTH0_VALIDATION_ERROR";
    int httpStatus = 400;

    // When
    Auth0Exception exception = new Auth0Exception(message, errorCode, httpStatus);

    // Then
    assertEquals(message, exception.getMessage());
    assertEquals(errorCode, exception.getErrorCode());
    assertEquals(httpStatus, exception.getHttpStatus());
  }

  @Test
  void testConstructorWithMessageCauseErrorCodeAndHttpStatus() {
    // Given
    String message = "Test error message";
    Throwable cause = new RuntimeException("Original cause");
    String errorCode = "AUTH0_CONNECTION_ERROR";
    int httpStatus = 503;

    // When
    Auth0Exception exception = new Auth0Exception(message, cause, errorCode, httpStatus);

    // Then
    assertEquals(message, exception.getMessage());
    assertEquals(cause, exception.getCause());
    assertEquals(errorCode, exception.getErrorCode());
    assertEquals(httpStatus, exception.getHttpStatus());
  }

  @Test
  void testExceptionInheritance() {
    // Given & When
    Auth0Exception exception = new Auth0Exception("Test message");

    // Then
    assertNotNull(exception);
    // Should be instance of RuntimeException
    assertEquals(RuntimeException.class, exception.getClass().getSuperclass());
  }

  @Test
  void testDefaultErrorCodeAndHttpStatus() {
    // Given & When
    Auth0Exception exception = new Auth0Exception("Test message");

    // Then
    assertEquals("AUTH0_ERROR", exception.getErrorCode());
    assertEquals(500, exception.getHttpStatus());
  }

  @Test
  void testCustomErrorCodeAndHttpStatus() {
    // Given
    String errorCode = "CUSTOM_ERROR";
    int httpStatus = 422;

    // When
    Auth0Exception exception = new Auth0Exception("Test message", errorCode, httpStatus);

    // Then
    assertEquals(errorCode, exception.getErrorCode());
    assertEquals(httpStatus, exception.getHttpStatus());
  }

  @Test
  void testExceptionWithNullMessage() {
    // Given & When
    Auth0Exception exception = new Auth0Exception(null);

    // Then
    assertEquals(null, exception.getMessage());
    assertEquals("AUTH0_ERROR", exception.getErrorCode());
    assertEquals(500, exception.getHttpStatus());
  }

  @Test
  void testExceptionWithEmptyMessage() {
    // Given & When
    Auth0Exception exception = new Auth0Exception("");

    // Then
    assertEquals("", exception.getMessage());
    assertEquals("AUTH0_ERROR", exception.getErrorCode());
    assertEquals(500, exception.getHttpStatus());
  }
} 