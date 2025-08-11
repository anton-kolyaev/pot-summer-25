package com.coherentsolutions.pot.insuranceservice.unit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.coherentsolutions.pot.insuranceservice.config.Auth0Properties;
import com.coherentsolutions.pot.insuranceservice.exception.Auth0Exception;
import com.coherentsolutions.pot.insuranceservice.service.Auth0PasswordService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
class Auth0PasswordServiceTest {

  @Mock
  private Auth0Properties auth0Properties;

  @Mock
  private RestTemplate restTemplate;

  private Auth0PasswordService auth0PasswordService;

  @BeforeEach
  void setUp() {
    auth0PasswordService = new Auth0PasswordService(auth0Properties, restTemplate);
  }

  @Test
  @DisplayName("Should send password change email successfully")
  void shouldSendPasswordChangeEmailSuccessfully() {
    // Given
    String userEmail = "test@example.com";
    String expectedResponse = "We've just sent you an email to change your password.";
    
    when(auth0Properties.enabled()).thenReturn(true);
    when(auth0Properties.domain()).thenReturn("test-domain.auth0.com");
    when(auth0Properties.clientId()).thenReturn("test-client-id");
    when(auth0Properties.connection()).thenReturn("Username-Password-Authentication");
    
    ResponseEntity<String> mockResponse = new ResponseEntity<>(expectedResponse, HttpStatus.OK);
    when(restTemplate.postForEntity(
        eq("https://test-domain.auth0.com/dbconnections/change_password"),
        any(HttpEntity.class),
        eq(String.class)
    )).thenReturn(mockResponse);

    // When
    String result = auth0PasswordService.sendPasswordChangeEmail(userEmail);

    // Then
    assertEquals(expectedResponse, result);
  }

  @Test
  @DisplayName("Should throw Auth0Exception when Auth0 is disabled")
  void shouldThrowAuth0ExceptionWhenAuth0IsDisabled() {
    // Given
    String userEmail = "test@example.com";
    when(auth0Properties.enabled()).thenReturn(false);

    // When & Then
    Auth0Exception exception = assertThrows(Auth0Exception.class, 
        () -> auth0PasswordService.sendPasswordChangeEmail(userEmail));
    assertEquals("Auth0 integration is disabled", exception.getMessage());
  }

  @Test
  @DisplayName("Should throw Auth0Exception when HTTP response is not OK")
  void shouldThrowAuth0ExceptionWhenHttpResponseIsNotOk() {
    // Given
    String userEmail = "test@example.com";
    
    when(auth0Properties.enabled()).thenReturn(true);
    when(auth0Properties.domain()).thenReturn("test-domain.auth0.com");
    when(auth0Properties.clientId()).thenReturn("test-client-id");
    when(auth0Properties.connection()).thenReturn("Username-Password-Authentication");
    
    ResponseEntity<String> mockResponse = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    when(restTemplate.postForEntity(
        eq("https://test-domain.auth0.com/dbconnections/change_password"),
        any(HttpEntity.class),
        eq(String.class)
    )).thenReturn(mockResponse);

    // When & Then
    Auth0Exception exception = assertThrows(Auth0Exception.class, 
        () -> auth0PasswordService.sendPasswordChangeEmail(userEmail));
    assertEquals("Error sending password change email: Failed to send change password email: 400 BAD_REQUEST", exception.getMessage());
  }

  @Test
  @DisplayName("Should throw Auth0Exception when RestTemplate throws exception")
  void shouldThrowAuth0ExceptionWhenRestTemplateThrowsException() {
    // Given
    String userEmail = "test@example.com";
    String errorMessage = "Connection timeout";
    
    when(auth0Properties.enabled()).thenReturn(true);
    when(auth0Properties.domain()).thenReturn("test-domain.auth0.com");
    when(auth0Properties.clientId()).thenReturn("test-client-id");
    when(auth0Properties.connection()).thenReturn("Username-Password-Authentication");
    
    when(restTemplate.postForEntity(
        eq("https://test-domain.auth0.com/dbconnections/change_password"),
        any(HttpEntity.class),
        eq(String.class)
    )).thenThrow(new RestClientException(errorMessage));

    // When & Then
    Auth0Exception exception = assertThrows(Auth0Exception.class, 
        () -> auth0PasswordService.sendPasswordChangeEmail(userEmail));
    assertEquals("Error sending password change email: " + errorMessage, exception.getMessage());
  }

  @Test
  @DisplayName("Should throw Auth0Exception when RestTemplate throws generic exception")
  void shouldThrowAuth0ExceptionWhenRestTemplateThrowsGenericException() {
    // Given
    String userEmail = "test@example.com";
    String errorMessage = "Unexpected error";
    
    when(auth0Properties.enabled()).thenReturn(true);
    when(auth0Properties.domain()).thenReturn("test-domain.auth0.com");
    when(auth0Properties.clientId()).thenReturn("test-client-id");
    when(auth0Properties.connection()).thenReturn("Username-Password-Authentication");
    
    when(restTemplate.postForEntity(
        eq("https://test-domain.auth0.com/dbconnections/change_password"),
        any(HttpEntity.class),
        eq(String.class)
    )).thenThrow(new RuntimeException(errorMessage));

    // When & Then
    Auth0Exception exception = assertThrows(Auth0Exception.class, 
        () -> auth0PasswordService.sendPasswordChangeEmail(userEmail));
    assertEquals("Error sending password change email: " + errorMessage, exception.getMessage());
  }

  @Test
  @DisplayName("Should construct correct URL with domain")
  void shouldConstructCorrectUrlWithDomain() {
    // Given
    final String userEmail = "test@example.com";
    final String domain = "custom-domain.auth0.com";
    
    when(auth0Properties.enabled()).thenReturn(true);
    when(auth0Properties.domain()).thenReturn(domain);
    when(auth0Properties.clientId()).thenReturn("test-client-id");
    when(auth0Properties.connection()).thenReturn("Username-Password-Authentication");
    
    ResponseEntity<String> mockResponse = new ResponseEntity<>("Success", HttpStatus.OK);
    when(restTemplate.postForEntity(
        eq("https://" + domain + "/dbconnections/change_password"),
        any(HttpEntity.class),
        eq(String.class)
    )).thenReturn(mockResponse);

    // When
    auth0PasswordService.sendPasswordChangeEmail(userEmail);

    // Then - verify the correct URL was constructed and called
    // The verification is implicit in the mock setup above
  }
}
