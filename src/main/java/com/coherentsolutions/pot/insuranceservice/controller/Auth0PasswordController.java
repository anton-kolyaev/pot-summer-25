package com.coherentsolutions.pot.insuranceservice.controller;

import com.coherentsolutions.pot.insuranceservice.dto.auth0.PasswordChangeRequestDto;
import com.coherentsolutions.pot.insuranceservice.service.Auth0PasswordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for handling Auth0 password change operations.
 *
 * <p>This controller provides endpoints for sending password change emails
 * using Auth0's password change functionality.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@Tag(name = "Auth0 Password Management", description = "Endpoints for Auth0 password change operations")
public class Auth0PasswordController {

  private final Auth0PasswordService auth0PasswordService;

  /**
   * Sends a password change email to the specified user.
   *
   * @param request the password change request containing the user's email
   * @return the response message from Auth0
   */
  @PostMapping("/change-password")
  @Operation(
      summary = "Send password change email",
      description = "Sends a password change email to the specified user via Auth0"
  )
  public ResponseEntity<String> changePassword(@Valid @RequestBody PasswordChangeRequestDto request) {
    String response = auth0PasswordService.sendPasswordChangeEmail(request.email());
    return ResponseEntity.ok(response);
  }
}
