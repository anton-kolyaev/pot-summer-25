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
  User toAuth0User(Auth0UserDto dto);

  /**
   * Converts Auth0 User object to Auth0UserDto.
   *
   * @param user the Auth0 User object to convert
   * @return the DTO
   */
  @Mapping(target = "password", ignore = true) // Don't map password from Auth0 response
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
  void updateUserFromDto(Auth0UserDto dto, @MappingTarget User user);
} 