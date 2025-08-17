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
    String clientId = "test-client-id";
    String connection = "Username-Password-Authentication";
    String connectionId = "con_123456789";
    int timeout = 5000;
    boolean enabled = true;

    // When
    Auth0Properties properties = new Auth0Properties(domain, apiToken, audience, clientId, connection, connectionId, timeout, enabled);

    // Then
    assertEquals(domain, properties.domain());
    assertEquals(apiToken, properties.apiToken());
    assertEquals(audience, properties.audience());
    assertEquals(clientId, properties.clientId());
    assertEquals(connection, properties.connection());
    assertEquals(connectionId, properties.connectionId());
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
    String clientId = "test-client-id";
    String connection = "Username-Password-Authentication";
    String connectionId = null;
    int timeout = 0;
    boolean enabled = true;

    // When
    Auth0Properties properties = new Auth0Properties(domain, apiToken, audience, clientId, connection, connectionId, timeout, enabled);

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
    String clientId = "test-client-id";
    String connection = "Username-Password-Authentication";
    String connectionId = null;
    int timeout = -1000;
    boolean enabled = true;

    // When
    Auth0Properties properties = new Auth0Properties(domain, apiToken, audience, clientId, connection, connectionId, timeout, enabled);

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
    String clientId = "test-client-id";
    String connection = "Username-Password-Authentication";
    String connectionId = null;
    int timeout = 15000;
    boolean enabled = true;

    // When
    Auth0Properties properties = new Auth0Properties(domain, apiToken, audience, clientId, connection, connectionId, timeout, enabled);

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
    String clientId = "test-client-id";
    String connection = "Username-Password-Authentication";
    String connectionId = null;
    int timeout = 5000;
    boolean enabled = false;

    // When
    Auth0Properties properties = new Auth0Properties(domain, apiToken, audience, clientId, connection, connectionId, timeout, enabled);

    // Then
    assertEquals(domain, properties.domain());
    assertEquals(apiToken, properties.apiToken());
    assertEquals(audience, properties.audience());
    assertEquals(clientId, properties.clientId());
    assertEquals(connection, properties.connection());
    assertNull(properties.connectionId());
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
    String clientId = null;
    String connection = null;
    String connectionId = null;
    int timeout = 10000;
    boolean enabled = true;

    // When
    Auth0Properties properties = new Auth0Properties(domain, apiToken, audience, clientId, connection, connectionId, timeout, enabled);

    // Then
    assertNull(properties.domain());
    assertNull(properties.apiToken());
    assertNull(properties.audience());
    assertNull(properties.clientId());
    assertNull(properties.connection());
    assertNull(properties.connectionId());
    assertEquals(timeout, properties.timeout());
    assertTrue(properties.enabled());
  }
}
