package com.coherentsolutions.pot.insuranceservice.controller;

import com.auth0.exception.Auth0Exception;
import com.coherentsolutions.pot.insuranceservice.dto.auth0.Auth0UserDto;
import com.coherentsolutions.pot.insuranceservice.dto.error.ErrorResponseDto;
import com.coherentsolutions.pot.insuranceservice.service.Auth0UserManagementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing Auth0 users.
 *
 * <p>This controller provides endpoints for creating, reading, updating, and deleting
 * users in the Auth0 authorization server via backend APIs.
 */
@RestController
@RequestMapping("/api/v1/auth0/users")
@ConditionalOnProperty(name = "auth0.enabled", havingValue = "true", matchIfMissing = false)
@Tag(name = "Auth0 User Management", description = "Operations for managing Auth0 users")
public class Auth0UserManagementController {

  private final Auth0UserManagementService auth0UserManagementService;

  public Auth0UserManagementController(Auth0UserManagementService auth0UserManagementService) {
    this.auth0UserManagementService = auth0UserManagementService;
  }

  /**
   * Creates a new user in Auth0.
   *
   * @param userDto the user data to create
   * @return the created user
   */
  @PostMapping
  @Operation(summary = "Create a new Auth0 user", description = "Creates a new user in Auth0")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "User created successfully",
          content = @Content(schema = @Schema(implementation = Auth0UserDto.class))),
      @ApiResponse(responseCode = "400", description = "Invalid input data",
          content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
      @ApiResponse(responseCode = "500", description = "Internal server error",
          content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
  })
  public ResponseEntity<Auth0UserDto> createUser(
      @Parameter(description = "User data to create", required = true)
      @Valid @RequestBody Auth0UserDto userDto) {
    try {
      Auth0UserDto createdUser = auth0UserManagementService.createUser(userDto);
      return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    } catch (Auth0Exception e) {
      throw new RuntimeException("Auth0 user creation failed: " + e.getMessage(), e);
    }
  }

  /**
   * Retrieves a user by their ID.
   *
   * @param userId the user ID
   * @return the user, or 404 if not found
   */
  @GetMapping("/{userId}")
  @Operation(summary = "Get user by ID", description = "Retrieves a user by their Auth0 user ID")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "User found",
          content = @Content(schema = @Schema(implementation = Auth0UserDto.class))),
      @ApiResponse(responseCode = "404", description = "User not found",
          content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
      @ApiResponse(responseCode = "500", description = "Internal server error",
          content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
  })
  public ResponseEntity<Auth0UserDto> getUserById(
      @Parameter(description = "Auth0 user ID", required = true)
      @PathVariable String userId) {
    try {
      Auth0UserDto user = auth0UserManagementService.getUserDtoById(userId);
      if (user == null) {
        return ResponseEntity.notFound().build();
      }
      return ResponseEntity.ok(user);
    } catch (Auth0Exception e) {
      throw new RuntimeException("Auth0 user retrieval failed: " + e.getMessage(), e);
    }
  }

  /**
   * Retrieves all users with optional filtering.
   *
   * @param email optional email filter
   * @param name optional name filter
   * @return list of users
   */
  @GetMapping
  @Operation(summary = "Get all users", description = "Retrieves all users with optional filtering")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Users retrieved successfully",
          content = @Content(schema = @Schema(implementation = Auth0UserDto.class))),
      @ApiResponse(responseCode = "500", description = "Internal server error",
          content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
  })
  public ResponseEntity<List<Auth0UserDto>> getUsers(
      @Parameter(description = "Filter by email")
      @RequestParam(required = false) String email,
      @Parameter(description = "Filter by name")
      @RequestParam(required = false) String name) {
    try {
      // TODO: Implement proper filtering with UserFilter
      // For now, return empty list as filtering is not implemented yet
      List<Auth0UserDto> users = auth0UserManagementService.getUserDtos(null);
      return ResponseEntity.ok(users);
    } catch (Auth0Exception e) {
      throw new RuntimeException("Auth0 user list retrieval failed: " + e.getMessage(), e);
    }
  }

  /**
   * Updates an existing user.
   *
   * @param userId the user ID
   * @param userDto the updated user data
   * @return the updated user
   */
  @PutMapping("/{userId}")
  @Operation(summary = "Update user", description = "Updates an existing user in Auth0")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "User updated successfully",
          content = @Content(schema = @Schema(implementation = Auth0UserDto.class))),
      @ApiResponse(responseCode = "400", description = "Invalid input data",
          content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
      @ApiResponse(responseCode = "404", description = "User not found",
          content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
      @ApiResponse(responseCode = "500", description = "Internal server error",
          content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
  })
  public ResponseEntity<Auth0UserDto> updateUser(
      @Parameter(description = "Auth0 user ID", required = true)
      @PathVariable String userId,
      @Parameter(description = "Updated user data", required = true)
      @Valid @RequestBody Auth0UserDto userDto) {
    try {
      Auth0UserDto updatedUser = auth0UserManagementService.updateUser(userId, userDto);
      return ResponseEntity.ok(updatedUser);
    } catch (Auth0Exception e) {
      if (e.getMessage().contains("User not found")) {
        return ResponseEntity.notFound().build();
      }
      throw new RuntimeException("Auth0 user update failed: " + e.getMessage(), e);
    }
  }

  /**
   * Deletes a user by their ID.
   *
   * @param userId the user ID
   * @return 204 No Content on success
   */
  @DeleteMapping("/{userId}")
  @Operation(summary = "Delete user", description = "Deletes a user from Auth0")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "User deleted successfully"),
      @ApiResponse(responseCode = "404", description = "User not found",
          content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
      @ApiResponse(responseCode = "500", description = "Internal server error",
          content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
  })
  public ResponseEntity<Void> deleteUser(
      @Parameter(description = "Auth0 user ID", required = true)
      @PathVariable String userId) {
    try {
      auth0UserManagementService.deleteUser(userId);
      return ResponseEntity.noContent().build();
    } catch (Auth0Exception e) {
      if (e.getMessage().contains("User not found")) {
        return ResponseEntity.notFound().build();
      }
      throw new RuntimeException("Auth0 user deletion failed: " + e.getMessage(), e);
    }
  }
} 