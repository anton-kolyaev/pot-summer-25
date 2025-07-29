package com.coherentsolutions.pot.insuranceservice.unit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.auth0.client.mgmt.GrantsEntity;
import com.auth0.client.mgmt.ManagementAPI;
import com.auth0.exception.Auth0Exception;
import com.auth0.net.Request;
import com.coherentsolutions.pot.insuranceservice.config.Auth0Properties;
import com.coherentsolutions.pot.insuranceservice.controller.Auth0HealthController;
import com.coherentsolutions.pot.insuranceservice.unit.AbstractControllerTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

class Auth0HealthControllerTest extends AbstractControllerTest {

  private static Auth0Properties auth0Properties;
  private static ManagementAPI managementAPI;
  private static Auth0HealthController controller;

  @BeforeAll
  static void setUpClass() {
    auth0Properties = mock(Auth0Properties.class);
    managementAPI = mock(ManagementAPI.class);
    controller = new Auth0HealthController(auth0Properties, managementAPI);
    initializeCommonObjects(controller);
  }

  @org.junit.jupiter.api.BeforeEach
  void setUp() {
    resetMocks(auth0Properties, managementAPI);
  }

  @Test
  void checkConfigurationWhenFullyConfiguredShouldReturnOk() throws Exception {
    // Given
    when(auth0Properties.domain()).thenReturn("test.auth0.com");
    when(auth0Properties.apiToken()).thenReturn("test-token");
    when(auth0Properties.timeout()).thenReturn(5000);
    when(auth0Properties.audience()).thenReturn("test-audience");

    // When & Then
    getMockMvc().perform(get("/api/v1/auth0/health/config")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.domain_configured").value(true))
        .andExpect(jsonPath("$.api_token_configured").value(true))
        .andExpect(jsonPath("$.fully_configured").value(true))
        .andExpect(jsonPath("$.status").value("CONFIGURED"))
        .andExpect(jsonPath("$.message").value("Auth0 is properly configured"))
        .andExpect(jsonPath("$.timeout").value(5000))
        .andExpect(jsonPath("$.audience").value("test-audience"));
  }

  @Test
  void checkConfigurationWhenDomainMissingShouldReturnIncomplete() throws Exception {
    // Given
    when(auth0Properties.domain()).thenReturn("");
    when(auth0Properties.apiToken()).thenReturn("test-token");
    when(auth0Properties.timeout()).thenReturn(5000);
    when(auth0Properties.audience()).thenReturn("test-audience");

    // When & Then
    getMockMvc().perform(get("/api/v1/auth0/health/config")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.domain_configured").value(false))
        .andExpect(jsonPath("$.api_token_configured").value(true))
        .andExpect(jsonPath("$.fully_configured").value(false))
        .andExpect(jsonPath("$.status").value("INCOMPLETE"))
        .andExpect(jsonPath("$.message").value("Auth0 configuration is incomplete"));
  }

  @Test
  void checkConfigurationWhenApiTokenMissingShouldReturnIncomplete() throws Exception {
    // Given
    when(auth0Properties.domain()).thenReturn("test.auth0.com");
    when(auth0Properties.apiToken()).thenReturn(null);
    when(auth0Properties.timeout()).thenReturn(5000);
    when(auth0Properties.audience()).thenReturn("test-audience");

    // When & Then
    getMockMvc().perform(get("/api/v1/auth0/health/config")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.domain_configured").value(true))
        .andExpect(jsonPath("$.api_token_configured").value(false))
        .andExpect(jsonPath("$.fully_configured").value(false))
        .andExpect(jsonPath("$.status").value("INCOMPLETE"))
        .andExpect(jsonPath("$.message").value("Auth0 configuration is incomplete"));
  }

  @Test
  void checkConfigurationWhenExceptionOccursShouldReturnError() throws Exception {
    // Given
    when(auth0Properties.domain()).thenThrow(new RuntimeException("Test exception"));

    // When & Then
    getMockMvc().perform(get("/api/v1/auth0/health/config")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.status").value("ERROR"))
        .andExpect(jsonPath("$.message").value("Error checking configuration: Test exception"));
  }

  @SuppressWarnings("unchecked")
  @Test
  void testConnectivityWhenSuccessfulShouldReturnConnected() throws Exception {
    // Given
    GrantsEntity grantsEntity = mock(GrantsEntity.class);
    Request requestMock = mock(Request.class);
    when(managementAPI.grants()).thenReturn(grantsEntity);
    when(grantsEntity.list(any(), any())).thenReturn(requestMock);
    when(requestMock.execute()).thenReturn(null);

    // When & Then
    getMockMvc().perform(get("/api/v1/auth0/health/connectivity")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("CONNECTED"))
        .andExpect(jsonPath("$.message").value("Successfully connected to Auth0 Management API"))
        .andExpect(jsonPath("$.test_method").value("grants_endpoint"))
        .andExpect(jsonPath("$.timestamp").exists());
  }

  @SuppressWarnings("unchecked")
  @Test
  void testConnectivityWhenAuth0ExceptionShouldReturnConnectionFailed() throws Exception {
    // Given
    GrantsEntity grantsEntity = mock(GrantsEntity.class);
    Request requestMock = mock(Request.class);
    when(managementAPI.grants()).thenReturn(grantsEntity);
    when(grantsEntity.list(any(), any())).thenReturn(requestMock);
    when(requestMock.execute()).thenThrow(new Auth0Exception("Auth0 connection failed"));

    // When & Then
    getMockMvc().perform(get("/api/v1/auth0/health/connectivity")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.status").value("CONNECTION_FAILED"))
        .andExpect(jsonPath("$.message").value("Failed to connect to Auth0: Auth0 connection failed"))
        .andExpect(jsonPath("$.error_details").value("Auth0 connection failed"))
        .andExpect(jsonPath("$.timestamp").exists());
  }

  @Test
  void testConnectivityWhenUnexpectedExceptionShouldReturnError() throws Exception {
    // Given
    GrantsEntity grantsEntity = mock(GrantsEntity.class);
    Request requestMock = mock(Request.class);
    when(managementAPI.grants()).thenReturn(grantsEntity);
    when(grantsEntity.list(any(), any())).thenReturn(requestMock);
    when(requestMock.execute()).thenThrow(new RuntimeException("Unexpected error"));

    // When & Then
    getMockMvc().perform(get("/api/v1/auth0/health/connectivity")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.status").value("ERROR"))
        .andExpect(jsonPath("$.message").value("Unexpected error: Unexpected error"))
        .andExpect(jsonPath("$.timestamp").exists());
  }

  @Test
  void healthCheckWhenFullyConfiguredAndConnectedShouldReturnHealthy() throws Exception {
    // Given
    when(auth0Properties.domain()).thenReturn("test.auth0.com");
    when(auth0Properties.apiToken()).thenReturn("test-token");
    when(auth0Properties.timeout()).thenReturn(5000);
    when(auth0Properties.audience()).thenReturn("test-audience");
    GrantsEntity grantsEntity = mock(GrantsEntity.class);
    Request requestMock = mock(Request.class);
    when(managementAPI.grants()).thenReturn(grantsEntity);
    when(grantsEntity.list(any(), any())).thenReturn(requestMock);
    when(requestMock.execute()).thenReturn(null);

    // When & Then
    getMockMvc().perform(get("/api/v1/auth0/health")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("HEALTHY"))
        .andExpect(jsonPath("$.message").value("Auth0 is properly configured and connected"))
        .andExpect(jsonPath("$.configuration.domain_configured").value(true))
        .andExpect(jsonPath("$.configuration.api_token_configured").value(true))
        .andExpect(jsonPath("$.configuration.fully_configured").value(true))
        .andExpect(jsonPath("$.connectivity.status").value("CONNECTED"))
        .andExpect(jsonPath("$.connectivity.test_method").value("grants_endpoint"))
        .andExpect(jsonPath("$.timestamp").exists());
  }

  @Test
  void healthCheckWhenIncompleteConfigurationShouldReturnIncompleteConfig() throws Exception {
    // Given
    when(auth0Properties.domain()).thenReturn("");
    when(auth0Properties.apiToken()).thenReturn("test-token");
    when(auth0Properties.timeout()).thenReturn(5000);
    when(auth0Properties.audience()).thenReturn("test-audience");

    // When & Then
    getMockMvc().perform(get("/api/v1/auth0/health")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("INCOMPLETE_CONFIG"))
        .andExpect(jsonPath("$.message").value("Auth0 configuration is incomplete - check domain and api_token properties"))
        .andExpect(jsonPath("$.configuration.domain_configured").value(false))
        .andExpect(jsonPath("$.configuration.api_token_configured").value(true))
        .andExpect(jsonPath("$.configuration.fully_configured").value(false))
        .andExpect(jsonPath("$.timestamp").exists());
  }

  @Test
  void healthCheckWhenConfiguredButConnectionFailsShouldReturnConnectivityError() throws Exception {
    // Given
    when(auth0Properties.domain()).thenReturn("test.auth0.com");
    when(auth0Properties.apiToken()).thenReturn("test-token");
    when(auth0Properties.timeout()).thenReturn(5000);
    when(auth0Properties.audience()).thenReturn("test-audience");
    GrantsEntity grantsEntity = mock(GrantsEntity.class);
    Request requestMock = mock(Request.class);
    when(managementAPI.grants()).thenReturn(grantsEntity);
    when(grantsEntity.list(any(), any())).thenReturn(requestMock);
    when(requestMock.execute()).thenThrow(new Auth0Exception("Connection failed"));

    // When & Then
    getMockMvc().perform(get("/api/v1/auth0/health")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("CONNECTIVITY_ERROR"))
        .andExpect(jsonPath("$.message").value("Auth0 is configured but connection failed - check API token and network connectivity"))
        .andExpect(jsonPath("$.configuration.domain_configured").value(true))
        .andExpect(jsonPath("$.configuration.api_token_configured").value(true))
        .andExpect(jsonPath("$.configuration.fully_configured").value(true))
        .andExpect(jsonPath("$.connectivity.status").value("CONNECTION_FAILED"))
        .andExpect(jsonPath("$.connectivity.error").value("Connection failed"))
        .andExpect(jsonPath("$.connectivity.error_details").value("Connection failed"))
        .andExpect(jsonPath("$.connectivity.test_method").value("grants_endpoint"))
        .andExpect(jsonPath("$.timestamp").exists());
  }

  @Test
  void healthCheckWhenExceptionOccursShouldReturnError() throws Exception {
    // Given
    when(auth0Properties.domain()).thenThrow(new RuntimeException("Test exception"));

    // When & Then
    getMockMvc().perform(get("/api/v1/auth0/health")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.status").value("ERROR"))
        .andExpect(jsonPath("$.message").value("Health check failed with unexpected error: Test exception"))
        .andExpect(jsonPath("$.timestamp").exists());
  }
} 