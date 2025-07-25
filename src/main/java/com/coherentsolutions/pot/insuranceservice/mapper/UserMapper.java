package com.coherentsolutions.pot.insuranceservice.mapper;

import com.coherentsolutions.pot.insuranceservice.dto.user.UserDto;
import com.coherentsolutions.pot.insuranceservice.enums.UserFunction;
import com.coherentsolutions.pot.insuranceservice.model.User;
import com.coherentsolutions.pot.insuranceservice.model.UserFunctionAssignment;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper interface for converting between {@link User} entities and {@link UserDto} objects. Uses
 * MapStruct for automatic mapping and contains custom mapping logic for user functions.
 */
@Mapper(componentModel = "spring")
public interface UserMapper {

  /**
   * Converts a {@link User} entity to a {@link UserDto}.
   */
  @Mapping(source = "company.id", target = "companyId")
  UserDto toDto(User user);

  /**
   * Converts a {@link UserDto} to a {@link User} entity. Ignores id, createdAt, updatedAt,
   * createdBy, updatedBy fields and sets status to ACTIVE.
   */
  @Mapping(source = "companyId", target = "company.id")
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "createdBy", ignore = true)
  @Mapping(target = "updatedBy", ignore = true)
  @Mapping(target = "status", constant = "ACTIVE")
  User toEntity(UserDto dto);

  /**
   * Maps a set of {@link UserFunctionAssignment} entities to a set of {@link UserFunction} enums.
   */
  default Set<UserFunction> mapToFunctions(Set<UserFunctionAssignment> assignments) {
    if (assignments == null) {
      return null;
    }
    return assignments.stream()
        .map(UserFunctionAssignment::getFunction)
        .collect(Collectors.toSet());
  }

  /**
   * Maps a set of {@link UserFunction} enums to a set of {@link UserFunctionAssignment} entities.
   */
  default Set<UserFunctionAssignment> mapToAssignments(Set<UserFunction> functions) {
    if (functions == null) {
      return null;
    }
    return functions.stream()
        .map(f -> {
          UserFunctionAssignment ufa = new UserFunctionAssignment();
          ufa.setFunction(f);
          return ufa;
        })
        .collect(Collectors.toSet());
  }
}
