package com.coherentsolutions.pot.insuranceservice.dto.auth0;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Map;

/**
 * DTO for Auth0 user operations.
 *
 * <p>This DTO represents the data structure for creating and updating Auth0 users
 * through the backend API.
 */
public class Auth0UserDto {

  @NotBlank(message = "Email is required")
  @Email(message = "Email must be valid")
  private String email;

  @NotBlank(message = "Password is required")
  @Size(min = 8, message = "Password must be at least 8 characters long")
  private String password;

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

  // Constructors
  public Auth0UserDto() {}

  public Auth0UserDto(String email, String password, String name) {
    this.email = email;
    this.password = password;
    this.name = name;
  }

  // Getters and Setters
  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getNickname() {
    return nickname;
  }

  public void setNickname(String nickname) {
    this.nickname = nickname;
  }

  public String getPicture() {
    return picture;
  }

  public void setPicture(String picture) {
    this.picture = picture;
  }

  public Map<String, Object> getUserMetadata() {
    return userMetadata;
  }

  public void setUserMetadata(Map<String, Object> userMetadata) {
    this.userMetadata = userMetadata;
  }

  public Map<String, Object> getAppMetadata() {
    return appMetadata;
  }

  public void setAppMetadata(Map<String, Object> appMetadata) {
    this.appMetadata = appMetadata;
  }

  public boolean isEmailVerified() {
    return emailVerified;
  }

  public void setEmailVerified(boolean emailVerified) {
    this.emailVerified = emailVerified;
  }

  public boolean isBlocked() {
    return blocked;
  }

  public void setBlocked(boolean blocked) {
    this.blocked = blocked;
  }

  public String getConnection() {
    return connection;
  }

  public void setConnection(String connection) {
    this.connection = connection;
  }
} 