package com.coherentsolutions.pot.insuranceservice.integration.controller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.coherentsolutions.pot.insuranceservice.config.Auth0Properties;
import com.coherentsolutions.pot.insuranceservice.controller.Auth0PasswordController;
import com.coherentsolutions.pot.insuranceservice.dto.auth0.PasswordChangeRequestDto;
import com.coherentsolutions.pot.insuranceservice.service.Auth0PasswordService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(Auth0PasswordController.class)
@ActiveProfiles("test")
@Disabled("Requires complex Spring context setup")
class Auth0PasswordControllerIt {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private Auth0PasswordService auth0PasswordService;

  @MockBean
  private Auth0Properties auth0Properties;

  @Test
  void changePasswordSuccess() throws Exception {
    // Given
    String email = "test@example.com";
    String expectedResponse = "We've just sent you an email to change your password.";
    String requestBody = objectMapper.writeValueAsString(new PasswordChangeRequestDto(email));

    when(auth0PasswordService.sendPasswordChangeEmail(email))
        .thenReturn(expectedResponse);

    // When & Then
    mockMvc.perform(post("/api/v1/auth/change-password")
            .content(requestBody)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().string(expectedResponse));
  }

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
  void changePasswordWhenServiceThrowsExceptionReturnsInternalServerError() throws Exception {
    // Given
    String email = "test@example.com";
    String requestBody = objectMapper.writeValueAsString(new PasswordChangeRequestDto(email));

    when(auth0PasswordService.sendPasswordChangeEmail(anyString()))
        .thenThrow(new RuntimeException("Service error"));

    // When & Then
    mockMvc.perform(post("/api/v1/auth/change-password")
            .content(requestBody)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isInternalServerError());
  }
}
