package com.coherentsolutions.pot.insuranceservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Service for sending emails, including user invitations.
 * 
 * <p>This service handles email sending functionality for the application,
 * including invitation emails for new user registration.
 * 
 * <p>Note: This implementation logs email content. To enable real email sending:
 * 1. Add spring.mail.host to application.yml
 * 2. Configure SMTP settings
 * 3. Replace this service with one that uses JavaMailSender
 */
@Slf4j
@Service
public class EmailService {
  
  @Value("${app.invitation.expiration-hours:24}")
  private int invitationExpirationHours;

  /**
   * Sends an invitation email to a new user.
   * 
   * @param toEmail the recipient email address
   * @param userName the user's name
   * @param invitationUrl the invitation URL for setting up the account
   * @param companyName the company name (optional)
   */
  public void sendInvitationEmail(String toEmail, String userName, String invitationUrl, String companyName) {
    log.info("Sending invitation email to: {} for user: {}", toEmail, userName);
    
    try {
      String subject = buildInvitationSubject(companyName);
      String content = buildInvitationEmailContent(userName, invitationUrl, companyName);
      
      // Log the email content (in production, this would be sent via email service)
      log.info("=== INVITATION EMAIL ===");
      log.info("To: {}", toEmail);
      log.info("Subject: {}", subject);
      log.info("Content:\n{}", content);
      log.info("=== END INVITATION EMAIL ===");
      
      log.info("Successfully logged invitation email to: {}", toEmail);
      
    } catch (Exception e) {
      log.error("Failed to send invitation email to: {}", toEmail, e);
      throw new RuntimeException("Failed to send invitation email: " + e.getMessage(), e);
    }
  }

  /**
   * Builds the invitation email subject.
   * 
   * @param companyName the company name
   * @return the email subject
   */
  private String buildInvitationSubject(String companyName) {
    if (companyName != null && !companyName.trim().isEmpty()) {
      return "You've been invited to join " + companyName;
    }
    return "You've been invited to join our application";
  }

  /**
   * Builds the invitation email content.
   * 
   * @param userName the user's name
   * @param invitationUrl the invitation URL
   * @param companyName the company name
   * @return the email content
   */
  private String buildInvitationEmailContent(String userName, String invitationUrl, String companyName) {
    StringBuilder content = new StringBuilder();
    
    content.append("Hello ").append(userName).append(",\n\n");
    
    if (companyName != null && !companyName.trim().isEmpty()) {
      content.append("You have been invited to join ").append(companyName).append(".\n\n");
    } else {
      content.append("You have been invited to join our application.\n\n");
    }
    
    content.append("To get started, please click the following link to set up your account:\n");
    content.append(invitationUrl).append("\n\n");
    
    content.append("This invitation link will expire in ").append(invitationExpirationHours).append(" hours.\n\n");
    
    content.append("If you have any questions or need assistance, please contact our support team.\n\n");
    
    content.append("Best regards,\n");
    if (companyName != null && !companyName.trim().isEmpty()) {
      content.append("The ").append(companyName).append(" Team");
    } else {
      content.append("The Application Team");
    }
    
    return content.toString();
  }

  /**
   * Sends a password reset email.
   * 
   * @param toEmail the recipient email address
   * @param userName the user's name
   * @param resetUrl the password reset URL
   */
  public void sendPasswordResetEmail(String toEmail, String userName, String resetUrl) {
    log.info("Sending password reset email to: {} for user: {}", toEmail, userName);
    
    try {
      String subject = "Password Reset Request";
      String content = buildPasswordResetEmailContent(userName, resetUrl);
      
      // Log the email content (in production, this would be sent via email service)
      log.info("=== PASSWORD RESET EMAIL ===");
      log.info("To: {}", toEmail);
      log.info("Subject: {}", subject);
      log.info("Content:\n{}", content);
      log.info("=== END PASSWORD RESET EMAIL ===");
      
      log.info("Successfully logged password reset email to: {}", toEmail);
      
    } catch (Exception e) {
      log.error("Failed to send password reset email to: {}", toEmail, e);
      throw new RuntimeException("Failed to send password reset email: " + e.getMessage(), e);
    }
  }

  /**
   * Builds the password reset email content.
   * 
   * @param userName the user's name
   * @param resetUrl the reset URL
   * @return the email content
   */
  private String buildPasswordResetEmailContent(String userName, String resetUrl) {
    StringBuilder content = new StringBuilder();
    
    content.append("Hello ").append(userName).append(",\n\n");
    content.append("You have requested to reset your password.\n\n");
    content.append("Please click the following link to reset your password:\n");
    content.append(resetUrl).append("\n\n");
    content.append("This link will expire in ").append(invitationExpirationHours).append(" hours.\n\n");
    content.append("If you did not request this password reset, please ignore this email.\n\n");
    content.append("Best regards,\n");
    content.append("The Application Team");
    
    return content.toString();
  }
} 