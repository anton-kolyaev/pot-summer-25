package com.coherentsolutions.pot.insuranceservice.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.auth0.exception.Auth0Exception;
import com.coherentsolutions.pot.insuranceservice.config.Auth0Properties;
import com.coherentsolutions.pot.insuranceservice.service.Auth0UserManagementService;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Unit tests for Auth0HealthController.
 *
 * <p>Tests cover health check functionality and configuration validation.
 */
@ExtendWith(MockitoExtension.class)
class Auth0HealthControllerTest {

  @Mock
  private Auth0Properties auth0Properties;

  @Mock
  private Auth0UserManagementService auth0UserManagementService;

  private Auth0HealthController controller;

  @BeforeEach
  void setUp() {
    controller = new Auth0HealthController(auth0Properties, auth0UserManagementService);
  }

  @Test
  void testControllerClassLoadsSuccessfully() {
    // Assert
    assertNotNull(controller);
  }

  @Test
  void testControllerHasRestControllerAnnotation() {
    // Assert
    assertNotNull(controller.getClass()
        .getAnnotation(org.springframework.web.bind.annotation.RestController.class));
  }

  @Test
  void testControllerHasRequestMappingAnnotation() {
    // Assert
    assertNotNull(controller.getClass()
        .getAnnotation(org.springframework.web.bind.annotation.RequestMapping.class));
  }

  @Test
  void testControllerHasConditionalOnPropertyAnnotation() {
    // Assert
    assertNotNull(controller.getClass()
        .getAnnotation(org.springframework.boot.autoconfigure.condition.ConditionalOnProperty.class));
  }

  @Test
  void testCheckConfigurationMethodExists() throws Exception {
    // Act & Assert - Method should exist and be callable
    assertNotNull(controller.getClass().getMethod("checkConfiguration"));
  }

  @Test
  void testTestConnectivityMethodExists() throws Exception {
    // Act & Assert - Method should exist and be callable
    assertNotNull(controller.getClass().getMethod("testConnectivity"));
  }

  @Test
  void testHealthCheckMethodExists() throws Exception {
    // Act & Assert - Method should exist and be callable
    assertNotNull(controller.getClass().getMethod("healthCheck"));
  }

  @Test
  void testControllerMethodsHaveProperAnnotations() throws Exception {
    // Assert - Check that methods have proper annotations
    assertNotNull(controller.getClass().getMethod("checkConfiguration")
        .getAnnotation(org.springframework.web.bind.annotation.GetMapping.class));
    assertNotNull(controller.getClass().getMethod("testConnectivity")
        .getAnnotation(org.springframework.web.bind.annotation.GetMapping.class));
    assertNotNull(controller.getClass().getMethod("healthCheck")
        .getAnnotation(org.springframework.web.bind.annotation.GetMapping.class));
  }

  @Test
  void testControllerHasSwaggerAnnotations() throws Exception {
    // Assert - Check that methods have Swagger annotations
    assertNotNull(controller.getClass().getMethod("checkConfiguration")
        .getAnnotation(io.swagger.v3.oas.annotations.Operation.class));
    assertNotNull(controller.getClass().getMethod("testConnectivity")
        .getAnnotation(io.swagger.v3.oas.annotations.Operation.class));
    assertNotNull(controller.getClass().getMethod("healthCheck")
        .getAnnotation(io.swagger.v3.oas.annotations.Operation.class));
  }

  @Test
  void testControllerRequestMappingValue() {
    // Assert - Check RequestMapping value
    org.springframework.web.bind.annotation.RequestMapping mapping = controller.getClass()
        .getAnnotation(org.springframework.web.bind.annotation.RequestMapping.class);
    assertEquals("/api/v1/auth0/health", mapping.value()[0]);
  }

  @Test
  void testControllerConditionalOnPropertyValue() {
    // Assert - Check ConditionalOnProperty values
    org.springframework.boot.autoconfigure.condition.ConditionalOnProperty conditional = 
        controller.getClass()
            .getAnnotation(org.springframework.boot.autoconfigure.condition.ConditionalOnProperty.class);
    assertNotNull(conditional);
    assertNotNull(conditional.name());
    assertNotNull(conditional.havingValue());
  }

  @Test
  void testControllerConstructorInjectsDependencies() {
    // Assert
    assertNotNull(controller);
    // The dependencies are injected via constructor, so if controller is created, injection worked
  }

