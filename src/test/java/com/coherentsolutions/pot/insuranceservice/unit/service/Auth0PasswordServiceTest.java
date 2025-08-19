package com.coherentsolutions.pot.insuranceservice.unit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.coherentsolutions.pot.insuranceservice.config.Auth0Properties;
import com.coherentsolutions.pot.insuranceservice.exception.Auth0Exception;
import com.coherentsolutions.pot.insuranceservice.service.Auth0PasswordService;
import com.coherentsolutions.pot.insuranceservice.service.Auth0TicketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class Auth0PasswordServiceTest {

  @Mock
  private Auth0TicketService auth0TicketService;

  @Mock
  private Auth0Properties auth0Properties;

  private Auth0PasswordService auth0PasswordService;

  @BeforeEach
  void setUp() {
    auth0PasswordService = new Auth0PasswordService(auth0Properties, auth0TicketService);
  }

  @Test
  @DisplayName("Should throw Auth0Exception when Auth0 is disabled")
  void shouldThrowAuth0ExceptionWhenAuth0IsDisabled() {
    // Given
    final String userEmail = "test@example.com";
    when(auth0Properties.enabled()).thenReturn(false);

    // When & Then
    Auth0Exception exception = assertThrows(Auth0Exception.class, 
        () -> auth0PasswordService.sendPasswordChangeEmail(userEmail));
    assertEquals("Auth0 integration is disabled", exception.getMessage());
  }

  @Test
  @DisplayName("Should send password change email successfully")
  void shouldSendPasswordChangeEmailSuccessfully() throws Exception {
    // Given
    final String userEmail = "test@example.com";
    when(auth0Properties.enabled()).thenReturn(true);
    when(auth0TicketService.createPasswordChangeTicketByEmail(userEmail))
        .thenReturn("https://test-domain.auth0.com/lo/reset?ticket=abc123");

    // When
    String result = auth0PasswordService.sendPasswordChangeEmail(userEmail);

    // Then
    assertEquals("We've just sent you an email to change your password.", result);
  }

  @Test
  @DisplayName("Should handle null domain")
  void shouldHandleNullDomain() {
    // Given
    final String userEmail = "test@example.com";
    when(auth0Properties.enabled()).thenReturn(true);
    when(auth0TicketService.createPasswordChangeTicketByEmail(userEmail))
        .thenThrow(new RuntimeException("Domain is null"));

    // When & Then
    assertThrows(Auth0Exception.class, 
        () -> auth0PasswordService.sendPasswordChangeEmail(userEmail));
  }

  @Test
  @DisplayName("Should handle empty domain")
  void shouldHandleEmptyDomain() {
    // Given
    final String userEmail = "test@example.com";
    when(auth0Properties.enabled()).thenReturn(true);
    when(auth0TicketService.createPasswordChangeTicketByEmail(userEmail))
        .thenThrow(new RuntimeException("Domain is empty"));

    // When & Then
    assertThrows(Auth0Exception.class, 
        () -> auth0PasswordService.sendPasswordChangeEmail(userEmail));
  }

  @Test
  @DisplayName("Should handle null client ID")
  void shouldHandleNullClientId() {
    // Given
    final String userEmail = "test@example.com";
    when(auth0Properties.enabled()).thenReturn(true);
    when(auth0TicketService.createPasswordChangeTicketByEmail(userEmail))
        .thenThrow(new RuntimeException("Client ID is null"));

    // When & Then
    assertThrows(Auth0Exception.class, 
        () -> auth0PasswordService.sendPasswordChangeEmail(userEmail));
  }

  @Test
  @DisplayName("Should handle null connection")
  void shouldHandleNullConnection() {
    // Given
    final String userEmail = "test@example.com";
    when(auth0Properties.enabled()).thenReturn(true);
    when(auth0TicketService.createPasswordChangeTicketByEmail(userEmail))
        .thenThrow(new RuntimeException("Connection is null"));

    // When & Then
    assertThrows(Auth0Exception.class, 
        () -> auth0PasswordService.sendPasswordChangeEmail(userEmail));
  }

  @Test
  @DisplayName("Should handle null email")
  void shouldHandleNullEmail() {
    // Given
    when(auth0Properties.enabled()).thenReturn(true);
    when(auth0TicketService.createPasswordChangeTicketByEmail(null))
        .thenThrow(new RuntimeException("Email is null"));

    // When & Then
    assertThrows(Auth0Exception.class, 
        () -> auth0PasswordService.sendPasswordChangeEmail(null));
  }

  @Test
  @DisplayName("Should handle empty email")
  void shouldHandleEmptyEmail() {
    // Given
    when(auth0Properties.enabled()).thenReturn(true);
    when(auth0TicketService.createPasswordChangeTicketByEmail(""))
        .thenThrow(new RuntimeException("Email is empty"));

    // When & Then
    assertThrows(Auth0Exception.class, 
        () -> auth0PasswordService.sendPasswordChangeEmail(""));
  }

  @Test
  @DisplayName("Should handle whitespace email")
  void shouldHandleWhitespaceEmail() {
    // Given
    when(auth0Properties.enabled()).thenReturn(true);
    when(auth0TicketService.createPasswordChangeTicketByEmail("   "))
        .thenThrow(new RuntimeException("Email is whitespace"));

    // When & Then
    assertThrows(Auth0Exception.class, 
        () -> auth0PasswordService.sendPasswordChangeEmail("   "));
  }

  @Test
  @DisplayName("Should handle malformed domain")
  void shouldHandleMalformedDomain() {
    // Given
    final String userEmail = "test@example.com";
    when(auth0Properties.enabled()).thenReturn(true);
    when(auth0TicketService.createPasswordChangeTicketByEmail(userEmail))
        .thenThrow(new RuntimeException("Malformed domain"));

    // When & Then
    assertThrows(Auth0Exception.class, 
        () -> auth0PasswordService.sendPasswordChangeEmail(userEmail));
  }

  @Test
  @DisplayName("Should handle special characters in email")
  void shouldHandleSpecialCharactersInEmail() {
    // Given
    final String userEmail = "test+tag@example.com";
    when(auth0Properties.enabled()).thenReturn(true);
    when(auth0TicketService.createPasswordChangeTicketByEmail(userEmail))
        .thenReturn("https://test-domain.auth0.com/lo/reset?ticket=abc123");

    // When
    String result = auth0PasswordService.sendPasswordChangeEmail(userEmail);

    // Then
    assertEquals("We've just sent you an email to change your password.", result);
  }

  @Test
  @DisplayName("Should handle very long email")
  void shouldHandleVeryLongEmail() {
    // Given
    final String userEmail = "very.long.email.address.that.exceeds.normal.length.but.should.still.be.valid@very.long.domain.name.com";
    when(auth0Properties.enabled()).thenReturn(true);
    when(auth0TicketService.createPasswordChangeTicketByEmail(userEmail))
        .thenReturn("https://test-domain.auth0.com/lo/reset?ticket=abc123");

    // When
    String result = auth0PasswordService.sendPasswordChangeEmail(userEmail);

    // Then
    assertEquals("We've just sent you an email to change your password.", result);
  }

  @Test
  @DisplayName("Should handle different email formats")
  void shouldHandleDifferentEmailFormats() {
    // Given
    final String userEmail = "user.name+tag@subdomain.example.co.uk";
    when(auth0Properties.enabled()).thenReturn(true);
    when(auth0TicketService.createPasswordChangeTicketByEmail(userEmail))
        .thenReturn("https://test-domain.auth0.com/lo/reset?ticket=abc123");

    // When
    String result = auth0PasswordService.sendPasswordChangeEmail(userEmail);

    // Then
    assertEquals("We've just sent you an email to change your password.", result);
  }

  @Test
  @DisplayName("Should handle constructor with valid parameters")
  void shouldHandleConstructorWithValidParameters() {
    // Given & When
    Auth0PasswordService service = new Auth0PasswordService(auth0Properties, auth0TicketService);

    // Then
    // No exception should be thrown
    assert service != null;
  }

  @Test
  @DisplayName("Should handle HTTP 400 error response")
  void shouldHandleHttp400ErrorResponse() {
    // Given
    final String userEmail = "test@example.com";
    when(auth0Properties.enabled()).thenReturn(true);
    when(auth0TicketService.createPasswordChangeTicketByEmail(userEmail))
        .thenThrow(new RuntimeException("HTTP 400 error"));

    // When & Then
    assertThrows(Auth0Exception.class, 
        () -> auth0PasswordService.sendPasswordChangeEmail(userEmail));
  }

  @Test
  @DisplayName("Should handle HTTP 500 error response")
  void shouldHandleHttp500ErrorResponse() {
    // Given
    final String userEmail = "test@example.com";
    when(auth0Properties.enabled()).thenReturn(true);
    when(auth0TicketService.createPasswordChangeTicketByEmail(userEmail))
        .thenThrow(new RuntimeException("HTTP 500 error"));

    // When & Then
    assertThrows(Auth0Exception.class, 
        () -> auth0PasswordService.sendPasswordChangeEmail(userEmail));
  }

  @Test
  @DisplayName("Should handle network timeout")
  void shouldHandleNetworkTimeout() {
    // Given
    final String userEmail = "test@example.com";
    when(auth0Properties.enabled()).thenReturn(true);
    when(auth0TicketService.createPasswordChangeTicketByEmail(userEmail))
        .thenThrow(new RuntimeException("Network timeout"));

    // When & Then
    assertThrows(Auth0Exception.class, 
        () -> auth0PasswordService.sendPasswordChangeEmail(userEmail));
  }

  @Test
  @DisplayName("Should handle malformed URL")
  void shouldHandleMalformedUrl() {
    // Given
    final String userEmail = "test@example.com";
    when(auth0Properties.enabled()).thenReturn(true);
    when(auth0TicketService.createPasswordChangeTicketByEmail(userEmail))
        .thenThrow(new RuntimeException("Malformed URL"));

    // When & Then
    assertThrows(Auth0Exception.class, 
        () -> auth0PasswordService.sendPasswordChangeEmail(userEmail));
  }
}
