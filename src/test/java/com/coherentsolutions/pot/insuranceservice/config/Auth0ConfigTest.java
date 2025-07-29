package com.coherentsolutions.pot.insuranceservice.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.auth0.client.mgmt.ManagementAPI;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for Auth0Config.
 *
 * <p>Tests cover configuration and conditional bean creation.
 */
class Auth0ConfigTest {

  @Test
  void testAuth0ConfigClassLoadsSuccessfully() {
    // Arrange & Act
    Auth0Config config = new Auth0Config();

    // Assert
    assertNotNull(config);
  }

  @Test
  void testManagementAPIWithValidPropertiesReturnsManagementAPI() {
    // Arrange
    Auth0Config config = new Auth0Config();
    Auth0Properties properties = new Auth0Properties(
        "test-domain.auth0.com",
        "test-api-token",
        "test-audience",
        10000,
        true
    );

    // Act
    ManagementAPI result = config.managementAPI(properties);

    // Assert
    assertNotNull(result);
  }

  @Test
  void testManagementAPIWithNullDomainThrowsException() {
    // Arrange
    Auth0Config config = new Auth0Config();
    Auth0Properties properties = new Auth0Properties(
        null,
        "test-api-token",
        "test-audience",
        10000,
        true
    );

    // Act & Assert
    assertThrows(IllegalStateException.class, () -> config.managementAPI(properties));
  }

  @Test
  void testManagementAPIWithEmptyDomainThrowsException() {
    // Arrange
    Auth0Config config = new Auth0Config();
    Auth0Properties properties = new Auth0Properties(
        "",
        "test-api-token",
        "test-audience",
        10000,
        true
    );

    // Act & Assert
    assertThrows(IllegalStateException.class, () -> config.managementAPI(properties));
  }

  @Test
  void testManagementAPIWithNullApiTokenThrowsException() {
    // Arrange
    Auth0Config config = new Auth0Config();
    Auth0Properties properties = new Auth0Properties(
        "test-domain.auth0.com",
        null,
        "test-audience",
        10000,
        true
    );

    // Act & Assert
    assertThrows(IllegalStateException.class, () -> config.managementAPI(properties));
  }

  @Test
  void testManagementAPIWithEmptyApiTokenThrowsException() {
    // Arrange
    Auth0Config config = new Auth0Config();
    Auth0Properties properties = new Auth0Properties(
        "test-domain.auth0.com",
        "",
        "test-audience",
        10000,
        true
    );

    // Act & Assert
    assertThrows(IllegalStateException.class, () -> config.managementAPI(properties));
  }

  @Test
  void testConfigClassHasConfigurationAnnotation() {
    // Arrange & Act
    Auth0Config config = new Auth0Config();

    // Assert
    assertNotNull(config.getClass()
        .getAnnotation(org.springframework.context.annotation.Configuration.class));
  }
} 