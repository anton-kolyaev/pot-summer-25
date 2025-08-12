package com.coherentsolutions.pot.insuranceservice.service;

import static com.coherentsolutions.pot.insuranceservice.util.ObjectUtils.setIfNotNull;

import com.coherentsolutions.pot.insuranceservice.dto.user.UserDto;
import com.coherentsolutions.pot.insuranceservice.dto.user.UserFilter;
import com.coherentsolutions.pot.insuranceservice.enums.UserFunction;
import com.coherentsolutions.pot.insuranceservice.enums.UserStatus;
import com.coherentsolutions.pot.insuranceservice.mapper.UserMapper;
import com.coherentsolutions.pot.insuranceservice.model.User;
import com.coherentsolutions.pot.insuranceservice.model.UserFunctionAssignment;
import com.coherentsolutions.pot.insuranceservice.repository.UserRepository;
import com.coherentsolutions.pot.insuranceservice.repository.UserSpecification;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/**
 * Service class for managing {@link User} entities including retrieval with
 * filtering, creation,
 * and updates.
 */
@Service
@RequiredArgsConstructor
public class UserManagementService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;

  /**
   * Retrieves a paginated list of users filtered by criteria specified in
   * {@link UserFilter}.
   */
  public Page<UserDto> getUsersWithFilters(UserFilter filter, Pageable pageable) {
    Page<User> users = userRepository.findAll(UserSpecification.withFilters(filter), pageable);
    return users.map(userMapper::toDto);
  }

  /**
   * Retrieves user details by ID. If the user does not exist, throws {@link
   * ResponseStatusException} with 404 NOT FOUND.
   */
  public UserDto getUsersDetails(UUID id) {
    User user = userRepository.findByIdOrThrow(id);
    return userMapper.toDto(user);
  }

  /**
   * Creates a new user entity from the given {@link UserDto} and persists it.
   * Also ensures
   * bidirectional linkage between user and their function assignments.
   */
  public UserDto createUser(UserDto dto) {
    User user = userMapper.toEntity(dto);

    if (user.getFunctions() != null) {
      for (UserFunctionAssignment ufa : user.getFunctions()) {
        ufa.setUser(user);
      }
    }

    user = userRepository.save(user);
    return userMapper.toDto(user);
  }

  /**
   * Updates an existing user identified by id with data from {@link UserDto}.
   * Synchronizes the
   * user's function assignments with the incoming data.
   */
  public UserDto updateUser(UUID id, UserDto request) {
    User user = userRepository.findByIdOrThrow(id);

    if (user.getStatus() == UserStatus.INACTIVE) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot modify an inactive user");
    }

    setIfNotNull(request.getFirstName(), user::setFirstName);
    setIfNotNull(request.getLastName(), user::setLastName);
    setIfNotNull(request.getUsername(), user::setUsername);
    setIfNotNull(request.getEmail(), user::setEmail);

    user.setPhoneData(request.getPhoneData());
    user.setAddressData(request.getAddressData());

    if (request.getFunctions() != null) {
      Set<UserFunction> incomingFunctions = request.getFunctions();
      Set<UserFunctionAssignment> currentAssignments = user.getFunctions();

      currentAssignments.removeIf(
          assignment -> !incomingFunctions.contains(assignment.getFunction()));

      Set<UserFunction> currentFunctions =
          currentAssignments.stream()
              .map(UserFunctionAssignment::getFunction)
              .collect(Collectors.toSet());

      for (UserFunction function : incomingFunctions) {
        if (!currentFunctions.contains(function)) {
          UserFunctionAssignment newAssignment = new UserFunctionAssignment();
          newAssignment.setFunction(function);
          newAssignment.setUser(user);
          currentAssignments.add(newAssignment);
        }
      }

      user.setFunctions(currentAssignments);
    }

    User updated = userRepository.save(user);
    return userMapper.toDto(updated);
  }

  /**
   * Sets the user status to inactive.
   */
  @Transactional
  public UserDto deactivateUser(UUID id) {
    return updateUserStatus(id, UserStatus.INACTIVE);
  }

  /**
   * Sets the user status to active.
   */
  @Transactional
  public UserDto reactivateUser(UUID id) {
    return updateUserStatus(id, UserStatus.ACTIVE);
  }

  /**
   * Updates the user status to the given target status if different from the current one. Throws an
   * exception if the status is already set.
   */
  private UserDto updateUserStatus(UUID id, UserStatus targetStatus) {
    User user = userRepository.findByIdOrThrow(id);

    if (user.getStatus() == targetStatus) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "User is already " + targetStatus.name().toLowerCase());
    }

    user.setStatus(targetStatus);
    User savedUser = userRepository.save(user);

    return userMapper.toDto(savedUser);
  }
}