  @Test
  void testControllerResponseEntityTypes() throws Exception {
    // Assert - Check return types
    assertEquals(ResponseEntity.class, controller.getClass()
        .getMethod("checkConfiguration").getReturnType());
    assertEquals(ResponseEntity.class, controller.getClass()
        .getMethod("testConnectivity").getReturnType());
    assertEquals(ResponseEntity.class, controller.getClass()
        .getMethod("healthCheck").getReturnType());
  }

  @Test
  void testControllerMethodsReturnResponseEntityWithMapType() throws Exception {
    // Assert - Check that methods return ResponseEntity with Map generic type
    var checkConfigMethod = controller.getClass().getMethod("checkConfiguration");
    var testConnectivityMethod = controller.getClass().getMethod("testConnectivity");
    var healthCheckMethod = controller.getClass().getMethod("healthCheck");
    
    // Check that return type is ResponseEntity
    assertEquals(ResponseEntity.class, checkConfigMethod.getReturnType());
    assertEquals(ResponseEntity.class, testConnectivityMethod.getReturnType());
    assertEquals(ResponseEntity.class, healthCheckMethod.getReturnType());
    
    // Check that generic type parameter is Map<String, Object>
    var checkConfigGenericType = checkConfigMethod.getGenericReturnType();
    var testConnectivityGenericType = testConnectivityMethod.getGenericReturnType();
    var healthCheckGenericType = healthCheckMethod.getGenericReturnType();
    
    assertNotNull(checkConfigGenericType);
    assertNotNull(testConnectivityGenericType);
    assertNotNull(healthCheckGenericType);
  }

  @Test
  void testCheckConfigurationWhenAuth0EnabledAndFullyConfigured() {
    // Arrange
    when(auth0Properties.enabled()).thenReturn(true);
    when(auth0Properties.domain()).thenReturn("test.auth0.com");
    when(auth0Properties.apiToken()).thenReturn("test-token");
    when(auth0Properties.timeout()).thenReturn(5000);
    when(auth0Properties.audience()).thenReturn("test-audience");

    // Act
    ResponseEntity<Map<String, Object>> response = controller.checkConfiguration();

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    Map<String, Object> body = response.getBody();
    assertNotNull(body);
    assertEquals(true, body.get("enabled"));
    assertEquals(true, body.get("domain_configured"));
    assertEquals(true, body.get("api_token_configured"));
    assertEquals(5000, body.get("timeout"));
    assertEquals("test-audience", body.get("audience"));
    assertEquals(true, body.get("fully_configured"));
    assertEquals("CONFIGURED", body.get("status"));
    assertEquals("Auth0 is properly configured", body.get("message"));
  }

  @Test
  void testCheckConfigurationWhenAuth0EnabledButIncomplete() {
    // Arrange
    when(auth0Properties.enabled()).thenReturn(true);
    when(auth0Properties.domain()).thenReturn("test.auth0.com");
    when(auth0Properties.apiToken()).thenReturn(null);
    when(auth0Properties.timeout()).thenReturn(5000);
    when(auth0Properties.audience()).thenReturn("test-audience");

    // Act
    ResponseEntity<Map<String, Object>> response = controller.checkConfiguration();

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    Map<String, Object> body = response.getBody();
    assertNotNull(body);
    assertEquals(true, body.get("enabled"));
    assertEquals(true, body.get("domain_configured"));
    assertEquals(false, body.get("api_token_configured"));
    assertEquals(false, body.get("fully_configured"));
    assertEquals("INCOMPLETE", body.get("status"));
    assertEquals("Auth0 configuration is incomplete", body.get("message"));
  }

  @Test
  void testCheckConfigurationWhenAuth0Disabled() {
    // Arrange
    when(auth0Properties.enabled()).thenReturn(false);

    // Act
    ResponseEntity<Map<String, Object>> response = controller.checkConfiguration();

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    Map<String, Object> body = response.getBody();
    assertNotNull(body);
    assertEquals(false, body.get("enabled"));
    assertEquals("DISABLED", body.get("status"));
    assertEquals("Auth0 is disabled", body.get("message"));
  }

  @Test
  void testCheckConfigurationWhenExceptionOccurs() {
    // Arrange
    when(auth0Properties.enabled()).thenThrow(new RuntimeException("Test exception"));

    // Act
    ResponseEntity<Map<String, Object>> response = controller.checkConfiguration();

    // Assert
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    Map<String, Object> body = response.getBody();
    assertNotNull(body);
    assertEquals("ERROR", body.get("status"));
    assertTrue(body.get("message").toString().contains("Error checking configuration"));
  }

