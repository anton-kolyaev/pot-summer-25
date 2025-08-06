package com.coherentsolutions.pot.insuranceservice.unit.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;

import com.coherentsolutions.pot.insuranceservice.service.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
@DisplayName("EmailService Unit Tests")
class EmailServiceTest {

  @Mock
  private JavaMailSender mailSender;

  @InjectMocks
  private EmailService emailService;

  private static final String TEST_EMAIL = "test@example.com";
  private static final String TEST_USER_NAME = "John Doe";
  private static final String TEST_INVITATION_URL = "https://example.com/invitation";
  private static final String TEST_COMPANY_NAME = "Test Company";
  private static final String TEST_FROM_EMAIL = "noreply@example.com";

  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(emailService, "fromEmail", TEST_FROM_EMAIL);
    ReflectionTestUtils.setField(emailService, "invitationExpirationHours", 24);
  }

  @Test
  @DisplayName("Should send invitation email successfully with mail sender")
  void shouldSendInvitationEmailSuccessfullyWithMailSender() {
    // When & Then
    assertDoesNotThrow(() -> 
        emailService.sendInvitationEmail(TEST_EMAIL, TEST_USER_NAME, TEST_INVITATION_URL, TEST_COMPANY_NAME));
  }

  @Test
  @DisplayName("Should send invitation email successfully without company name")
  void shouldSendInvitationEmailSuccessfullyWithoutCompanyName() {
    // When & Then
    assertDoesNotThrow(() -> 
        emailService.sendInvitationEmail(TEST_EMAIL, TEST_USER_NAME, TEST_INVITATION_URL, null));
  }

  @Test
  @DisplayName("Should handle mail sender exception gracefully")
  void shouldHandleMailSenderExceptionGracefully() {
    // Given
    doThrow(new RuntimeException("Mail server error"))
        .when(mailSender).send(any(SimpleMailMessage.class));

    // When & Then
    assertDoesNotThrow(() -> 
        emailService.sendInvitationEmail(TEST_EMAIL, TEST_USER_NAME, TEST_INVITATION_URL, TEST_COMPANY_NAME));
  }

  @Test
  @DisplayName("Should send invitation email when mail sender is null")
  void shouldSendInvitationEmailWhenMailSenderIsNull() {
    // Given
    ReflectionTestUtils.setField(emailService, "mailSender", null);

    // When & Then
    assertDoesNotThrow(() -> 
        emailService.sendInvitationEmail(TEST_EMAIL, TEST_USER_NAME, TEST_INVITATION_URL, TEST_COMPANY_NAME));
  }

  @Test
  @DisplayName("Should send invitation email when from email is empty")
  void shouldSendInvitationEmailWhenFromEmailIsEmpty() {
    // Given
    ReflectionTestUtils.setField(emailService, "fromEmail", "");

    // When & Then
    assertDoesNotThrow(() -> 
        emailService.sendInvitationEmail(TEST_EMAIL, TEST_USER_NAME, TEST_INVITATION_URL, TEST_COMPANY_NAME));
  }

  @Test
  @DisplayName("Should send password reset email successfully")
  void shouldSendPasswordResetEmailSuccessfully() {
    // Given
    String resetUrl = "https://example.com/reset";

    // When & Then
    assertDoesNotThrow(() -> 
        emailService.sendPasswordResetEmail(TEST_EMAIL, TEST_USER_NAME, resetUrl));
  }

  @Test
  @DisplayName("Should handle password reset email exception gracefully")
  void shouldHandlePasswordResetEmailExceptionGracefully() {
    // Given
    String resetUrl = "https://example.com/reset";
    doThrow(new RuntimeException("Mail server error"))
        .when(mailSender).send(any(SimpleMailMessage.class));

    // When & Then
    assertDoesNotThrow(() -> 
        emailService.sendPasswordResetEmail(TEST_EMAIL, TEST_USER_NAME, resetUrl));
  }

  @Test
  @DisplayName("Should send password reset email when mail sender is null")
  void shouldSendPasswordResetEmailWhenMailSenderIsNull() {
    // Given
    String resetUrl = "https://example.com/reset";
    ReflectionTestUtils.setField(emailService, "mailSender", null);

    // When & Then
    assertDoesNotThrow(() -> 
        emailService.sendPasswordResetEmail(TEST_EMAIL, TEST_USER_NAME, resetUrl));
  }

  @Test
  @DisplayName("Should handle invitation email with null parameters")
  void shouldHandleInvitationEmailWithNullParameters() {
    // When & Then
    assertDoesNotThrow(() -> 
        emailService.sendInvitationEmail(null, null, null, null));
  }

  @Test
  @DisplayName("Should handle password reset email with null parameters")
  void shouldHandlePasswordResetEmailWithNullParameters() {
    // When & Then
    assertDoesNotThrow(() -> 
        emailService.sendPasswordResetEmail(null, null, null));
  }
} 