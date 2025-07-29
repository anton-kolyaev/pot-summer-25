package com.coherentsolutions.pot.insuranceservice.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.auth0.client.mgmt.GrantsEntity;
import com.auth0.client.mgmt.ManagementAPI;
import com.auth0.exception.Auth0Exception;
import com.auth0.net.Request;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;

@ExtendWith(MockitoExtension.class)
class Auth0HealthIndicatorTest {

  @Mock
  private Auth0Properties auth0Properties;

  @Mock
  private ManagementAPI managementAPI;

  @Mock
  private GrantsEntity grantsEntity;

  private Auth0HealthIndicator healthIndicator;

  @BeforeEach
  void setUp() {
    healthIndicator = new Auth0HealthIndicator(auth0Properties, managementAPI);
  }

  @Test
  void testHealthIndicatorClassLoadsSuccessfully() {
    // Assert
    assertNotNull(healthIndicator);
  }

  @Test
  void testHealthIndicatorHasComponentAnnotation() {
    // Assert
    assertNotNull(healthIndicator.getClass()
        .getAnnotation(org.springframework.stereotype.Component.class));
  }

  @Test
  void testHealthIndicatorHasConditionalOnPropertyAnnotation() {
    // Assert
    assertNotNull(healthIndicator.getClass()
        .getAnnotation(org.springframework.boot.autoconfigure.condition.ConditionalOnProperty.class));
  }

  @Test
  void testHealthIndicatorImplementsHealthIndicator() {
    // Assert
    assertTrue(healthIndicator instanceof org.springframework.boot.actuate.health.HealthIndicator);
  }

  @SuppressWarnings("unchecked")
  @Test
  void testHealthWhenFullyConfiguredAndConnectedShouldReturnUp() throws Auth0Exception {
    // Given
    when(auth0Properties.domain()).thenReturn("test.auth0.com");
    when(auth0Properties.apiToken()).thenReturn("test-token");
    when(auth0Properties.audience()).thenReturn("test-audience");
    when(auth0Properties.timeout()).thenReturn(5000);
    when(managementAPI.grants()).thenReturn(grantsEntity);
    Request requestMock = mock(Request.class);
    when(grantsEntity.list(any(), any())).thenReturn(requestMock);
    when(requestMock.execute()).thenReturn(null);

    // When
    Health result = healthIndicator.health();

    // Then
    assertEquals(Status.UP, result.getStatus());
    assertNotNull(result.getDetails());
    assertNotNull(result.getDetails().get("domain"));
    assertNotNull(result.getDetails().get("audience"));
    assertNotNull(result.getDetails().get("timeout"));
  }

  @Test
  void testHealthWhenDomainMissingShouldReturnDown() {
    // Given
    when(auth0Properties.domain()).thenReturn("");
    when(auth0Properties.apiToken()).thenReturn("test-token");

    // When
    Health health = healthIndicator.health();

    // Then
    assertEquals(Status.DOWN, health.getStatus());
    assertEquals("Incomplete configuration", health.getDetails().get("reason"));
    assertFalse((Boolean) health.getDetails().get("domain_configured"));
    assertTrue((Boolean) health.getDetails().get("api_token_configured"));
  }

  @Test
  void testHealthWhenDomainNullShouldReturnDown() {
    // Given
    when(auth0Properties.domain()).thenReturn(null);
    when(auth0Properties.apiToken()).thenReturn("test-token");

    // When
    Health health = healthIndicator.health();

    // Then
    assertEquals(Status.DOWN, health.getStatus());
    assertEquals("Incomplete configuration", health.getDetails().get("reason"));
    assertFalse((Boolean) health.getDetails().get("domain_configured"));
    assertTrue((Boolean) health.getDetails().get("api_token_configured"));
  }

  @Test
  void testHealthWhenApiTokenMissingShouldReturnDown() {
    // Given
    when(auth0Properties.domain()).thenReturn("test.auth0.com");
    when(auth0Properties.apiToken()).thenReturn(null);

    // When
    Health health = healthIndicator.health();

    // Then
    assertEquals(Status.DOWN, health.getStatus());
    assertEquals("Incomplete configuration", health.getDetails().get("reason"));
    assertTrue((Boolean) health.getDetails().get("domain_configured"));
    assertFalse((Boolean) health.getDetails().get("api_token_configured"));
  }

  @Test
  void testHealthWhenApiTokenEmptyShouldReturnDown() {
    // Given
    when(auth0Properties.domain()).thenReturn("test.auth0.com");
    when(auth0Properties.apiToken()).thenReturn("");

    // When
    Health health = healthIndicator.health();

    // Then
    assertEquals(Status.DOWN, health.getStatus());
    assertEquals("Incomplete configuration", health.getDetails().get("reason"));
    assertTrue((Boolean) health.getDetails().get("domain_configured"));
    assertFalse((Boolean) health.getDetails().get("api_token_configured"));
  }

  @Test
  void testHealthWhenBothMissingShouldReturnDown() {
    // Given
    when(auth0Properties.domain()).thenReturn(null);
    when(auth0Properties.apiToken()).thenReturn(null);

    // When
    Health health = healthIndicator.health();

    // Then
    assertEquals(Status.DOWN, health.getStatus());
    assertEquals("Incomplete configuration", health.getDetails().get("reason"));
    assertFalse((Boolean) health.getDetails().get("domain_configured"));
    assertFalse((Boolean) health.getDetails().get("api_token_configured"));
  }

  @Test
  void testHealthWhenAuth0ExceptionShouldReturnDown() throws Auth0Exception {
    // Given
    when(auth0Properties.domain()).thenReturn("test.auth0.com");
    when(auth0Properties.apiToken()).thenReturn("test-token");
    when(managementAPI.grants()).thenReturn(grantsEntity);
    Request requestMock = mock(Request.class);
    when(grantsEntity.list(any(), any())).thenReturn(requestMock);
    when(requestMock.execute()).thenThrow(new Auth0Exception("Auth0 connection failed"));

    // When
    Health health = healthIndicator.health();

    // Then
    assertEquals(Status.DOWN, health.getStatus());
    assertEquals("Auth0 API connection failed", health.getDetails().get("reason"));
    assertEquals("Auth0 connection failed", health.getDetails().get("error"));
  }

  @Test
  void testHealthWhenUnexpectedExceptionShouldReturnDown() throws Auth0Exception {
    // Given
    when(auth0Properties.domain()).thenReturn("test.auth0.com");
    when(auth0Properties.apiToken()).thenReturn("test-token");
    when(managementAPI.grants()).thenReturn(grantsEntity);
    Request requestMock = mock(Request.class);
    when(grantsEntity.list(any(), any())).thenReturn(requestMock);
    when(requestMock.execute()).thenThrow(new RuntimeException("Unexpected error"));

    // When
    Health health = healthIndicator.health();

    // Then
    assertEquals(Status.DOWN, health.getStatus());
    assertEquals("Unexpected error", health.getDetails().get("reason"));
    assertEquals("Unexpected error", health.getDetails().get("error"));
  }

  @Test
  void testHealthIndicatorConstructorInjectsDependencies() {
    // Assert
    assertNotNull(healthIndicator);
    // If the constructor works, dependencies are injected
  }

  @Test
  void testHealthIndicatorHasHealthMethod() throws Exception {
    // Assert
    assertNotNull(healthIndicator.getClass().getMethod("health"));
  }

  @Test
  void testHealthIndicatorMethodReturnsHealth() throws Exception {
    // Assert
    assertEquals(Health.class, healthIndicator.getClass().getMethod("health").getReturnType());
  }
} 