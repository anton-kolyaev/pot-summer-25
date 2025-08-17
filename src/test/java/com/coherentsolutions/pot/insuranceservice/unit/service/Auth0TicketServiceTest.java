package com.coherentsolutions.pot.insuranceservice.unit.service;

import static org.junit.jupiter.api.Assertions.assertThrows;

import com.coherentsolutions.pot.insuranceservice.config.Auth0Properties;
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
    auth0TicketService = new Auth0TicketService(null, auth0Properties, restClient);
  }

  @Test
  @DisplayName("Should handle null email")
  void shouldHandleNullEmail() {
    // Given
    String email = null;

    // When & Then - This will throw an exception when trying to search for user
    assertThrows(Exception.class,
        () -> auth0TicketService.createPasswordChangeTicketByEmail(email));
  }

  @Test
  @DisplayName("Should handle empty email")
  void shouldHandleEmptyEmail() {
    // Given
    String email = "";

    // When & Then - This will throw an exception when trying to search for user
    assertThrows(Exception.class,
        () -> auth0TicketService.createPasswordChangeTicketByEmail(email));
  }
}
