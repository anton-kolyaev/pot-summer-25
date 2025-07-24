package com.coherentsolutions.pot.insuranceservice.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for Auth0Exception.
 *
 * <p>Tests cover exception creation and property access.
 */
class Auth0ExceptionTest {

  @Test
  void testConstructorWithMessage() {
    // Arrange
    String message = "Test error message";

    // Act
    Auth0Exception exception = new Auth0Exception(message);

    // Assert
    assertEquals(message, exception.getMessage());
    assertEquals("AUTH0_ERROR", exception.getErrorCode());
    assertEquals(500, exception.getHttpStatus());
  }

  @Test
  void testConstructorWithMessageAndCause() {
    // Arrange
    String message = "Test error message";
    Throwable cause = new RuntimeException("Original cause");

    // Act
    Auth0Exception exception = new Auth0Exception(message, cause);

    // Assert
    assertEquals(message, exception.getMessage());
    assertEquals(cause, exception.getCause());
    assertEquals("AUTH0_ERROR", exception.getErrorCode());
    assertEquals(500, exception.getHttpStatus());
  }

  @Test
  void testConstructorWithMessageErrorCodeAndHttpStatus() {
    // Arrange
    String message = "Test error message";
    String errorCode = "AUTH0_VALIDATION_ERROR";
    int httpStatus = 400;

    // Act
    Auth0Exception exception = new Auth0Exception(message, errorCode, httpStatus);

    // Assert
    assertEquals(message, exception.getMessage());
    assertEquals(errorCode, exception.getErrorCode());
    assertEquals(httpStatus, exception.getHttpStatus());
  }

  @Test
  void testConstructorWithMessageCauseErrorCodeAndHttpStatus() {
    // Arrange
    String message = "Test error message";
    Throwable cause = new RuntimeException("Original cause");
    String errorCode = "AUTH0_CONNECTION_ERROR";
    int httpStatus = 503;

    // Act
    Auth0Exception exception = new Auth0Exception(message, cause, errorCode, httpStatus);

    // Assert
    assertEquals(message, exception.getMessage());
    assertEquals(cause, exception.getCause());
    assertEquals(errorCode, exception.getErrorCode());
    assertEquals(httpStatus, exception.getHttpStatus());
  }

  @Test
  void testExceptionInheritance() {
    // Arrange & Act
    Auth0Exception exception = new Auth0Exception("Test message");

    // Assert
    assertNotNull(exception);
    // Should be instance of RuntimeException
    assertEquals(RuntimeException.class, exception.getClass().getSuperclass());
  }

  @Test
  void testDefaultErrorCodeAndHttpStatus() {
    // Arrange & Act
    Auth0Exception exception = new Auth0Exception("Test message");

    // Assert
    assertEquals("AUTH0_ERROR", exception.getErrorCode());
    assertEquals(500, exception.getHttpStatus());
  }

  @Test
  void testCustomErrorCodeAndHttpStatus() {
    // Arrange
    String errorCode = "CUSTOM_ERROR";
    int httpStatus = 422;

    // Act
    Auth0Exception exception = new Auth0Exception("Test message", errorCode, httpStatus);

    // Assert
    assertEquals(errorCode, exception.getErrorCode());
    assertEquals(httpStatus, exception.getHttpStatus());
  }

  @Test
  void testExceptionWithNullMessage() {
    // Arrange & Act
    Auth0Exception exception = new Auth0Exception(null);

    // Assert
    assertEquals(null, exception.getMessage());
    assertEquals("AUTH0_ERROR", exception.getErrorCode());
    assertEquals(500, exception.getHttpStatus());
  }

  @Test
  void testExceptionWithEmptyMessage() {
    // Arrange & Act
    Auth0Exception exception = new Auth0Exception("");

    // Assert
    assertEquals("", exception.getMessage());
    assertEquals("AUTH0_ERROR", exception.getErrorCode());
    assertEquals(500, exception.getHttpStatus());
  }
} 