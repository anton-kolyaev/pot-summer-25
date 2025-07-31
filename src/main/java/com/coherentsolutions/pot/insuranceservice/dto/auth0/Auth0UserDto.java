package com.coherentsolutions.pot.insuranceservice.dto.auth0;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Auth0 user operations.
 *
 * <p>This DTO represents the data structure for creating and updating Auth0 users
 * through the backend API.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Auth0UserDto {

  @JsonProperty("user_id")
  private String userId;

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

  private boolean emailVerified = false;

  private boolean blocked = false;

  // Auth0 connection field - required for user creation
  private String connection = "Username-Password-Authentication";

  // Constructor for required fields
  public Auth0UserDto(String email, String name) {
    this.email = email;
    this.name = name;
  }
} 