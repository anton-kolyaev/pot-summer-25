package com.coherentsolutions.pot.insuranceservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.coherentsolutions.pot.insuranceservice.config.Auth0Properties;
import com.coherentsolutions.pot.insuranceservice.exception.Auth0Exception;
import com.coherentsolutions.pot.insuranceservice.service.Auth0PasswordService;
import com.coherentsolutions.pot.insuranceservice.service.Auth0TicketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class Auth0PasswordServiceTest {

  @Mock
  private Auth0Properties auth0Properties;

  @Mock
  private Auth0TicketService auth0TicketService;

  private Auth0PasswordService auth0PasswordService;

  @BeforeEach
  void setUp() {
    when(auth0Properties.enabled()).thenReturn(true);
    when(auth0Properties.domain()).thenReturn("test-domain.auth0.com");
    when(auth0Properties.clientId()).thenReturn("test-client-id");
    when(auth0Properties.connection()).thenReturn("Username-Password-Authentication");
    when(auth0Properties.timeout()).thenReturn(10000);

    auth0PasswordService = new Auth0PasswordService(auth0Properties, auth0TicketService);
  }

  @Test
  void sendPasswordChangeEmailSuccess() {
    // Given
    final String userEmail = "test@example.com";
    when(auth0TicketService.createPasswordChangeTicketByEmail(userEmail)).thenReturn("ticket-url");

    // When
    String result = auth0PasswordService.sendPasswordChangeEmail(userEmail);

    // Then
    assertEquals("We've just sent you an email to change your password.", result);
  }

  @Test
  void sendPasswordChangeEmailWhenAuth0DisabledThrowsException() {
    // Given
    when(auth0Properties.enabled()).thenReturn(false);
    final String userEmail = "test@example.com";

    // When & Then
    Auth0Exception exception = assertThrows(Auth0Exception.class,
        () -> auth0PasswordService.sendPasswordChangeEmail(userEmail));
    assertEquals("Auth0 integration is disabled", exception.getMessage());
  }

  @Test
  void sendPasswordChangeEmailWhenRequestFailsThrowsException() {
    // Given
    final String userEmail = "test@example.com";
    when(auth0TicketService.createPasswordChangeTicketByEmail(userEmail))
        .thenThrow(new RuntimeException("Network error"));

    // When & Then
    Auth0Exception exception = assertThrows(Auth0Exception.class,
        () -> auth0PasswordService.sendPasswordChangeEmail(userEmail));
    assertEquals("Error sending password change email: Network error", exception.getMessage());
  }

  @Test
  void sendPasswordChangeEmailWhenRestClientThrowsExceptionThrowsAuth0Exception() {
    // Given
    final String userEmail = "test@example.com";
    when(auth0TicketService.createPasswordChangeTicketByEmail(userEmail))
        .thenThrow(new Auth0Exception("Auth0 API error"));

    // When & Then
    Auth0Exception exception = assertThrows(Auth0Exception.class,
        () -> auth0PasswordService.sendPasswordChangeEmail(userEmail));
    assertEquals("Error sending password change email: Auth0 API error", exception.getMessage());
  }

  @Test
  void sendPasswordChangeEmailWithNullEmailThrowsException() {
    // Given
    final String userEmail = null;
    when(auth0TicketService.createPasswordChangeTicketByEmail(userEmail))
        .thenThrow(new Auth0Exception("User not found with email: null"));

    // When & Then
    Auth0Exception exception = assertThrows(Auth0Exception.class,
        () -> auth0PasswordService.sendPasswordChangeEmail(userEmail));
    assertEquals("Error sending password change email: User not found with email: null", exception.getMessage());
  }

  @Test
  void sendPasswordChangeEmailWithEmptyEmailThrowsException() {
    // Given
    final String userEmail = "";
    when(auth0TicketService.createPasswordChangeTicketByEmail(userEmail))
        .thenThrow(new Auth0Exception("User not found with email: "));

    // When & Then
    Auth0Exception exception = assertThrows(Auth0Exception.class,
        () -> auth0PasswordService.sendPasswordChangeEmail(userEmail));
    assertEquals("Error sending password change email: User not found with email: ", exception.getMessage());
  }

  @Test
  void sendPasswordChangeEmailWithWhitespaceEmailThrowsException() {
    // Given
    final String userEmail = "   ";
    when(auth0TicketService.createPasswordChangeTicketByEmail(userEmail))
        .thenThrow(new Auth0Exception("User not found with email:    "));

    // When & Then
    Auth0Exception exception = assertThrows(Auth0Exception.class,
        () -> auth0PasswordService.sendPasswordChangeEmail(userEmail));
    assertEquals("Error sending password change email: User not found with email:    ", exception.getMessage());
  }

  @Test
  void sendPasswordChangeEmailWithNullDomainThrowsException() {
    // Given
    when(auth0Properties.domain()).thenReturn(null);
    final String userEmail = "test@example.com";
    when(auth0TicketService.createPasswordChangeTicketByEmail(userEmail))
        .thenThrow(new RuntimeException("Invalid domain"));

    // When & Then
    Auth0Exception exception = assertThrows(Auth0Exception.class,
        () -> auth0PasswordService.sendPasswordChangeEmail(userEmail));
    assertEquals("Error sending password change email: Invalid domain", exception.getMessage());
  }

  @Test
  void sendPasswordChangeEmailWithEmptyDomainThrowsException() {
    // Given
    when(auth0Properties.domain()).thenReturn("");
    final String userEmail = "test@example.com";
    when(auth0TicketService.createPasswordChangeTicketByEmail(userEmail))
        .thenThrow(new RuntimeException("Invalid domain"));

    // When & Then
    Auth0Exception exception = assertThrows(Auth0Exception.class,
        () -> auth0PasswordService.sendPasswordChangeEmail(userEmail));
    assertEquals("Error sending password change email: Invalid domain", exception.getMessage());
  }

  @Test
  void sendPasswordChangeEmailWithNullClientIdThrowsException() {
    // Given
    when(auth0Properties.clientId()).thenReturn(null);
    final String userEmail = "test@example.com";
    when(auth0TicketService.createPasswordChangeTicketByEmail(userEmail))
        .thenThrow(new RuntimeException("Invalid client ID"));

    // When & Then
    Auth0Exception exception = assertThrows(Auth0Exception.class,
        () -> auth0PasswordService.sendPasswordChangeEmail(userEmail));
    assertEquals("Error sending password change email: Invalid client ID", exception.getMessage());
  }

  @Test
  void sendPasswordChangeEmailWithNullConnectionThrowsException() {
    // Given
    when(auth0Properties.connection()).thenReturn(null);
    final String userEmail = "test@example.com";
    when(auth0TicketService.createPasswordChangeTicketByEmail(userEmail))
        .thenThrow(new RuntimeException("Invalid connection"));

    // When & Then
    Auth0Exception exception = assertThrows(Auth0Exception.class,
        () -> auth0PasswordService.sendPasswordChangeEmail(userEmail));
    assertEquals("Error sending password change email: Invalid connection", exception.getMessage());
  }
}
