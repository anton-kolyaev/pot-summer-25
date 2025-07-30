package com.coherentsolutions.pot.insuranceservice.integration.controller;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.auth0.exception.Auth0Exception;
import com.auth0.json.mgmt.users.User;
import com.coherentsolutions.pot.insuranceservice.dto.auth0.Auth0UserDto;
import com.coherentsolutions.pot.insuranceservice.integration.IntegrationTestConfiguration;
import com.coherentsolutions.pot.insuranceservice.integration.containers.PostgresTestContainer;
import com.coherentsolutions.pot.insuranceservice.mapper.Auth0UserMapper;
import com.coherentsolutions.pot.insuranceservice.service.Auth0UserManagementService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for Auth0UserManagementController.
 * 
 * <p>Tests the controller endpoints with mocked ManagementAPI to avoid actual calls to Auth0.
 * This approach allows testing the full request/response cycle while controlling the external
 * service behavior.
 */
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
@Import(IntegrationTestConfiguration.class)
@DisplayName("Integration test for Auth0UserManagementController")
public class Auth0UserManagementControllerIt extends PostgresTestContainer {

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private Auth0UserMapper auth0UserMapper;

  @MockBean
  private Auth0UserManagementService auth0UserManagementService;

  @Test
  @DisplayName("Should create user successfully")
  void shouldCreateUserSuccessfully() throws Exception {
    // Given
    Auth0UserDto createRequest = buildAuth0UserDto("test@example.com", "password123", "Test User");
    User mockUser = buildMockUser("auth0|123", "test@example.com", "Test User");
    Auth0UserDto expectedResponse = auth0UserMapper.toDto(mockUser);

    // Mock the service behavior
    when(auth0UserManagementService.createUser(any(Auth0UserDto.class)))
        .thenReturn(expectedResponse);

    // When & Then
    mockMvc
        .perform(
            post("/api/v1/auth0/users")
                .content(objectMapper.writeValueAsString(createRequest))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isCreated())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.email").value("test@example.com"))
        .andExpect(jsonPath("$.name").value("Test User"));
  }

