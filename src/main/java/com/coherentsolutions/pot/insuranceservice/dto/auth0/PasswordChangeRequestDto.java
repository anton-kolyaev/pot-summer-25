package com.coherentsolutions.pot.insuranceservice.dto.auth0;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO for password change requests.
 * 
 * <p>This DTO represents a request to send a password change email
 * to a user via Auth0.
 */
public record PasswordChangeRequestDto(
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be a valid email address")
    String email
) {
}
