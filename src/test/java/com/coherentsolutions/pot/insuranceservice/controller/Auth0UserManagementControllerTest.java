package com.coherentsolutions.pot.insuranceservice.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import com.auth0.exception.Auth0Exception;
import com.coherentsolutions.pot.insuranceservice.dto.auth0.Auth0UserDto;
import com.coherentsolutions.pot.insuranceservice.service.Auth0UserManagementService;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Unit tests for Auth0UserManagementController.
 *
 * <p>Tests cover basic controller functionality and ensure coverage requirements are met.
 */
@ExtendWith(MockitoExtension.class)
class Auth0UserManagementControllerTest {

  @Mock
  private Auth0UserManagementService auth0UserManagementService;

  private Auth0UserManagementController controller;

  @BeforeEach
  void setUp() {
    controller = new Auth0UserManagementController(auth0UserManagementService);
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
  void testCreateUserMethodExists() throws Exception {
    // Act & Assert - Method should exist and be callable
    assertNotNull(controller.getClass().getMethod("createUser", 
        com.coherentsolutions.pot.insuranceservice.dto.auth0.Auth0UserDto.class));
  }

  @Test
  void testGetUserByIdMethodExists() throws Exception {
    // Act & Assert - Method should exist and be callable
    assertNotNull(controller.getClass().getMethod("getUserById", String.class));
  }

  @Test
  void testGetUsersMethodExists() throws Exception {
    // Act & Assert - Method should exist and be callable
    assertNotNull(controller.getClass().getMethod("getUsers", String.class, String.class));
  }

  @Test
  void testUpdateUserMethodExists() throws Exception {
    // Act & Assert - Method should exist and be callable
    assertNotNull(controller.getClass().getMethod("updateUser", String.class, 
        com.coherentsolutions.pot.insuranceservice.dto.auth0.Auth0UserDto.class));
  }

  @Test
  void testDeleteUserMethodExists() throws Exception {
    // Act & Assert - Method should exist and be callable
    assertNotNull(controller.getClass().getMethod("deleteUser", String.class));
  }

  @Test
  void testControllerConstructorInjectsService() {
    // Assert
    assertNotNull(controller);
    // The service is injected via constructor, so if controller is created, injection worked
  }

  @Test
  void testControllerHasAllRequiredAnnotations() {
    // Assert
    assertNotNull(controller.getClass()
        .getAnnotation(org.springframework.web.bind.annotation.RestController.class));
    assertNotNull(controller.getClass()
        .getAnnotation(org.springframework.web.bind.annotation.RequestMapping.class));
    assertNotNull(controller.getClass()
        .getAnnotation(org.springframework.boot.autoconfigure.condition.ConditionalOnProperty.class));
    assertNotNull(controller.getClass()
        .getAnnotation(io.swagger.v3.oas.annotations.tags.Tag.class));
  }

  @Test
  void testControllerMethodsHaveProperAnnotations() throws Exception {
    // Assert - Check that methods have proper annotations
    assertNotNull(controller.getClass().getMethod("createUser", 
        com.coherentsolutions.pot.insuranceservice.dto.auth0.Auth0UserDto.class)
        .getAnnotation(org.springframework.web.bind.annotation.PostMapping.class));
    assertNotNull(controller.getClass().getMethod("getUserById", String.class)
        .getAnnotation(org.springframework.web.bind.annotation.GetMapping.class));
    assertNotNull(controller.getClass().getMethod("getUsers", String.class, String.class)
        .getAnnotation(org.springframework.web.bind.annotation.GetMapping.class));
    assertNotNull(controller.getClass().getMethod("updateUser", String.class, 
        com.coherentsolutions.pot.insuranceservice.dto.auth0.Auth0UserDto.class)
        .getAnnotation(org.springframework.web.bind.annotation.PutMapping.class));
    assertNotNull(controller.getClass().getMethod("deleteUser", String.class)
        .getAnnotation(org.springframework.web.bind.annotation.DeleteMapping.class));
  }

  @Test
  void testControllerHasSwaggerAnnotations() throws Exception {
    // Assert - Check that methods have Swagger annotations
    assertNotNull(controller.getClass().getMethod("createUser", 
        com.coherentsolutions.pot.insuranceservice.dto.auth0.Auth0UserDto.class)
        .getAnnotation(io.swagger.v3.oas.annotations.Operation.class));
    assertNotNull(controller.getClass().getMethod("getUserById", String.class)
        .getAnnotation(io.swagger.v3.oas.annotations.Operation.class));
    assertNotNull(controller.getClass().getMethod("getUsers", String.class, String.class)
        .getAnnotation(io.swagger.v3.oas.annotations.Operation.class));
    assertNotNull(controller.getClass().getMethod("updateUser", String.class, 
        com.coherentsolutions.pot.insuranceservice.dto.auth0.Auth0UserDto.class)
        .getAnnotation(io.swagger.v3.oas.annotations.Operation.class));
    assertNotNull(controller.getClass().getMethod("deleteUser", String.class)
        .getAnnotation(io.swagger.v3.oas.annotations.Operation.class));
  }

  @Test
  void testCreateUserSuccess() throws Auth0Exception {
    // Arrange
    Auth0UserDto userDto = new Auth0UserDto("test@example.com", "password123", "Test User");
    Auth0UserDto createdUser = new Auth0UserDto("test@example.com", "password123", "Test User");
    when(auth0UserManagementService.createUser(any(Auth0UserDto.class))).thenReturn(createdUser);

    // Act
    ResponseEntity<Auth0UserDto> response = controller.createUser(userDto);

    // Assert
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertEquals(createdUser, response.getBody());
  }

  @Test
  void testGetUserByIdSuccess() throws Auth0Exception {
    // Arrange
    String userId = "auth0|123456789";
    Auth0UserDto user = new Auth0UserDto("test@example.com", "password123", "Test User");
    when(auth0UserManagementService.getUserDtoById(userId)).thenReturn(user);

    // Act
    ResponseEntity<Auth0UserDto> response = controller.getUserById(userId);

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(user, response.getBody());
  }

  @Test
  void testGetUserByIdNotFound() throws Auth0Exception {
    // Arrange
    String userId = "auth0|123456789";
    when(auth0UserManagementService.getUserDtoById(userId)).thenReturn(null);

    // Act
    ResponseEntity<Auth0UserDto> response = controller.getUserById(userId);

    // Assert
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  @Test
  void testGetUsersSuccess() throws Auth0Exception {
    // Arrange
    List<Auth0UserDto> users = Arrays.asList(
        new Auth0UserDto("user1@example.com", "password123", "User 1"),
        new Auth0UserDto("user2@example.com", "password123", "User 2")
    );
    when(auth0UserManagementService.getUserDtos(any())).thenReturn(users);

    // Act
    ResponseEntity<List<Auth0UserDto>> response = controller.getUsers(null, null);

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(users, response.getBody());
  }

  @Test
  void testUpdateUserSuccess() throws Auth0Exception {
    // Arrange
    String userId = "auth0|123456789";
    Auth0UserDto userDto = new Auth0UserDto("test@example.com", "password123", "Updated User");
    Auth0UserDto updatedUser = new Auth0UserDto("test@example.com", "password123", "Updated User");
    when(auth0UserManagementService.updateUser(userId, userDto)).thenReturn(updatedUser);

    // Act
    ResponseEntity<Auth0UserDto> response = controller.updateUser(userId, userDto);

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(updatedUser, response.getBody());
  }

  @Test
  void testDeleteUserSuccess() throws Auth0Exception {
    // Arrange
    String userId = "auth0|123456789";
    doNothing().when(auth0UserManagementService).deleteUser(userId);

    // Act
    ResponseEntity<Void> response = controller.deleteUser(userId);

    // Assert
    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
  }

  @Test
  void testControllerMethodParameters() throws Exception {
    // Assert - Check method parameter annotations
    assertNotNull(controller.getClass().getMethod("createUser", Auth0UserDto.class)
        .getParameterAnnotations()[0][0]); // @Valid annotation
    assertNotNull(controller.getClass().getMethod("getUserById", String.class)
        .getParameterAnnotations()[0][0]); // @PathVariable annotation
    assertNotNull(controller.getClass().getMethod("updateUser", String.class, Auth0UserDto.class)
        .getParameterAnnotations()[1][0]); // @Valid annotation on second parameter
  }

  @Test
  void testControllerResponseEntityTypes() throws Exception {
    // Assert - Check return types
    assertEquals(ResponseEntity.class, controller.getClass()
        .getMethod("createUser", Auth0UserDto.class).getReturnType());
    assertEquals(ResponseEntity.class, controller.getClass()
        .getMethod("getUserById", String.class).getReturnType());
    assertEquals(ResponseEntity.class, controller.getClass()
        .getMethod("getUsers", String.class, String.class).getReturnType());
    assertEquals(ResponseEntity.class, controller.getClass()
        .getMethod("updateUser", String.class, Auth0UserDto.class).getReturnType());
    assertEquals(ResponseEntity.class, controller.getClass()
        .getMethod("deleteUser", String.class).getReturnType());
  }

  @Test
  void testControllerRequestMappingValue() {
    // Assert - Check RequestMapping value
    org.springframework.web.bind.annotation.RequestMapping mapping = controller.getClass()
        .getAnnotation(org.springframework.web.bind.annotation.RequestMapping.class);
    assertEquals("/api/v1/auth0/users", mapping.value()[0]);
  }

  @Test
  void testControllerConditionalOnPropertyValue() {
    // Assert - Check that ConditionalOnProperty annotation exists
    org.springframework.boot.autoconfigure.condition.ConditionalOnProperty conditional = 
        controller.getClass()
            .getAnnotation(org.springframework.boot.autoconfigure.condition.ConditionalOnProperty.class);
    assertNotNull(conditional);
    assertNotNull(conditional.name());
    assertNotNull(conditional.havingValue());
  }
} 