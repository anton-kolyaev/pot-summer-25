package com.coherentsolutions.pot.insuranceservice.integration.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.coherentsolutions.pot.insuranceservice.dto.auth0.PasswordChangeRequestDto;
import com.coherentsolutions.pot.insuranceservice.integration.containers.PostgresTestContainer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Integration tests for Auth0PasswordController.
 * Note: These tests require Auth0 to be properly configured or mocked at the HTTP level.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class Auth0PasswordControllerIt extends PostgresTestContainer {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  void changePasswordWhenEmailIsInvalidReturnsBadRequest() throws Exception {
    // Given
    String requestBody = objectMapper.writeValueAsString(new PasswordChangeRequestDto("invalid-email"));

    // When & Then
    mockMvc.perform(post("/api/v1/auth/change-password")
            .content(requestBody)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }

  @Test
  void changePasswordWhenEmailIsMissingReturnsBadRequest() throws Exception {
    // Given
    String requestBody = "{\"email\": \"\"}";

    // When & Then
    mockMvc.perform(post("/api/v1/auth/change-password")
            .content(requestBody)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }

  @Test
  void changePasswordWhenEmailIsNullReturnsBadRequest() throws Exception {
    // Given
    String requestBody = "{\"email\": null}";

    // When & Then
    mockMvc.perform(post("/api/v1/auth/change-password")
            .content(requestBody)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }

  @Test
  void changePasswordWhenRequestBodyIsInvalidReturnsBadRequest() throws Exception {
    // Given
    String requestBody = "{\"invalid\": \"field\"}";

    // When & Then
    mockMvc.perform(post("/api/v1/auth/change-password")
            .content(requestBody)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }
}