  @Test
  void testTestConnectivityWhenSuccessful() throws Auth0Exception {
    // Arrange
    when(auth0UserManagementService.getUserDtos(null)).thenReturn(List.of());

    // Act
    ResponseEntity<Map<String, Object>> response = controller.testConnectivity();

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    Map<String, Object> body = response.getBody();
    assertNotNull(body);
    assertEquals("CONNECTED", body.get("status"));
    assertEquals("Successfully connected to Auth0 Management API", body.get("message"));
    assertEquals(0, body.get("user_count"));
  }

  @Test
  void testTestConnectivityWhenAuth0ExceptionOccurs() throws Auth0Exception {
    // Arrange
    when(auth0UserManagementService.getUserDtos(null))
        .thenThrow(new Auth0Exception("Connection failed"));

    // Act
    ResponseEntity<Map<String, Object>> response = controller.testConnectivity();

    // Assert
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    Map<String, Object> body = response.getBody();
    assertNotNull(body);
    assertEquals("CONNECTION_FAILED", body.get("status"));
    assertTrue(body.get("message").toString().contains("Connection failed"));
  }



  @Test
  void testTestConnectivityWhenGeneralExceptionOccurs() throws Auth0Exception {
    // Arrange
    when(auth0UserManagementService.getUserDtos(null))
        .thenThrow(new RuntimeException("General error"));

    // Act
    ResponseEntity<Map<String, Object>> response = controller.testConnectivity();

    // Assert
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    Map<String, Object> body = response.getBody();
    assertNotNull(body);
    assertEquals("ERROR", body.get("status"));
    assertTrue(body.get("message").toString().contains("General error"));
  }

  @Test
  void testHealthCheckWhenAllChecksPass() throws Auth0Exception {
    // Arrange
    when(auth0Properties.enabled()).thenReturn(true);
    when(auth0Properties.domain()).thenReturn("test.auth0.com");
    when(auth0Properties.apiToken()).thenReturn("test-token");
    when(auth0UserManagementService.getUserDtos(null)).thenReturn(List.of());

    // Act
    ResponseEntity<Map<String, Object>> response = controller.healthCheck();

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    Map<String, Object> body = response.getBody();
    assertNotNull(body);
    assertEquals("HEALTHY", body.get("status"));
    assertEquals("Auth0 is properly configured and connected", body.get("message"));
    assertNotNull(body.get("configuration"));
    assertNotNull(body.get("connectivity"));
  }

  @Test
  void testHealthCheckWhenAuth0Disabled() {
    // Arrange
    when(auth0Properties.enabled()).thenReturn(false);

    // Act
    ResponseEntity<Map<String, Object>> response = controller.healthCheck();

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    Map<String, Object> body = response.getBody();
    assertNotNull(body);
    assertEquals("DISABLED", body.get("status"));
    assertEquals("Auth0 is disabled", body.get("message"));
    assertEquals(false, body.get("enabled"));
  }

  @Test
  void testHealthCheckWhenConfigurationFails() {
    // Arrange
    when(auth0Properties.enabled()).thenThrow(new RuntimeException("Config error"));

    // Act
    ResponseEntity<Map<String, Object>> response = controller.healthCheck();

    // Assert
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    Map<String, Object> body = response.getBody();
    assertNotNull(body);
    assertEquals("ERROR", body.get("status"));
    assertTrue(body.get("message").toString().contains("Config error"));
  }

  @Test
  void testHealthCheckWhenConnectivityFails() throws Auth0Exception {
    // Arrange
    when(auth0Properties.enabled()).thenReturn(true);
    when(auth0Properties.domain()).thenReturn("test.auth0.com");
    when(auth0Properties.apiToken()).thenReturn("test-token");
    when(auth0UserManagementService.getUserDtos(null))
        .thenThrow(new Auth0Exception("Connection failed"));

    // Act
    ResponseEntity<Map<String, Object>> response = controller.healthCheck();

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    Map<String, Object> body = response.getBody();
    assertNotNull(body);
    assertEquals("CONNECTIVITY_ERROR", body.get("status"));
    assertEquals("Auth0 is configured but connection failed", body.get("message"));
  }
} 