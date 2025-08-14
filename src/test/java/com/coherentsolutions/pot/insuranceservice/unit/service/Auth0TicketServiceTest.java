package com.coherentsolutions.pot.insuranceservice.unit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.coherentsolutions.pot.insuranceservice.config.Auth0Properties;
import com.coherentsolutions.pot.insuranceservice.exception.Auth0Exception;
import com.coherentsolutions.pot.insuranceservice.service.Auth0TicketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClient;

@ExtendWith(MockitoExtension.class)
class Auth0TicketServiceTest {

  @Mock
  private RestClient restClient;

  @Mock
  private Auth0Properties auth0Properties;

  private Auth0TicketService auth0TicketService;

  @BeforeEach
  void setUp() {
    when(auth0Properties.enabled()).thenReturn(true);
    when(auth0Properties.domain()).thenReturn("test-domain.auth0.com");
    when(auth0Properties.clientId()).thenReturn("test-client-id");
    when(auth0Properties.connection()).thenReturn("Username-Password-Authentication");
    when(auth0Properties.apiToken()).thenReturn("test-api-token");

    auth0TicketService = new Auth0TicketService(null, auth0Properties, restClient);
  }

  @Test
  @DisplayName("Should throw Auth0Exception when Auth0 is disabled")
  void shouldThrowAuth0ExceptionWhenAuth0IsDisabled() {
    // Given
    when(auth0Properties.enabled()).thenReturn(false);
    String email = "test@example.com";

    // When & Then
    Auth0Exception exception = assertThrows(Auth0Exception.class,
        () -> auth0TicketService.createPasswordChangeTicketByEmail(email));
    assertEquals("Auth0 integration is disabled", exception.getMessage());
  }

  @Test
  @DisplayName("Should handle null email")
  void shouldHandleNullEmail() {
    // Given
    String email = null;

    // When & Then
    assertThrows(Exception.class,
        () -> auth0TicketService.createPasswordChangeTicketByEmail(email));
  }

  @Test
  @DisplayName("Should handle empty email")
  void shouldHandleEmptyEmail() {
    // Given
    String email = "";

    // When & Then
    assertThrows(Exception.class,
        () -> auth0TicketService.createPasswordChangeTicketByEmail(email));
  }
}