  @Test
  @DisplayName("Should get user by ID successfully")
  void shouldGetUserByIdSuccessfully() throws Exception {
    // Given
    String userId = "auth0|123";
    User mockUser = buildMockUser(userId, "test@example.com", "Test User");
    Auth0UserDto expectedResponse = auth0UserMapper.toDto(mockUser);

    // Mock the service behavior
    when(auth0UserManagementService.getUserDtoById(userId))
        .thenReturn(expectedResponse);

    // When & Then
    mockMvc
        .perform(get("/api/v1/auth0/users/{userId}", userId).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.user_id").value(userId))
        .andExpect(jsonPath("$.email").value("test@example.com"))
        .andExpect(jsonPath("$.name").value("Test User"));
  }

  @Test
  @DisplayName("Should return 404 when user not found")
  void shouldReturn404WhenUserNotFound() throws Exception {
    // Given
    String userId = "auth0|nonexistent";

    // Mock the service to return null (user not found)
    when(auth0UserManagementService.getUserDtoById(userId))
        .thenReturn(null);

    // When & Then
    mockMvc
        .perform(get("/api/v1/auth0/users/{userId}", userId).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("Should get all users successfully")
  void shouldGetAllUsersSuccessfully() throws Exception {
    // Given
    List<Auth0UserDto> mockUsers = Arrays.asList(
        auth0UserMapper.toDto(buildMockUser("auth0|1", "user1@example.com", "User One")),
        auth0UserMapper.toDto(buildMockUser("auth0|2", "user2@example.com", "User Two"))
    );

    // Mock the service behavior
    when(auth0UserManagementService.getUserDtos(any()))
        .thenReturn(mockUsers);

    // When & Then
    mockMvc
        .perform(get("/api/v1/auth0/users").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(2))
        .andExpect(jsonPath("$[*].email", containsInAnyOrder("user1@example.com", "user2@example.com")));
  }

  @Test
  @DisplayName("Should update user successfully")
  void shouldUpdateUserSuccessfully() throws Exception {
    // Given
    String userId = "auth0|123";
    Auth0UserDto updateRequest = buildAuth0UserDto("updated@example.com", "newpassword123", "Updated User");
    User mockUpdatedUser = buildMockUser(userId, "updated@example.com", "Updated User");
    Auth0UserDto expectedResponse = auth0UserMapper.toDto(mockUpdatedUser);

    // Mock the service behavior
    when(auth0UserManagementService.updateUser(eq(userId), any(Auth0UserDto.class)))
        .thenReturn(expectedResponse);

    // When & Then
    mockMvc
        .perform(
            put("/api/v1/auth0/users/{userId}", userId)
                .content(objectMapper.writeValueAsString(updateRequest))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.user_id").value(userId))
        .andExpect(jsonPath("$.email").value("updated@example.com"))
        .andExpect(jsonPath("$.name").value("Updated User"));
  }

  @Test
  @DisplayName("Should return 404 when updating non-existent user")
  void shouldReturn404WhenUpdatingNonExistentUser() throws Exception {
    // Given
    String userId = "auth0|nonexistent";
    Auth0UserDto updateRequest = buildAuth0UserDto("updated@example.com", "newpassword123", "Updated User");

    // Mock the service to throw Auth0Exception for non-existent user
    when(auth0UserManagementService.updateUser(eq(userId), any(Auth0UserDto.class)))
        .thenThrow(new Auth0Exception("User not found: " + userId));

    // When & Then
    mockMvc
        .perform(
            put("/api/v1/auth0/users/{userId}", userId)
                .content(objectMapper.writeValueAsString(updateRequest))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("Should delete user successfully")
  void shouldDeleteUserSuccessfully() throws Exception {
    // Given
    String userId = "auth0|123";

    // Mock the service behavior
    doNothing().when(auth0UserManagementService).deleteUser(userId);

    // When & Then
    mockMvc
        .perform(delete("/api/v1/auth0/users/{userId}", userId).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("Should return 404 when deleting non-existent user")
  void shouldReturn404WhenDeletingNonExistentUser() throws Exception {
    // Given
    String userId = "auth0|nonexistent";

    // Mock the service to throw Auth0Exception for non-existent user
    doThrow(new Auth0Exception("User not found: " + userId))
        .when(auth0UserManagementService).deleteUser(userId);

    // When & Then
    mockMvc
        .perform(delete("/api/v1/auth0/users/{userId}", userId).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("Should return 400 when creating user with invalid email")
  void shouldReturn400WhenCreatingUserWithInvalidEmail() throws Exception {
    // Given
    Auth0UserDto createRequest = buildAuth0UserDto("invalid-email", "password123", "Test User");

    // When & Then
    mockMvc
        .perform(
            post("/api/v1/auth0/users")
                .content(objectMapper.writeValueAsString(createRequest))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("Should return 400 when creating user with short password")
  void shouldReturn400WhenCreatingUserWithShortPassword() throws Exception {
    // Given
    Auth0UserDto createRequest = buildAuth0UserDto("test@example.com", "short", "Test User");

    // When & Then
    mockMvc
        .perform(
            post("/api/v1/auth0/users")
                .content(objectMapper.writeValueAsString(createRequest))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("Should return 400 when creating user with missing required fields")
  void shouldReturn400WhenCreatingUserWithMissingRequiredFields() throws Exception {
    // Given
    Auth0UserDto createRequest = new Auth0UserDto();
    createRequest.setEmail("test@example.com");
    // Missing password and name

    // When & Then
    mockMvc
        .perform(
            post("/api/v1/auth0/users")
                .content(objectMapper.writeValueAsString(createRequest))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("Should return 400 when updating user with invalid data")
  void shouldReturn400WhenUpdatingUserWithInvalidData() throws Exception {
    // Given
    String userId = "auth0|123";
    Auth0UserDto updateRequest = buildAuth0UserDto("invalid-email", "short", "Test User");

    // When & Then
    mockMvc
        .perform(
            put("/api/v1/auth0/users/{userId}", userId)
                .content(objectMapper.writeValueAsString(updateRequest))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("Should return 400 when creating user with invalid JSON")
  void shouldReturn400WhenCreatingUserWithInvalidJson() throws Exception {
    // Given
    String invalidJson = "{ invalid json }";

    // When & Then
    mockMvc
        .perform(
            post("/api/v1/auth0/users")
                .content(invalidJson)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("Should return 400 when updating user with invalid JSON")
  void shouldReturn400WhenUpdatingUserWithInvalidJson() throws Exception {
    // Given
    String userId = "auth0|123";
    String invalidJson = "{ invalid json }";

    // When & Then
    mockMvc
        .perform(
            put("/api/v1/auth0/users/{userId}", userId)
                .content(invalidJson)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("Should return 400 when creating user with empty request body")
  void shouldReturn400WhenCreatingUserWithEmptyBody() throws Exception {
    // When & Then
    mockMvc
        .perform(
            post("/api/v1/auth0/users")
                .content("")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("Should return 400 when updating user with empty request body")
  void shouldReturn400WhenUpdatingUserWithEmptyBody() throws Exception {
    // Given
    String userId = "auth0|123";

    // When & Then
    mockMvc
        .perform(
            put("/api/v1/auth0/users/{userId}", userId)
                .content("")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("Should return 400 when creating user with null request body")
  void shouldReturn400WhenCreatingUserWithNullBody() throws Exception {
    // When & Then
    mockMvc
        .perform(post("/api/v1/auth0/users").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("Should return 400 when updating user with null request body")
  void shouldReturn400WhenUpdatingUserWithNullBody() throws Exception {
    // Given
    String userId = "auth0|123";

    // When & Then
    mockMvc
        .perform(
            put("/api/v1/auth0/users/{userId}", userId).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("Should handle unsupported HTTP methods")
  void shouldHandleUnsupportedHttpMethods() throws Exception {
    // When & Then
    mockMvc
        .perform(
            org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch("/api/v1/auth0/users/auth0|123")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isMethodNotAllowed());
  }

  @Test
  @DisplayName("Should handle missing content type header")
  void shouldHandleMissingContentTypeHeader() throws Exception {
    // Given
    Auth0UserDto createRequest = buildAuth0UserDto("test@example.com", "password123", "Test User");

    // When & Then
    mockMvc
        .perform(
            post("/api/v1/auth0/users")
                .content(objectMapper.writeValueAsString(createRequest)))
        .andExpect(status().isUnsupportedMediaType());

    mockMvc
        .perform(
            put("/api/v1/auth0/users/auth0|123")
                .content(objectMapper.writeValueAsString(createRequest)))
        .andExpect(status().isUnsupportedMediaType());
  }

  @Test
  @DisplayName("Should handle unsupported content type")
  void shouldHandleUnsupportedContentType() throws Exception {
    // Given
    Auth0UserDto createRequest = buildAuth0UserDto("test@example.com", "password123", "Test User");

    // When & Then
    mockMvc
        .perform(
            post("/api/v1/auth0/users")
                .content(objectMapper.writeValueAsString(createRequest))
                .contentType(MediaType.TEXT_PLAIN))
        .andExpect(status().isUnsupportedMediaType());

    mockMvc
        .perform(
            put("/api/v1/auth0/users/auth0|123")
                .content(objectMapper.writeValueAsString(createRequest))
                .contentType(MediaType.TEXT_PLAIN))
        .andExpect(status().isUnsupportedMediaType());
  }

  // ========== PRIVATE HELPER METHODS ==========

  private Auth0UserDto buildAuth0UserDto(String email, String password, String name) {
    Auth0UserDto dto = new Auth0UserDto();
    dto.setEmail(email);
    dto.setPassword(password);
    dto.setName(name);
    dto.setConnection("Username-Password-Authentication");
    return dto;
  }

  private User buildMockUser(String userId, String email, String name) {
    User user = new User();
    user.setId(userId);
    user.setEmail(email);
    user.setName(name);
    user.setEmailVerified(false);
    user.setBlocked(false);
    return user;
  }
} 