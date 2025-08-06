package com.coherentsolutions.pot.insuranceservice.unit.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.coherentsolutions.pot.insuranceservice.config.Auth0Properties;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Auth0Properties Unit Tests")
class Auth0PropertiesTest {

  @Test
  @DisplayName("Should create Auth0Properties with valid values")
  void shouldCreateAuth0PropertiesWithValidValues() {
    // Given
    String domain = "test.auth0.com";
    String apiToken = "test-token";
    String audience = "https://api.test.com";
    int timeout = 5000;
    boolean enabled = true;

    // When
    Auth0Properties properties = new Auth0Properties(domain, apiToken, audience, timeout, enabled);

    // Then
    assertEquals(domain, properties.domain());
    assertEquals(apiToken, properties.apiToken());
    assertEquals(audience, properties.audience());
    assertEquals(timeout, properties.timeout());
    assertEquals(enabled, properties.enabled());
  }

  @Test
  @DisplayName("Should set default timeout when timeout is zero")
  void shouldSetDefaultTimeoutWhenTimeoutIsZero() {
    // Given
    String domain = "test.auth0.com";
    String apiToken = "test-token";
    String audience = "https://api.test.com";
    int timeout = 0;
    boolean enabled = true;

    // When
    Auth0Properties properties = new Auth0Properties(domain, apiToken, audience, timeout, enabled);

    // Then
    assertEquals(10000, properties.timeout());
  }

  @Test
  @DisplayName("Should set default timeout when timeout is negative")
  void shouldSetDefaultTimeoutWhenTimeoutIsNegative() {
    // Given
    String domain = "test.auth0.com";
    String apiToken = "test-token";
    String audience = "https://api.test.com";
    int timeout = -1000;
    boolean enabled = true;

    // When
    Auth0Properties properties = new Auth0Properties(domain, apiToken, audience, timeout, enabled);

    // Then
    assertEquals(10000, properties.timeout());
  }

  @Test
  @DisplayName("Should keep custom timeout when timeout is positive")
  void shouldKeepCustomTimeoutWhenTimeoutIsPositive() {
    // Given
    String domain = "test.auth0.com";
    String apiToken = "test-token";
    String audience = "https://api.test.com";
    int timeout = 15000;
    boolean enabled = true;

    // When
    Auth0Properties properties = new Auth0Properties(domain, apiToken, audience, timeout, enabled);

    // Then
    assertEquals(15000, properties.timeout());
  }

  @Test
  @DisplayName("Should create Auth0Properties with disabled state")
  void shouldCreateAuth0PropertiesWithDisabledState() {
    // Given
    String domain = "test.auth0.com";
    String apiToken = "test-token";
    String audience = "https://api.test.com";
    int timeout = 5000;
    boolean enabled = false;

    // When
    Auth0Properties properties = new Auth0Properties(domain, apiToken, audience, timeout, enabled);

    // Then
    assertEquals(domain, properties.domain());
    assertEquals(apiToken, properties.apiToken());
    assertEquals(audience, properties.audience());
    assertEquals(timeout, properties.timeout());
    assertFalse(properties.enabled());
  }

  @Test
  @DisplayName("Should create Auth0Properties with null values")
  void shouldCreateAuth0PropertiesWithNullValues() {
    // Given
    String domain = null;
    String apiToken = null;
    String audience = null;
    int timeout = 5000;
    boolean enabled = true;

    // When
    Auth0Properties properties = new Auth0Properties(domain, apiToken, audience, timeout, enabled);

    // Then
    assertNull(properties.domain());
    assertNull(properties.apiToken());
    assertNull(properties.audience());
    assertEquals(timeout, properties.timeout());
    assertTrue(properties.enabled());
  }

  @Test
  @DisplayName("Should create Auth0Properties with empty strings")
  void shouldCreateAuth0PropertiesWithEmptyStrings() {
    // Given
    String domain = "";
    String apiToken = "";
    String audience = "";
    int timeout = 5000;
    boolean enabled = true;

    // When
    Auth0Properties properties = new Auth0Properties(domain, apiToken, audience, timeout, enabled);

    // Then
    assertEquals("", properties.domain());
    assertEquals("", properties.apiToken());
    assertEquals("", properties.audience());
    assertEquals(timeout, properties.timeout());
    assertTrue(properties.enabled());
  }
} 