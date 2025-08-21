package com.coherentsolutions.pot.insuranceservice.unit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.coherentsolutions.pot.insuranceservice.dto.user.UserDto;
import com.coherentsolutions.pot.insuranceservice.dto.user.UserFilter;
import com.coherentsolutions.pot.insuranceservice.enums.UserFunction;
import com.coherentsolutions.pot.insuranceservice.enums.UserStatus;
import com.coherentsolutions.pot.insuranceservice.mapper.UserMapper;
import com.coherentsolutions.pot.insuranceservice.model.Address;
import com.coherentsolutions.pot.insuranceservice.model.Phone;
import com.coherentsolutions.pot.insuranceservice.model.User;
import com.coherentsolutions.pot.insuranceservice.model.UserFunctionAssignment;
import com.coherentsolutions.pot.insuranceservice.repository.UserRepository;
import com.coherentsolutions.pot.insuranceservice.service.Auth0UserMetadataService;
import com.coherentsolutions.pot.insuranceservice.service.UserManagementService;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class UserManagementServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private UserMapper userMapper;

  private UserManagementService userManagementService;

  @Mock
  private Auth0UserMetadataService auth0UserMetadataService;

  @BeforeEach
  void setUp() {
    userManagementService = new UserManagementService(auth0UserMetadataService, userRepository, userMapper);
  }

  @Test
  @DisplayName("Should get users with filters successfully")
  void shouldGetUsersWithFiltersSuccessfully() {
    // Given
    UserFilter filter = new UserFilter();
    filter.setName("John");
    filter.setEmail("john@example.com");
    filter.setStatus(UserStatus.ACTIVE);

    User user = new User();
    user.setId(UUID.randomUUID());
    user.setFirstName("John");
    user.setEmail("john@example.com");

    List<User> users = List.of(user);
    final Page<User> userPage = new PageImpl<>(users);

    UserDto userDto = new UserDto();
    userDto.setId(user.getId());
    userDto.setFirstName("John");
    userDto.setEmail("john@example.com");

    final Pageable pageable = PageRequest.of(0, 10, Sort.by("firstName"));

    when(userRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(userPage);
    when(userMapper.toDto(user)).thenReturn(userDto);

    // When
    Page<UserDto> result = userManagementService.getUsersWithFilters(filter, pageable);

    // Then
    assertEquals(1, result.getContent().size());
    assertEquals(userDto, result.getContent().get(0));
    verify(userRepository).findAll(any(Specification.class), eq(pageable));
  }

  @Test
  @DisplayName("Should get user details by ID successfully")
  void shouldGetUserDetailsByIdSuccessfully() {
    // Given
    UUID userId = UUID.randomUUID();
    User user = new User();
    user.setId(userId);
    user.setFirstName("John");
    user.setLastName("Doe");

    UserDto userDto = new UserDto();
    userDto.setId(userId);
    userDto.setFirstName("John");
    userDto.setLastName("Doe");

    when(userRepository.findByIdOrThrow(userId)).thenReturn(user);
    when(userMapper.toDto(user)).thenReturn(userDto);

    // When
    UserDto result = userManagementService.getUsersDetails(userId);

    // Then
    assertEquals(userDto, result);
    verify(userRepository).findByIdOrThrow(userId);
  }

  @Test
  @DisplayName("Should create user successfully")
  void shouldCreateUserSuccessfully() {
    // Given
    UserDto requestDto = new UserDto();
    requestDto.setFirstName("John");
    requestDto.setLastName("Doe");
    requestDto.setEmail("john.doe@example.com");

    User user = new User();
    user.setFirstName("John");
    user.setLastName("Doe");
    user.setEmail("john.doe@example.com");

    User savedUser = new User();
    savedUser.setId(UUID.randomUUID());
    savedUser.setFirstName("John");
    savedUser.setLastName("Doe");
    savedUser.setEmail("john.doe@example.com");

    UserDto expectedDto = new UserDto();
    expectedDto.setId(savedUser.getId());
    expectedDto.setFirstName("John");
    expectedDto.setLastName("Doe");
    expectedDto.setEmail("john.doe@example.com");

    when(userMapper.toEntity(requestDto)).thenReturn(user);
    when(userRepository.save(user)).thenReturn(savedUser);
    when(userMapper.toDto(savedUser)).thenReturn(expectedDto);

    // When
    UserDto result = userManagementService.createUser(requestDto);

    // Then
    assertEquals(expectedDto, result);
    verify(userRepository).save(user);
  }

  @Test
  @DisplayName("Should create user with functions successfully")
  void shouldCreateUserWithFunctionsSuccessfully() {
    // Given
    UserDto requestDto = new UserDto();
    requestDto.setFirstName("John");
    requestDto.setLastName("Doe");
    requestDto.setEmail("john.doe@example.com");

    Set<UserFunction> functions = new HashSet<>();
    functions.add(UserFunction.COMPANY_MANAGER);
    requestDto.setFunctions(functions);

    User user = new User();
    user.setFirstName("John");
    user.setLastName("Doe");
    user.setEmail("john.doe@example.com");

    Set<UserFunctionAssignment> functionAssignments = new HashSet<>();
    UserFunctionAssignment assignment = new UserFunctionAssignment();
    assignment.setFunction(UserFunction.COMPANY_MANAGER);
    functionAssignments.add(assignment);
    user.setFunctions(functionAssignments);

    User savedUser = new User();
    savedUser.setId(UUID.randomUUID());
    savedUser.setFunctions(functionAssignments);

    UserDto expectedDto = new UserDto();
    expectedDto.setId(savedUser.getId());

    when(userMapper.toEntity(requestDto)).thenReturn(user);
    when(userRepository.save(user)).thenReturn(savedUser);
    when(userMapper.toDto(savedUser)).thenReturn(expectedDto);

    // When
    UserDto result = userManagementService.createUser(requestDto);

    // Then
    assertEquals(expectedDto, result);
    verify(userRepository).save(user);
    assertEquals(user, assignment.getUser());
  }

  @Test
  @DisplayName("Should update user successfully")
  void shouldUpdateUserSuccessfully() {
    // Given
    final UUID userId = UUID.randomUUID();
    User existingUser = new User();
    existingUser.setId(userId);
    existingUser.setStatus(UserStatus.ACTIVE);

    UserDto requestDto = new UserDto();
    requestDto.setFirstName("Updated John");
    requestDto.setLastName("Updated Doe");
    requestDto.setEmail("updated.john@example.com");

    User updatedUser = new User();
    updatedUser.setId(userId);
    updatedUser.setFirstName("Updated John");
    updatedUser.setLastName("Updated Doe");
    updatedUser.setEmail("updated.john@example.com");

    UserDto expectedDto = new UserDto();
    expectedDto.setId(userId);
    expectedDto.setFirstName("Updated John");
    expectedDto.setLastName("Updated Doe");
    expectedDto.setEmail("updated.john@example.com");

    when(userRepository.findByIdOrThrow(userId)).thenReturn(existingUser);
    when(userRepository.save(existingUser)).thenReturn(updatedUser);
    when(userMapper.toDto(updatedUser)).thenReturn(expectedDto);

    // When
    UserDto result = userManagementService.updateUser(userId, requestDto);

    // Then
    assertEquals(expectedDto, result);
    assertEquals("Updated John", existingUser.getFirstName());
    assertEquals("Updated Doe", existingUser.getLastName());
    assertEquals("updated.john@example.com", existingUser.getEmail());
  }

  @Test
  @DisplayName("Should throw exception when updating inactive user")
  void shouldThrowExceptionWhenUpdatingInactiveUser() {
    // Given
    UUID userId = UUID.randomUUID();
    UserDto requestDto = new UserDto();
    requestDto.setFirstName("Jane");

    User existingUser = new User();
    existingUser.setId(userId);
    existingUser.setStatus(UserStatus.INACTIVE);

    when(userRepository.findByIdOrThrow(userId)).thenReturn(existingUser);

    // When & Then
    ResponseStatusException exception = assertThrows(ResponseStatusException.class,
        () -> userManagementService.updateUser(userId, requestDto));
    assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    assertEquals("Cannot modify an inactive user", exception.getReason());
  }

  @Test
  @DisplayName("Should update user with functions successfully")
  void shouldUpdateUserWithFunctionsSuccessfully() {
    // Given
    final UUID userId = UUID.randomUUID();
    User existingUser = new User();
    existingUser.setId(userId);
    existingUser.setStatus(UserStatus.ACTIVE);

    Set<UserFunctionAssignment> currentAssignments = new HashSet<>();
    UserFunctionAssignment existingAssignment = new UserFunctionAssignment();
    existingAssignment.setFunction(UserFunction.COMPANY_MANAGER);
    existingAssignment.setUser(existingUser);
    currentAssignments.add(existingAssignment);
    existingUser.setFunctions(currentAssignments);

    UserDto requestDto = new UserDto();
    requestDto.setFirstName("Updated John");
    Set<UserFunction> newFunctions = new HashSet<>();
    newFunctions.add(UserFunction.CONSUMER);
    requestDto.setFunctions(newFunctions);

    User updatedUser = new User();
    updatedUser.setId(userId);
    updatedUser.setFirstName("Updated John");

    UserDto expectedDto = new UserDto();
    expectedDto.setId(userId);
    expectedDto.setFirstName("Updated John");

    when(userRepository.findByIdOrThrow(userId)).thenReturn(existingUser);
    when(userRepository.save(existingUser)).thenReturn(updatedUser);
    when(userMapper.toDto(updatedUser)).thenReturn(expectedDto);

    // When
    UserDto result = userManagementService.updateUser(userId, requestDto);

    // Then
    assertEquals(expectedDto, result);
    assertEquals("Updated John", existingUser.getFirstName());
    verify(userRepository).save(existingUser);
  }

  @Test
  @DisplayName("Should update user with phone and address data successfully")
  void shouldUpdateUserWithPhoneAndAddressDataSuccessfully() {
    // Given
    final UUID userId = UUID.randomUUID();
    User existingUser = new User();
    existingUser.setId(userId);
    existingUser.setStatus(UserStatus.ACTIVE);

    UserDto requestDto = new UserDto();
    requestDto.setFirstName("Updated John");

    List<Phone> phoneData = new ArrayList<>();
    Phone phone = new Phone();
    phone.setCode("+1");
    phone.setNumber("123-456-7890");
    phoneData.add(phone);
    requestDto.setPhoneData(phoneData);

    final List<Address> addressData = new ArrayList<>();
    Address address = new Address();
    address.setCountry("USA");
    address.setStreet("123 Main St");
    address.setCity("New York");
    address.setState("NY");
    address.setBuilding("Apt 1");
    addressData.add(address);
    requestDto.setAddressData(addressData);

    User updatedUser = new User();
    updatedUser.setId(userId);
    updatedUser.setFirstName("Updated John");
    updatedUser.setPhoneData(phoneData);
    updatedUser.setAddressData(addressData);

    UserDto expectedDto = new UserDto();
    expectedDto.setId(userId);
    expectedDto.setFirstName("Updated John");

    when(userRepository.findByIdOrThrow(userId)).thenReturn(existingUser);
    when(userRepository.save(existingUser)).thenReturn(updatedUser);
    when(userMapper.toDto(updatedUser)).thenReturn(expectedDto);

    // When
    UserDto result = userManagementService.updateUser(userId, requestDto);

    // Then
    assertEquals(expectedDto, result);
    assertEquals("Updated John", existingUser.getFirstName());
    assertEquals(phoneData, existingUser.getPhoneData());
    assertEquals(addressData, existingUser.getAddressData());
  }

  @Test
  @DisplayName("Should deactivate user successfully")
  void shouldDeactivateUserSuccessfully() {
    // Given
    UUID userId = UUID.randomUUID();
    User existingUser = new User();
    existingUser.setId(userId);
    existingUser.setStatus(UserStatus.ACTIVE);

    User deactivatedUser = new User();
    deactivatedUser.setId(userId);
    deactivatedUser.setStatus(UserStatus.INACTIVE);

    UserDto expectedDto = new UserDto();
    expectedDto.setId(userId);
    expectedDto.setStatus(UserStatus.INACTIVE);

    when(userRepository.findByIdOrThrow(userId)).thenReturn(existingUser);
    when(userRepository.save(existingUser)).thenReturn(deactivatedUser);
    when(userMapper.toDto(deactivatedUser)).thenReturn(expectedDto);

    // When
    UserDto result = userManagementService.deactivateUser(userId);

    // Then
    assertEquals(expectedDto, result);
    assertEquals(UserStatus.INACTIVE, existingUser.getStatus());
  }

  @Test
  @DisplayName("Should reactivate user successfully")
  void shouldReactivateUserSuccessfully() {
    // Given
    UUID userId = UUID.randomUUID();
    User existingUser = new User();
    existingUser.setId(userId);
    existingUser.setStatus(UserStatus.INACTIVE);

    User reactivatedUser = new User();
    reactivatedUser.setId(userId);
    reactivatedUser.setStatus(UserStatus.ACTIVE);

    UserDto expectedDto = new UserDto();
    expectedDto.setId(userId);
    expectedDto.setStatus(UserStatus.ACTIVE);

    when(userRepository.findByIdOrThrow(userId)).thenReturn(existingUser);
    when(userRepository.save(existingUser)).thenReturn(reactivatedUser);
    when(userMapper.toDto(reactivatedUser)).thenReturn(expectedDto);

    // When
    UserDto result = userManagementService.reactivateUser(userId);

    // Then
    assertEquals(expectedDto, result);
    assertEquals(UserStatus.ACTIVE, existingUser.getStatus());
  }

  @Test
  @DisplayName("Should deactivate user successfully - fixed mocking")
  void shouldDeactivateUserSuccessfullyFixedMocking() {
    // Given
    UUID userId = UUID.randomUUID();
    User existingUser = new User();
    existingUser.setId(userId);
    existingUser.setStatus(UserStatus.ACTIVE);

    UserDto expectedDto = new UserDto();
    expectedDto.setId(userId);
    expectedDto.setStatus(UserStatus.INACTIVE);

    when(userRepository.findByIdOrThrow(userId)).thenReturn(existingUser);
    when(userRepository.save(existingUser)).thenReturn(existingUser);
    when(userMapper.toDto(existingUser)).thenReturn(expectedDto);

    // When
    UserDto result = userManagementService.deactivateUser(userId);

    // Then
    assertEquals(expectedDto, result);
    assertEquals(UserStatus.INACTIVE, existingUser.getStatus());
    verify(userRepository).save(existingUser);
  }

  @Test
  @DisplayName("Should reactivate user successfully - fixed mocking")
  void shouldReactivateUserSuccessfullyFixedMocking() {
    // Given
    UUID userId = UUID.randomUUID();
    User existingUser = new User();
    existingUser.setId(userId);
    existingUser.setStatus(UserStatus.INACTIVE);

    UserDto expectedDto = new UserDto();
    expectedDto.setId(userId);
    expectedDto.setStatus(UserStatus.ACTIVE);

    when(userRepository.findByIdOrThrow(userId)).thenReturn(existingUser);
    when(userRepository.save(existingUser)).thenReturn(existingUser);
    when(userMapper.toDto(existingUser)).thenReturn(expectedDto);

    // When
    UserDto result = userManagementService.reactivateUser(userId);

    // Then
    assertEquals(expectedDto, result);
    assertEquals(UserStatus.ACTIVE, existingUser.getStatus());
    verify(userRepository).save(existingUser);
  }

  @Test
  @DisplayName("Should deactivate user successfully - working test")
  void shouldDeactivateUserSuccessfullyWorkingTest() {
    // Given
    UUID userId = UUID.randomUUID();
    User existingUser = new User();
    existingUser.setId(userId);
    existingUser.setStatus(UserStatus.ACTIVE);

    UserDto expectedDto = new UserDto();
    expectedDto.setId(userId);
    expectedDto.setStatus(UserStatus.INACTIVE);

    when(userRepository.findByIdOrThrow(userId)).thenReturn(existingUser);
    when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
      User user = invocation.getArgument(0);
      return user; // Return the same object that was passed in
    });
    when(userMapper.toDto(existingUser)).thenReturn(expectedDto);

    // When
    UserDto result = userManagementService.deactivateUser(userId);

    // Then
    assertEquals(expectedDto, result);
    assertEquals(UserStatus.INACTIVE, existingUser.getStatus());
    verify(userRepository).save(existingUser);
  }

  @Test
  @DisplayName("Should reactivate user successfully - working test")
  void shouldReactivateUserSuccessfullyWorkingTest() {
    // Given
    UUID userId = UUID.randomUUID();
    User existingUser = new User();
    existingUser.setId(userId);
    existingUser.setStatus(UserStatus.INACTIVE);

    UserDto expectedDto = new UserDto();
    expectedDto.setId(userId);
    expectedDto.setStatus(UserStatus.ACTIVE);

    when(userRepository.findByIdOrThrow(userId)).thenReturn(existingUser);
    when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
      User user = invocation.getArgument(0);
      return user; // Return the same object that was passed in
    });
    when(userMapper.toDto(existingUser)).thenReturn(expectedDto);

    // When
    UserDto result = userManagementService.reactivateUser(userId);

    // Then
    assertEquals(expectedDto, result);
    assertEquals(UserStatus.ACTIVE, existingUser.getStatus());
    verify(userRepository).save(existingUser);
  }

  @Test
  @DisplayName("Should throw exception when deactivating already inactive user")
  void shouldThrowExceptionWhenDeactivatingAlreadyInactiveUser() {
    // Given
    UUID userId = UUID.randomUUID();
    User existingUser = new User();
    existingUser.setId(userId);
    existingUser.setStatus(UserStatus.INACTIVE);

    when(userRepository.findByIdOrThrow(userId)).thenReturn(existingUser);

    // When & Then
    ResponseStatusException exception = assertThrows(ResponseStatusException.class,
        () -> userManagementService.deactivateUser(userId));
    assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    assertEquals("User is already inactive", exception.getReason());
  }

  @Test
  @DisplayName("Should throw exception when reactivating already active user")
  void shouldThrowExceptionWhenReactivatingAlreadyActiveUser() {
    // Given
    UUID userId = UUID.randomUUID();
    User existingUser = new User();
    existingUser.setId(userId);
    existingUser.setStatus(UserStatus.ACTIVE);

    when(userRepository.findByIdOrThrow(userId)).thenReturn(existingUser);

    // When & Then
    ResponseStatusException exception = assertThrows(ResponseStatusException.class,
        () -> userManagementService.reactivateUser(userId));
    assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    assertEquals("User is already active", exception.getReason());
  }

  @Test
  @DisplayName("Should handle null functions in create user")
  void shouldHandleNullFunctionsInCreateUser() {
    // Given
    UserDto requestDto = new UserDto();
    requestDto.setFirstName("John");
    requestDto.setFunctions(null);

    User user = new User();
    user.setFirstName("John");
    user.setFunctions(null);

    User savedUser = new User();
    savedUser.setId(UUID.randomUUID());

    UserDto expectedDto = new UserDto();
    expectedDto.setId(savedUser.getId());

    when(userMapper.toEntity(requestDto)).thenReturn(user);
    when(userRepository.save(user)).thenReturn(savedUser);
    when(userMapper.toDto(savedUser)).thenReturn(expectedDto);

    // When
    UserDto result = userManagementService.createUser(requestDto);

    // Then
    assertEquals(expectedDto, result);
    verify(userRepository).save(user);
  }

  @Test
  @DisplayName("Should handle null functions in update user")
  void shouldHandleNullFunctionsInUpdateUser() {
    // Given
    UUID userId = UUID.randomUUID();
    UserDto requestDto = new UserDto();
    requestDto.setFirstName("John");
    requestDto.setFunctions(null);

    User existingUser = new User();
    existingUser.setId(userId);
    existingUser.setStatus(UserStatus.ACTIVE);

    User updatedUser = new User();
    updatedUser.setId(userId);

    UserDto expectedDto = new UserDto();
    expectedDto.setId(userId);

    when(userRepository.findByIdOrThrow(userId)).thenReturn(existingUser);
    when(userRepository.save(existingUser)).thenReturn(updatedUser);
    when(userMapper.toDto(updatedUser)).thenReturn(expectedDto);

    // When
    UserDto result = userManagementService.updateUser(userId, requestDto);

    // Then
    assertEquals(expectedDto, result);
    verify(userRepository).save(existingUser);
  }

  @Test
  @DisplayName("Should handle empty functions set in update user")
  void shouldHandleEmptyFunctionsSetInUpdateUser() {
    // Given
    UUID userId = UUID.randomUUID();
    UserDto requestDto = new UserDto();
    requestDto.setFirstName("John");
    requestDto.setFunctions(new HashSet<>());

    User existingUser = new User();
    existingUser.setId(userId);
    existingUser.setStatus(UserStatus.ACTIVE);

    Set<UserFunctionAssignment> currentAssignments = new HashSet<>();
    UserFunctionAssignment existingAssignment = new UserFunctionAssignment();
    existingAssignment.setFunction(UserFunction.COMPANY_MANAGER);
    existingAssignment.setUser(existingUser);
    currentAssignments.add(existingAssignment);
    existingUser.setFunctions(currentAssignments);

    User updatedUser = new User();
    updatedUser.setId(userId);
    updatedUser.setFunctions(new HashSet<>());

    UserDto expectedDto = new UserDto();
    expectedDto.setId(userId);

    when(userRepository.findByIdOrThrow(userId)).thenReturn(existingUser);
    when(userRepository.save(existingUser)).thenReturn(updatedUser);
    when(userMapper.toDto(updatedUser)).thenReturn(expectedDto);

    // When
    UserDto result = userManagementService.updateUser(userId, requestDto);

    // Then
    assertEquals(expectedDto, result);
    verify(userRepository).save(existingUser);
  }

  @Test
  @DisplayName("Should update Auth0 user ID successfully")
  void shouldUpdateAuth0UserIdSuccessfully() {
    // Given
    final UUID userId = UUID.randomUUID();
    final String auth0UserId = "auth0|123456789";
    
    User existingUser = new User();
    existingUser.setId(userId);
    existingUser.setFirstName("John");
    existingUser.setLastName("Doe");
    existingUser.setEmail("john.doe@example.com");
    existingUser.setAuth0UserId("old-auth0-id");

    User updatedUser = new User();
    updatedUser.setId(userId);
    updatedUser.setFirstName("John");
    updatedUser.setLastName("Doe");
    updatedUser.setEmail("john.doe@example.com");
    updatedUser.setAuth0UserId(auth0UserId);

    UserDto expectedDto = new UserDto();
    expectedDto.setId(userId);
    expectedDto.setFirstName("John");
    expectedDto.setLastName("Doe");
    expectedDto.setEmail("john.doe@example.com");
    expectedDto.setAuth0UserId(auth0UserId);

    when(userRepository.findByIdOrThrow(userId)).thenReturn(existingUser);
    when(userRepository.save(existingUser)).thenReturn(updatedUser);
    when(userMapper.toDto(updatedUser)).thenReturn(expectedDto);

    // When
    UserDto result = userManagementService.updateAuth0UserId(userId, auth0UserId);

    // Then
    assertEquals(expectedDto, result);
    assertEquals(auth0UserId, existingUser.getAuth0UserId());
    verify(userRepository).findByIdOrThrow(userId);
    verify(userRepository).save(existingUser);
    verify(userMapper).toDto(updatedUser);
  }

  @Test
  @DisplayName("Should update Auth0 user ID when user has no existing Auth0 ID")
  void shouldUpdateAuth0UserIdWhenUserHasNoExistingAuth0Id() {
    // Given
    final UUID userId = UUID.randomUUID();
    final String auth0UserId = "auth0|987654321";
    
    User existingUser = new User();
    existingUser.setId(userId);
    existingUser.setFirstName("Jane");
    existingUser.setLastName("Smith");
    existingUser.setEmail("jane.smith@example.com");
    existingUser.setAuth0UserId(null);

    User updatedUser = new User();
    updatedUser.setId(userId);
    updatedUser.setFirstName("Jane");
    updatedUser.setLastName("Smith");
    updatedUser.setEmail("jane.smith@example.com");
    updatedUser.setAuth0UserId(auth0UserId);

    UserDto expectedDto = new UserDto();
    expectedDto.setId(userId);
    expectedDto.setFirstName("Jane");
    expectedDto.setLastName("Smith");
    expectedDto.setEmail("jane.smith@example.com");
    expectedDto.setAuth0UserId(auth0UserId);

    when(userRepository.findByIdOrThrow(userId)).thenReturn(existingUser);
    when(userRepository.save(existingUser)).thenReturn(updatedUser);
    when(userMapper.toDto(updatedUser)).thenReturn(expectedDto);

    // When
    UserDto result = userManagementService.updateAuth0UserId(userId, auth0UserId);

    // Then
    assertEquals(expectedDto, result);
    assertEquals(auth0UserId, existingUser.getAuth0UserId());
    verify(userRepository).findByIdOrThrow(userId);
    verify(userRepository).save(existingUser);
    verify(userMapper).toDto(updatedUser);
  }

  @Test
  @DisplayName("Should update Auth0 user ID with empty string")
  void shouldUpdateAuth0UserIdWithEmptyString() {
    // Given
    final UUID userId = UUID.randomUUID();
    final String auth0UserId = "";
    
    User existingUser = new User();
    existingUser.setId(userId);
    existingUser.setFirstName("Bob");
    existingUser.setLastName("Johnson");
    existingUser.setEmail("bob.johnson@example.com");
    existingUser.setAuth0UserId("existing-auth0-id");

    User updatedUser = new User();
    updatedUser.setId(userId);
    updatedUser.setFirstName("Bob");
    updatedUser.setLastName("Johnson");
    updatedUser.setEmail("bob.johnson@example.com");
    updatedUser.setAuth0UserId(auth0UserId);

    UserDto expectedDto = new UserDto();
    expectedDto.setId(userId);
    expectedDto.setFirstName("Bob");
    expectedDto.setLastName("Johnson");
    expectedDto.setEmail("bob.johnson@example.com");
    expectedDto.setAuth0UserId(auth0UserId);

    when(userRepository.findByIdOrThrow(userId)).thenReturn(existingUser);
    when(userRepository.save(existingUser)).thenReturn(updatedUser);
    when(userMapper.toDto(updatedUser)).thenReturn(expectedDto);

    // When
    UserDto result = userManagementService.updateAuth0UserId(userId, auth0UserId);

    // Then
    assertEquals(expectedDto, result);
    assertEquals(auth0UserId, existingUser.getAuth0UserId());
    verify(userRepository).findByIdOrThrow(userId);
    verify(userRepository).save(existingUser);
    verify(userMapper).toDto(updatedUser);
  }

  @Test
  @DisplayName("Should update Auth0 user ID with null value")
  void shouldUpdateAuth0UserIdWithNullValue() {
    // Given
    final UUID userId = UUID.randomUUID();
    final String auth0UserId = null;
    
    User existingUser = new User();
    existingUser.setId(userId);
    existingUser.setFirstName("Alice");
    existingUser.setLastName("Brown");
    existingUser.setEmail("alice.brown@example.com");
    existingUser.setAuth0UserId("existing-auth0-id");

    User updatedUser = new User();
    updatedUser.setId(userId);
    updatedUser.setFirstName("Alice");
    updatedUser.setLastName("Brown");
    updatedUser.setEmail("alice.brown@example.com");
    updatedUser.setAuth0UserId(auth0UserId);

    UserDto expectedDto = new UserDto();
    expectedDto.setId(userId);
    expectedDto.setFirstName("Alice");
    expectedDto.setLastName("Brown");
    expectedDto.setEmail("alice.brown@example.com");
    expectedDto.setAuth0UserId(auth0UserId);

    when(userRepository.findByIdOrThrow(userId)).thenReturn(existingUser);
    when(userRepository.save(existingUser)).thenReturn(updatedUser);
    when(userMapper.toDto(updatedUser)).thenReturn(expectedDto);

    // When
    UserDto result = userManagementService.updateAuth0UserId(userId, auth0UserId);

    // Then
    assertEquals(expectedDto, result);
    assertEquals(auth0UserId, existingUser.getAuth0UserId());
    verify(userRepository).findByIdOrThrow(userId);
    verify(userRepository).save(existingUser);
    verify(userMapper).toDto(updatedUser);
  }
}
