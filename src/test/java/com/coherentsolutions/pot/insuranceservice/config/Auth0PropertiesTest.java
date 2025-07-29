package com.coherentsolutions.pot.insuranceservice.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for Auth0Properties.
 *
 * <p>Tests cover record creation and default values.
 */
class Auth0PropertiesTest {

  @Test
  void testConstructorWithValidDataCreatesProperties() {
    // Arrange & Act
    Auth0Properties properties = new Auth0Properties(
        "test-domain.auth0.com",
        "test-api-token",
        "https://test-domain.auth0.com/api/v2/",
        15000,
        true
    );

    // Assert
    assertNotNull(properties);
    assertEquals("test-domain.auth0.com", properties.domain());
    assertEquals("test-api-token", properties.apiToken());
    assertEquals("https://test-domain.auth0.com/api/v2/", properties.audience());
    assertEquals(15000, properties.timeout());
    assertEquals(true, properties.enabled());
  }

  @Test
  void testConstructorWithZeroTimeoutSetsDefaultTimeout() {
    // Arrange & Act
    Auth0Properties properties = new Auth0Properties(
        "test-domain.auth0.com",
        "test-api-token",
        "https://test-domain.auth0.com/api/v2/",
        0,
        true
    );

    // Assert
    assertEquals(10000, properties.timeout());
  }

  @Test
  void testConstructorWithNegativeTimeoutSetsDefaultTimeout() {
    // Arrange & Act
    Auth0Properties properties = new Auth0Properties(
        "test-domain.auth0.com",
        "test-api-token",
        "https://test-domain.auth0.com/api/v2/",
        -1000,
        true
    );

    // Assert
    assertEquals(10000, properties.timeout());
  }

  @Test
  void testConstructorWithValidTimeoutKeepsTimeout() {
    // Arrange & Act
    Auth0Properties properties = new Auth0Properties(
        "test-domain.auth0.com",
        "test-api-token",
        "https://test-domain.auth0.com/api/v2/",
        5000,
        true
    );

    // Assert
    assertEquals(5000, properties.timeout());
  }

  @Test
  void testConstructorWithNullValuesHandlesCorrectly() {
    // Arrange & Act
    Auth0Properties properties = new Auth0Properties(
        null,
        null,
        null,
        10000,
        false
    );

    // Assert
    assertNotNull(properties);
    assertEquals(null, properties.domain());
    assertEquals(null, properties.apiToken());
    assertEquals(null, properties.audience());
    assertEquals(10000, properties.timeout());
    assertEquals(false, properties.enabled());
  }

  @Test
  void testConstructorWithEmptyStringsHandlesCorrectly() {
    // Arrange & Act
    Auth0Properties properties = new Auth0Properties(
        "",
        "",
        "",
        10000,
        false
    );

    // Assert
    assertNotNull(properties);
    assertEquals("", properties.domain());
    assertEquals("", properties.apiToken());
    assertEquals("", properties.audience());
    assertEquals(10000, properties.timeout());
    assertEquals(false, properties.enabled());
  }

  @Test
  void testRecordImmutabilityWorksCorrectly() {
    // Arrange
    Auth0Properties properties = new Auth0Properties(
        "test-domain.auth0.com",
        "test-api-token",
        "https://test-domain.auth0.com/api/v2/",
        10000,
        true
    );

    // Act & Assert - Record should be immutable, so we can only read values
    assertEquals("test-domain.auth0.com", properties.domain());
    assertEquals("test-api-token", properties.apiToken());
    assertEquals("https://test-domain.auth0.com/api/v2/", properties.audience());
    assertEquals(10000, properties.timeout());
    assertEquals(true, properties.enabled());
  }

  @Test
  void testRecordEqualityWorksCorrectly() {
    // Arrange
    Auth0Properties properties1 = new Auth0Properties(
        "test-domain.auth0.com",
        "test-api-token",
        "https://test-domain.auth0.com/api/v2/",
        10000,
        true
    );

    Auth0Properties properties2 = new Auth0Properties(
        "test-domain.auth0.com",
        "test-api-token",
        "https://test-domain.auth0.com/api/v2/",
        10000,
        true
    );

    // Act & Assert
    assertEquals(properties1, properties2);
    assertEquals(properties1.hashCode(), properties2.hashCode());
  }
} 