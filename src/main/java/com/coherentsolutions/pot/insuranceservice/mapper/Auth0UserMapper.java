package com.coherentsolutions.pot.insuranceservice.mapper;

import com.auth0.json.mgmt.users.User;
import com.coherentsolutions.pot.insuranceservice.dto.auth0.Auth0UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * Mapper for converting between Auth0UserDto and Auth0 User objects.
 *
 * <p>This mapper uses MapStruct to generate mapping code between DTOs and
 * Auth0 SDK User objects for API operations.
 */
@Mapper(componentModel = "spring")
public interface Auth0UserMapper {

  /**
   * Converts Auth0UserDto to Auth0 User object.
   *
   * @param dto the DTO to convert
   * @return the Auth0 User object
   */
  @Mapping(target = "userMetadata", source = "userMetadata")
  @Mapping(target = "appMetadata", source = "appMetadata")
  @Mapping(target = "emailVerified", source = "emailVerified")
  @Mapping(target = "connection", source = "connection")
  @Mapping(target = "password", 
           expression = "java(dto.getPassword() != null ? dto.getPassword().toCharArray() : null)")
  @Mapping(target = "username", ignore = true)
  @Mapping(target = "phoneNumber", ignore = true)
  @Mapping(target = "phoneVerified", ignore = true)
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "givenName", ignore = true)
  @Mapping(target = "familyName", ignore = true)
  @Mapping(target = "clientId", ignore = true)
  @Mapping(target = "verifyPassword", ignore = true)
  @Mapping(target = "verifyEmail", ignore = true)
  @Mapping(target = "verifyPhoneNumber", ignore = true)
  @Mapping(target = "identities", ignore = true)
  @Mapping(target = "multifactor", ignore = true)
  @Mapping(target = "values", ignore = true)
  User toAuth0User(Auth0UserDto dto);

  /**
   * Converts Auth0 User object to Auth0UserDto.
   *
   * @param user the Auth0 User object to convert
   * @return the DTO
   */
  @Mapping(target = "password", ignore = true) // Don't map password from Auth0 response
  @Mapping(target = "userId", expression = "java(user.getId())")
  @Mapping(target = "connection", ignore = true)
  Auth0UserDto toDto(User user);

  /**
   * Updates an existing Auth0 User object with data from DTO.
   *
   * @param dto the DTO with updated data
   * @param user the existing User object to update
   */
  @Mapping(target = "userMetadata", source = "userMetadata")
  @Mapping(target = "appMetadata", source = "appMetadata")
  @Mapping(target = "emailVerified", source = "emailVerified")
  @Mapping(target = "connection", source = "connection")
  @Mapping(target = "password", 
           expression = "java(dto.getPassword() != null ? dto.getPassword().toCharArray() : null)")
  @Mapping(target = "username", ignore = true)
  @Mapping(target = "phoneNumber", ignore = true)
  @Mapping(target = "phoneVerified", ignore = true)
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "givenName", ignore = true)
  @Mapping(target = "familyName", ignore = true)
  @Mapping(target = "clientId", ignore = true)
  @Mapping(target = "verifyPassword", ignore = true)
  @Mapping(target = "verifyEmail", ignore = true)
  @Mapping(target = "verifyPhoneNumber", ignore = true)
  @Mapping(target = "identities", ignore = true)
  @Mapping(target = "multifactor", ignore = true)
  @Mapping(target = "values", ignore = true)
  void updateUserFromDto(Auth0UserDto dto, @MappingTarget User user);
} 