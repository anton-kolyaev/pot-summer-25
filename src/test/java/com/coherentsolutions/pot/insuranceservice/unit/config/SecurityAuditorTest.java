package com.coherentsolutions.pot.insuranceservice.unit.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.coherentsolutions.pot.insuranceservice.config.SecurityAuditor;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

@DisplayName("SecurityAuditor.currentUserOrSystem()")
class SecurityAuditorTest {

  private static void putAuth(String name, boolean authenticated) {
    Authentication auth = mock(Authentication.class);
    when(auth.isAuthenticated()).thenReturn(authenticated);
    when(auth.getName()).thenReturn(name);

    SecurityContext context = mock(SecurityContext.class);
    when(context.getAuthentication()).thenReturn(auth);

    SecurityContextHolder.setContext(context);
  }

  private static void context(SecurityContext context) {
    SecurityContextHolder.setContext(context);
  }

  @AfterEach
  void clearContext() {
    SecurityContextHolder.clearContext();
  }

  @Test
  @DisplayName("Returns SYSTEM when no Authentication is present in SecurityContext")
  void returnsSystem_whenNoAuthenticationInContext() {
    SecurityContext context = mock(SecurityContext.class);
    when(context.getAuthentication()).thenReturn(null);
    context(context);

    UUID actual = SecurityAuditor.currentUserOrSystem();
    assertEquals(SecurityAuditor.SYSTEM, actual);
  }

  @Test
  @DisplayName("Returns SYSTEM when Authentication is not authenticated")
  void returnsSystem_whenAuthenticationNotAuthenticated() {
    putAuth("11111111-1111-1111-1111-111111111111", false);

    UUID actual = SecurityAuditor.currentUserOrSystem();
    assertEquals(SecurityAuditor.SYSTEM, actual);
  }

  @Test
  @DisplayName("Returns SYSTEM when principal is 'anonymousUser'")
  void returnsSystem_whenPrincipalIsAnonymousUser() {
    putAuth("anonymousUser", true);

    UUID actual = SecurityAuditor.currentUserOrSystem();
    assertEquals(SecurityAuditor.SYSTEM, actual);
  }

  @Test
  @DisplayName("Returns SYSTEM when principal name is blank")
  void returnsSystem_whenPrincipalIsBlank() {
    putAuth("   ", true);

    UUID actual = SecurityAuditor.currentUserOrSystem();
    assertEquals(SecurityAuditor.SYSTEM, actual);
  }

  @Test
  @DisplayName("Returns SYSTEM when principal name is null")
  void returnsSystem_whenPrincipalIsNull() {
    Authentication auth = mock(Authentication.class);
    when(auth.isAuthenticated()).thenReturn(true);
    when(auth.getName()).thenReturn(null);

    SecurityContext context = mock(SecurityContext.class);
    when(context.getAuthentication()).thenReturn(auth);
    context(context);

    UUID actual = SecurityAuditor.currentUserOrSystem();
    assertEquals(SecurityAuditor.SYSTEM, actual);
  }

  @Test
  @DisplayName("Returns SYSTEM when principal name is not a UUID")
  void returnsSystem_whenPrincipalIsNotUuid() {
    putAuth("user@example.com", true);

    UUID actual = SecurityAuditor.currentUserOrSystem();
    assertEquals(SecurityAuditor.SYSTEM, actual);
  }

  @Test
  @DisplayName("Parses and returns UUID when principal name is a valid UUID")
  void returnsParsedUuid_whenPrincipalIsUuid() {
    String id = "11111111-1111-1111-1111-111111111111";
    putAuth(id, true);

    UUID actual = SecurityAuditor.currentUserOrSystem();
    assertEquals(UUID.fromString(id), actual);
  }

  @Test
  @DisplayName("Returns SYSTEM on unexpected exception from SecurityContext")
  void returnsSystem_onUnexpectedExceptionFromSecurityContext() {
    SecurityContext badContext = mock(SecurityContext.class);
    when(badContext.getAuthentication()).thenThrow(new RuntimeException("boom"));
    context(badContext);

    UUID actual = SecurityAuditor.currentUserOrSystem();
    assertEquals(SecurityAuditor.SYSTEM, actual);
  }
}
