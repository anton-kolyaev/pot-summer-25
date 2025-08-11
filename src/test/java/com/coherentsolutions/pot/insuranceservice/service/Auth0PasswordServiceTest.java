package com.coherentsolutions.pot.insuranceservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.coherentsolutions.pot.insuranceservice.config.Auth0Properties;
import com.coherentsolutions.pot.insuranceservice.exception.Auth0Exception;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
class Auth0PasswordServiceTest {

  @Mock
  private RestTemplate restTemplate;

  @Mock
  private Auth0Properties auth0Properties;

  private Auth0PasswordService auth0PasswordService;

  @BeforeEach
  void setUp() {
    when(auth0Properties.enabled()).thenReturn(true);
    when(auth0Properties.domain()).thenReturn("test-domain.auth0.com");
    when(auth0Properties.clientId()).thenReturn("test-client-id");
    when(auth0Properties.connection()).thenReturn("Username-Password-Authentication");
    when(auth0Properties.timeout()).thenReturn(10000);

    auth0PasswordService = new Auth0PasswordService(auth0Properties, restTemplate);
  }

  @Test
  void sendPasswordChangeEmailSuccess() {
    // Given
    String userEmail = "test@example.com";
    String expectedResponse = "We've just sent you an email to change your password.";
    ResponseEntity<String> responseEntity = new ResponseEntity<>(expectedResponse, HttpStatus.OK);

    when(restTemplate.postForEntity(anyString(), any(), eq(String.class)))
        .thenReturn(responseEntity);

    // When
    String result = auth0PasswordService.sendPasswordChangeEmail(userEmail);

    // Then
    assertEquals(expectedResponse, result);
  }

  @Test
  void sendPasswordChangeEmailWhenAuth0DisabledThrowsException() {
    // Given
    when(auth0Properties.enabled()).thenReturn(false);
    String userEmail = "test@example.com";

    // When & Then
    Auth0Exception exception = assertThrows(Auth0Exception.class,
        () -> auth0PasswordService.sendPasswordChangeEmail(userEmail));
    assertEquals("Auth0 integration is disabled", exception.getMessage());
  }

  @Test
  void sendPasswordChangeEmailWhenRequestFailsThrowsException() {
    // Given
    String userEmail = "test@example.com";
    ResponseEntity<String> responseEntity = new ResponseEntity<>(HttpStatus.BAD_REQUEST);

    when(restTemplate.postForEntity(anyString(), any(), eq(String.class)))
        .thenReturn(responseEntity);

    // When & Then
    Auth0Exception exception = assertThrows(Auth0Exception.class,
        () -> auth0PasswordService.sendPasswordChangeEmail(userEmail));
    assertTrue(exception.getMessage().contains("Failed to send change password email"));
  }

  @Test
  void sendPasswordChangeEmailWhenRestTemplateThrowsExceptionThrowsAuth0Exception() {
    // Given
    String userEmail = "test@example.com";
    RuntimeException restException = new RuntimeException("Network error");

    when(restTemplate.postForEntity(anyString(), any(), eq(String.class)))
        .thenThrow(restException);

    // When & Then
    Auth0Exception exception = assertThrows(Auth0Exception.class,
        () -> auth0PasswordService.sendPasswordChangeEmail(userEmail));
    assertTrue(exception.getMessage().contains("Error sending password change email"));
    assertEquals(restException, exception.getCause());
  }
}
