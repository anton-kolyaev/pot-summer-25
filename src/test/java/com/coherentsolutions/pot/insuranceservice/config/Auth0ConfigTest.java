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
  void auth0Config_ClassLoadsSuccessfully() {
    // Arrange & Act
    Auth0Config config = new Auth0Config();

    // Assert
    assertNotNull(config);
  }

  @Test
  void managementAPI_WithValidProperties_ReturnsManagementAPI() {
    // Arrange
    Auth0Config config = new Auth0Config();
    Auth0Properties properties = new Auth0Properties(
        "test-domain.auth0.com",
        "test-client-id",
        "test-client-secret",
        "https://test-domain.auth0.com/api/v2/",
        10000,
        true
    );

    // Act
    ManagementAPI result = config.managementAPI(properties);

    // Assert
    assertNotNull(result);
  }

  @Test
  void managementAPI_WithNullDomain_ThrowsException() {
    // Arrange
    Auth0Config config = new Auth0Config();
    Auth0Properties properties = new Auth0Properties(
        null,
        "test-client-id",
        "test-client-secret",
        "https://test-domain.auth0.com/api/v2/",
        10000,
        true
    );

    // Act & Assert
    assertThrows(IllegalStateException.class, () -> config.managementAPI(properties));
  }

  @Test
  void managementAPI_WithEmptyDomain_ThrowsException() {
    // Arrange
    Auth0Config config = new Auth0Config();
    Auth0Properties properties = new Auth0Properties(
        "",
        "test-client-id",
        "test-client-secret",
        "https://test-domain.auth0.com/api/v2/",
        10000,
        true
    );

    // Act & Assert
    assertThrows(IllegalStateException.class, () -> config.managementAPI(properties));
  }

  @Test
  void managementAPI_WithNullClientId_ThrowsException() {
    // Arrange
    Auth0Config config = new Auth0Config();
    Auth0Properties properties = new Auth0Properties(
        "test-domain.auth0.com",
        null,
        "test-client-secret",
        "https://test-domain.auth0.com/api/v2/",
        10000,
        true
    );

    // Act & Assert
    assertThrows(IllegalStateException.class, () -> config.managementAPI(properties));
  }

  @Test
  void managementAPI_WithEmptyClientId_ThrowsException() {
    // Arrange
    Auth0Config config = new Auth0Config();
    Auth0Properties properties = new Auth0Properties(
        "test-domain.auth0.com",
        "",
        "test-client-secret",
        "https://test-domain.auth0.com/api/v2/",
        10000,
        true
    );

    // Act & Assert
    assertThrows(IllegalStateException.class, () -> config.managementAPI(properties));
  }

  @Test
  void configClass_HasConfigurationAnnotation() {
    // Arrange & Act
    Auth0Config config = new Auth0Config();

    // Assert
    assertNotNull(config.getClass().getAnnotation(org.springframework.context.annotation.Configuration.class));
  }
} 