package com.coherentsolutions.pot.insuranceservice.integration.controller;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.auth0.client.mgmt.ManagementAPI;
import com.auth0.client.mgmt.UsersEntity;
import com.auth0.json.mgmt.users.User;
import com.auth0.json.mgmt.users.UsersPage;
import com.auth0.net.Request;
import com.auth0.net.Response;
import com.coherentsolutions.pot.insuranceservice.dto.auth0.Auth0UserDto;
import com.coherentsolutions.pot.insuranceservice.integration.IntegrationTestConfiguration;
import com.coherentsolutions.pot.insuranceservice.integration.containers.PostgresTestContainer;
import com.coherentsolutions.pot.insuranceservice.mapper.Auth0UserMapper;
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
 * <p>These tests verify the full HTTP request/response cycle for all controller endpoints,
 * including proper mocking of the ManagementAPI external dependency.
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
  private ManagementAPI managementAPI;

  @MockBean
  private UsersPage usersPage;

  @MockBean
  private UsersEntity usersEntity;

  @Test
  @DisplayName("Should create user successfully")
  void shouldCreateUserSuccessfully() throws Exception {
    // Given
    Auth0UserDto createRequest = buildAuth0UserDto("test@example.com", "password123", "Test User");
    User mockUser = buildMockUser("auth0|123", "test@example.com", "Test User");
    Auth0UserDto expectedResponse = auth0UserMapper.toDto(mockUser);

    // Mock the ManagementAPI behavior using Request/Response pattern
    Request<User> userRequest = mock(Request.class);
    Response<User> userResponse = mock(Response.class);
    when(managementAPI.users()).thenReturn(usersEntity);
    when(usersEntity.create(any(User.class))).thenReturn(userRequest);
    when(userRequest.execute()).thenReturn(userResponse);
    when(userResponse.getBody()).thenReturn(mockUser);

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

    // Mock the ManagementAPI behavior using Request/Response pattern
    Request<User> userRequest = mock(Request.class);
    Response<User> userResponse = mock(Response.class);
    when(managementAPI.users()).thenReturn(usersEntity);
    when(usersEntity.get(eq(userId), eq(null))).thenReturn(userRequest);
    when(userRequest.execute()).thenReturn(userResponse);
    when(userResponse.getBody()).thenReturn(mockUser);

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

    // Mock the ManagementAPI to return null (user not found) using Request/Response pattern
    Request<User> userRequest = mock(Request.class);
    Response<User> userResponse = mock(Response.class);
    when(managementAPI.users()).thenReturn(usersEntity);
    when(usersEntity.get(eq(userId), eq(null))).thenReturn(userRequest);
    when(userRequest.execute()).thenReturn(userResponse);
    when(userResponse.getBody()).thenReturn(null);

    // When & Then
    mockMvc
        .perform(get("/api/v1/auth0/users/{userId}", userId).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("Should get all users successfully")
  void shouldGetAllUsersSuccessfully() throws Exception {
    // Given
    List<User> mockUsers = Arrays.asList(
        buildMockUser("auth0|1", "user1@example.com", "User One"),
        buildMockUser("auth0|2", "user2@example.com", "User Two")
    );

    // Mock the ManagementAPI behavior using Request/Response pattern
    Request<UsersPage> usersPageRequest = mock(Request.class);
    Response<UsersPage> usersPageResponse = mock(Response.class);
    when(managementAPI.users()).thenReturn(usersEntity);
    when(usersEntity.list(any())).thenReturn(usersPageRequest);
    when(usersPageRequest.execute()).thenReturn(usersPageResponse);
    when(usersPageResponse.getBody()).thenReturn(usersPage);
    when(usersPage.getItems()).thenReturn(mockUsers);

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

    // Mock the ManagementAPI behavior using Request/Response pattern
    Request<User> getUserRequest = mock(Request.class);
    Response<User> getUserResponse = mock(Response.class);
    Request<User> updateUserRequest = mock(Request.class);
    Response<User> updateUserResponse = mock(Response.class);
    when(managementAPI.users()).thenReturn(usersEntity);
    when(usersEntity.get(eq(userId), eq(null))).thenReturn(getUserRequest);
    when(getUserRequest.execute()).thenReturn(getUserResponse);
    when(getUserResponse.getBody()).thenReturn(mockUpdatedUser);
    when(usersEntity.update(eq(userId), any(User.class))).thenReturn(updateUserRequest);
    when(updateUserRequest.execute()).thenReturn(updateUserResponse);
    when(updateUserResponse.getBody()).thenReturn(mockUpdatedUser);

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

    // Mock the ManagementAPI to return null (user not found) using Request/Response pattern
    Request<User> userRequest = mock(Request.class);
    Response<User> userResponse = mock(Response.class);
    when(managementAPI.users()).thenReturn(usersEntity);
    when(usersEntity.get(eq(userId), eq(null))).thenReturn(userRequest);
    when(userRequest.execute()).thenReturn(userResponse);
    when(userResponse.getBody()).thenReturn(null);

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

    // Mock the ManagementAPI behavior using Request/Response pattern
    Request<Void> voidRequest = mock(Request.class);
    Response<Void> voidResponse = mock(Response.class);
    when(managementAPI.users()).thenReturn(usersEntity);
    when(usersEntity.delete(userId)).thenReturn(voidRequest);
    when(voidRequest.execute()).thenReturn(voidResponse);

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

    // Mock the ManagementAPI to throw exception using Request/Response pattern
    Request<Void> voidRequest = mock(Request.class);
    when(managementAPI.users()).thenReturn(usersEntity);
    when(usersEntity.delete(userId)).thenReturn(voidRequest);
    when(voidRequest.execute()).thenThrow(new com.auth0.exception.Auth0Exception("User not found"));

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
    Auth0UserDto createRequest = buildAuth0UserDto("test@example.com", "123", "Test User");

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
    Auth0UserDto updateRequest = buildAuth0UserDto("invalid-email", "newpassword123", "Updated User");

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
    String invalidJson = "{\"email\": \"test@example.com\", \"password\": \"password123\", \"name\": \"Test User\""; // Missing closing brace

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
    String invalidJson = "{\"email\": \"updated@example.com\", \"password\": \"newpassword123\", \"name\": \"Updated User\""; // Missing closing brace

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
    // Given
    String emptyBody = "";

    // When & Then
    mockMvc
        .perform(
            post("/api/v1/auth0/users")
                .content(emptyBody)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("Should return 400 when updating user with empty request body")
  void shouldReturn400WhenUpdatingUserWithEmptyBody() throws Exception {
    // Given
    String userId = "auth0|123";
    String emptyBody = "";

    // When & Then
    mockMvc
        .perform(
            put("/api/v1/auth0/users/{userId}", userId)
                .content(emptyBody)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("Should return 400 when creating user with null request body")
  void shouldReturn400WhenCreatingUserWithNullBody() throws Exception {
    // When & Then
    mockMvc
        .perform(
            post("/api/v1/auth0/users")
                .contentType(MediaType.APPLICATION_JSON))
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
            put("/api/v1/auth0/users/{userId}", userId)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("Should handle unsupported HTTP methods")
  void shouldHandleUnsupportedHttpMethods() throws Exception {
    // Given
    String userId = "auth0|123";

    // When & Then
    mockMvc
        .perform(
            post("/api/v1/auth0/users/{userId}", userId)
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
  }

  private Auth0UserDto buildAuth0UserDto(String email, String password, String name) {
    Auth0UserDto dto = new Auth0UserDto();
    dto.setEmail(email);
    dto.setPassword(password);
    dto.setName(name);
    return dto;
  }

  private User buildMockUser(String userId, String email, String name) {
    User user = new User();
    user.setId(userId);
    user.setEmail(email);
    user.setName(name);
    return user;
  }
} 