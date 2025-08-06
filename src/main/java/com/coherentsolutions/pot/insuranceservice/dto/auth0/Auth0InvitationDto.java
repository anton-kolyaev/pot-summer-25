package com.coherentsolutions.pot.insuranceservice.dto.auth0;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Auth0 user invitation requests.
 *
 * <p>This DTO represents the data structure for creating Auth0 users with
 * invitation emails instead of direct password creation.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Auth0InvitationDto {

  @NotBlank(message = "Email is required")
  @Email(message = "Email must be valid")
  private String email;

  @NotBlank(message = "Name is required")
  @Size(max = 100, message = "Name must not exceed 100 characters")
  private String name;

  @Size(max = 100, message = "Nickname must not exceed 100 characters")
  private String nickname;

  @Size(max = 500, message = "Picture URL must not exceed 500 characters")
  private String picture;

  @JsonProperty("user_metadata")
  private Map<String, Object> userMetadata;

  @JsonProperty("app_metadata")
  private Map<String, Object> appMetadata;

  @JsonProperty("invitation_url")
  private String invitationUrl;

  @JsonProperty("client_id")
  private String clientId;

  @JsonProperty("connection")
  private String connection = "Username-Password-Authentication";

  // Constructor for required fields
  public Auth0InvitationDto(String email, String name) {
    this.email = email;
    this.name = name;
  }
} 