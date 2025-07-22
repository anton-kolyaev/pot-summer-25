package com.coherentsolutions.pot.insuranceservice.unit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.coherentsolutions.pot.insuranceservice.dto.user.UserDto;
import com.coherentsolutions.pot.insuranceservice.dto.user.UserFilter;
import com.coherentsolutions.pot.insuranceservice.enums.UserFunction;
import com.coherentsolutions.pot.insuranceservice.enums.UserStatus;
import com.coherentsolutions.pot.insuranceservice.mapper.UserMapper;
import com.coherentsolutions.pot.insuranceservice.model.Address;
import com.coherentsolutions.pot.insuranceservice.model.Company;
import com.coherentsolutions.pot.insuranceservice.model.Phone;
import com.coherentsolutions.pot.insuranceservice.model.User;
import com.coherentsolutions.pot.insuranceservice.repository.UserRepository;
import com.coherentsolutions.pot.insuranceservice.service.UserManagementService;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * Tests that updating core user fields (firstName, lastName, username, email) works correctly when
 * the user exists in the repository.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("User Company Management Service Tests")
public class UserManagementServiceTest {

  @Mock private UserRepository userRepository;

  @Mock private UserMapper userMapper;

  @InjectMocks private UserManagementService userManagementService;

  private User user;
  private UUID userId;
  private UserDto testUserDto;
  private UUID testUserId;

  @BeforeEach
  void setUp() {
    testUserId = UUID.randomUUID();
    userId = UUID.randomUUID();
    user = new User();
    user.setId(userId);
    user.setFirstName("Old");
    user.setLastName("Name");
    user.setUsername("old_username");
    user.setEmail("old@email.com");
    user.setStatus(UserStatus.ACTIVE);

    testUserDto =
        UserDto.builder()
            .id(testUserId)
            .firstName("Test User")
            .email("test@user.com")
            .dateOfBirth(LocalDate.of(1990, 1, 1))
            .status(UserStatus.ACTIVE)
            .ssn("123-45-6789")
            .functions(Set.of(UserFunction.COMPANY_MANAGER))
            .build();
  }

  @Test
  @DisplayName("Should update core user fields when user exists")
  void shouldUpdateUserFieldsSuccessfully() {
    // Given
    UserDto requestDto = new UserDto();
    requestDto.setFirstName("New");
    requestDto.setLastName("User");
    requestDto.setUsername("new_username");
    requestDto.setEmail("new@email.com");

    when(userRepository.getByIdOrThrow(userId)).thenReturn(user);
    when(userRepository.save(any(User.class))).thenReturn(user);
    when(userMapper.toDto(user)).thenReturn(requestDto);

    // When
    UserDto result = userManagementService.updateUser(userId, requestDto);

    // Then
    assertEquals("new@email.com", result.getEmail());
    assertEquals("New", result.getFirstName());
    assertEquals("User", result.getLastName());
    assertEquals("new_username", result.getUsername());
    verify(userRepository).save(user);
    verify(userRepository).getByIdOrThrow(userId);
    verify(userMapper).toDto(user);
  }

  @Test
  @DisplayName("Should update phone and address data when present in request")
  void shouldUpdatePhoneAndAddressData() {
    // Given
    List<Phone> phoneDtos = List.of(new Phone());
    List<Address> addressDtos = List.of(new Address());

    UserDto requestDto = new UserDto();
    requestDto.setPhoneData(phoneDtos);
    requestDto.setAddressData(addressDtos);

    when(userRepository.getByIdOrThrow(userId)).thenReturn(user);
    when(userRepository.save(any(User.class))).thenReturn(user);
    when(userMapper.toDto(any(User.class))).thenReturn(requestDto);

    // When
    UserDto result = userManagementService.updateUser(userId, requestDto);

    // Then
    assertEquals(phoneDtos, result.getPhoneData());
    assertEquals(addressDtos, result.getAddressData());
    verify(userRepository).save(user);
    verify(userRepository).getByIdOrThrow(userId);
    verify(userMapper).toDto(user);
  }

  @Test
  @DisplayName("Should throw ResponseStatusException when attempting to update non-existent user")
  void shouldThrowExceptionWhenUserNotFound() {
    // Given
    when(userRepository.getByIdOrThrow(userId))
        .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

    // When // Then
    assertThrows(
        ResponseStatusException.class,
        () -> userManagementService.updateUser(userId, new UserDto()));
  }

  @Test
  @DisplayName("Should deactivate active user successfully")
  void shouldDeactivateUserSuccessfully() {
    // Given
    User activeUser = new User();
    activeUser.setId(userId);
    activeUser.setStatus(UserStatus.ACTIVE);

    UserDto expectedDto = new UserDto();
    expectedDto.setStatus(UserStatus.INACTIVE);

    when(userRepository.getByIdOrThrow(userId)).thenReturn(activeUser);
    when(userRepository.save(any(User.class))).thenReturn(activeUser);
    when(userMapper.toDto(activeUser)).thenReturn(expectedDto);

    // When
    UserDto result = userManagementService.deactivateUser(userId);

    // Then
    assertEquals(UserStatus.INACTIVE, result.getStatus());
    verify(userRepository).save(activeUser);
    verify(userMapper).toDto(activeUser);
  }

  @Test
  @DisplayName("Should reactivate inactive user successfully")
  void shouldReactivateUserSuccessfully() {
    // Given
    User inactiveUser = new User();
    inactiveUser.setId(userId);
    inactiveUser.setStatus(UserStatus.INACTIVE);

    UserDto expectedDto = new UserDto();
    expectedDto.setStatus(UserStatus.ACTIVE);

    when(userRepository.getByIdOrThrow(userId)).thenReturn(inactiveUser);
    when(userRepository.save(any(User.class))).thenReturn(inactiveUser);
    when(userMapper.toDto(inactiveUser)).thenReturn(expectedDto);

    // When
    UserDto result = userManagementService.reactivateUser(userId);

    // Then
    assertEquals(UserStatus.ACTIVE, result.getStatus());
    verify(userRepository).save(inactiveUser);
    verify(userMapper).toDto(inactiveUser);
  }

  @Test
  @DisplayName("Should throw BAD_REQUEST when deactivating already inactive user")
  void shouldThrowWhenUserAlreadyInactive() {
    // Given
    User user = new User();
    user.setId(userId);
    user.setStatus(UserStatus.INACTIVE);

    when(userRepository.getByIdOrThrow(userId)).thenReturn(user);

    // When // Then
    ResponseStatusException ex =
        assertThrows(
            ResponseStatusException.class, () -> userManagementService.deactivateUser(userId));

    assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    assertEquals("User is already inactive", ex.getReason());
  }

  @Test
  @DisplayName("Should return all users of a company by companyId")
  void shouldReturnAllUsersOfExistingCompany() {
    UUID companyId = UUID.randomUUID();
    Company mockCompany = new Company();
    mockCompany.setId(companyId);

    User user1 = new User();
    user1.setId(UUID.randomUUID());
    user1.setFirstName("Alice");
    user1.setLastName("Johnson");
    user1.setUsername("alice.johnson");
    user1.setEmail("alice@example.com");
    user1.setCompany(mockCompany);
    user1.setStatus(UserStatus.ACTIVE);

    User user2 = new User();
    user2.setId(UUID.randomUUID());
    user2.setFirstName("Bob");
    user2.setLastName("Smith");
    user2.setUsername("bob.smith");
    user2.setEmail("bob.smith@example.com");
    user2.setCompany(mockCompany);
    user2.setStatus(UserStatus.ACTIVE);

    List<User> users = List.of(user1, user2);

    UserDto testUserDto1 =
        UserDto.builder()
            .id(user1.getId())
            .firstName(user1.getFirstName())
            .lastName(user1.getLastName())
            .email(user1.getEmail())
            .username(user1.getUsername())
            .companyId(companyId)
            .status(user1.getStatus())
            .build();

    UserFilter filter = new UserFilter();
    filter.setCompanyId(companyId);

    Pageable pageable = Pageable.unpaged();

    UserDto testUserDto2 =
        UserDto.builder()
            .id(user2.getId())
            .firstName(user2.getFirstName())
            .lastName(user2.getLastName())
            .email(user2.getEmail())
            .username(user2.getUsername())
            .companyId(companyId)
            .status(user2.getStatus())
            .build();

    when(userRepository.findAll(Mockito.<Specification<User>>any(), Mockito.eq(pageable)))
        .thenReturn(new PageImpl<>(users));

    when(userMapper.toDto(user1)).thenReturn(testUserDto1);
    when(userMapper.toDto(user2)).thenReturn(testUserDto2);

    Page<UserDto> result = userManagementService.getUsersWithFilters(filter, pageable);

    Assertions.assertNotNull(result);
    Assertions.assertEquals(2, result.getTotalElements());
    Assertions.assertEquals("alice.johnson", result.getContent().get(0).getUsername());
    Assertions.assertEquals("bob.smith", result.getContent().get(1).getUsername());

    verify(userRepository).findAll(Mockito.<Specification<User>>any(), Mockito.eq(pageable));
    verify(userMapper).toDto(user1);
    verify(userMapper).toDto(user2);
  }

  @Test
  @DisplayName("Should return empty result when no users match the companyId")
  void shouldReturnEmptyPage() {

    UUID nonExistentCompanyId = UUID.randomUUID();
    UserFilter filter = new UserFilter();
    filter.setCompanyId(nonExistentCompanyId);

    Pageable pageable = Pageable.unpaged();

    when(userRepository.findAll(Mockito.<Specification<User>>any(), Mockito.eq(pageable)))
        .thenReturn(Page.empty());

    Page<UserDto> result = userManagementService.getUsersWithFilters(filter, pageable);

    Assertions.assertNotNull(result, "Result should not be null");
    Assertions.assertTrue(result.isEmpty(), "");

    verify(userRepository).findAll(Mockito.<Specification<User>>any(), Mockito.eq(pageable));
    verify(userMapper, times(0)).toDto(Mockito.any());
  }

  @Test
  @DisplayName("Should get user details by ID")
  void shouldGetUserDetailsById() {
    // Given
    when(userRepository.findByIdOrThrow(testUserId)).thenReturn(user);
    when(userMapper.toDto(user)).thenReturn(testUserDto);

    // When
    UserDto result = userManagementService.getUsersDetails(testUserId);

    // Then
    assertNotNull(result);
    assertEquals(testUserDto, result);
    verify(userRepository).findByIdOrThrow(testUserId);
    verify(userMapper).toDto(user);
  }

  @Test
  @DisplayName("Should throw ResponseStatusException when user not found by ID")
  void shouldThrowExceptionWhenUserDetailsNotFound() {
    // Given
    UUID notFoundId = UUID.randomUUID();
    when(userRepository.findByIdOrThrow(notFoundId))
        .thenThrow(
            new ResponseStatusException(
                org.springframework.http.HttpStatus.NOT_FOUND, "User not found"));

    // When / Then
    assertThrows(
        ResponseStatusException.class, () -> userManagementService.getUsersDetails(notFoundId));
    verify(userRepository).findByIdOrThrow(notFoundId);
  }

  @Test
  @DisplayName("Should create user and return UserDto")
  void shouldCreateUserSuccessfully() {
    // Given
    User mappedUser = new User();
    mappedUser.setFirstName("Create");
    mappedUser.setLastName("User");
    mappedUser.setEmail("create@user.com");
    UserDto createDto =
        UserDto.builder()
            .firstName("Create")
            .lastName("User")
            .email("create@user.com")
            .functions(Set.of(UserFunction.COMPANY_MANAGER))
            .build();
    when(userMapper.toEntity(createDto)).thenReturn(mappedUser);
    when(userRepository.save(mappedUser)).thenReturn(mappedUser);
    when(userMapper.toDto(mappedUser)).thenReturn(createDto);

    // When
    UserDto result = userManagementService.createUser(createDto);

    // Then
    assertNotNull(result);
    assertEquals("Create", result.getFirstName());
    assertEquals("User", result.getLastName());
    assertEquals("create@user.com", result.getEmail());
    verify(userMapper).toEntity(createDto);
    verify(userRepository).save(mappedUser);
    verify(userMapper).toDto(mappedUser);
  }

  @Test
  @DisplayName("Should not call findByIdOrThrow when creating a user")
  void shouldNotCallFindByIdOrThrowOnCreateUser() {
    // Given
    UserDto createDto =
        UserDto.builder()
            .firstName("Create")
            .lastName("User")
            .functions(Set.of(UserFunction.COMPANY_MANAGER))
            .build();
    User mappedUser = new User();
    mappedUser.setFirstName("Create");
    mappedUser.setLastName("User");
    when(userMapper.toEntity(createDto)).thenReturn(mappedUser);
    when(userRepository.save(mappedUser)).thenReturn(mappedUser);
    when(userMapper.toDto(mappedUser)).thenReturn(createDto);

    // When
    userManagementService.createUser(createDto);

    // Then
    verify(userMapper).toEntity(createDto);
    verify(userRepository).save(mappedUser);
    verify(userMapper).toDto(mappedUser);
    // Ensure findByIdOrThrow is never called during create
    org.mockito.Mockito.verify(userRepository, org.mockito.Mockito.never())
        .findByIdOrThrow(any(UUID.class));
  }

  @Test
  @DisplayName("Should throw exception when getUsersDetails is called with non-existent user")
  void shouldThrowExceptionWhenGetUsersDetailsWithNonExistentUser() {
    // Given
    UUID notFoundId = UUID.randomUUID();
    when(userRepository.findByIdOrThrow(notFoundId))
        .thenThrow(
            new ResponseStatusException(
                org.springframework.http.HttpStatus.NOT_FOUND, "User not found"));

    // When / Then
    assertThrows(
        ResponseStatusException.class, () -> userManagementService.getUsersDetails(notFoundId));
    verify(userRepository).findByIdOrThrow(notFoundId);
  }

  @Test
  @DisplayName("Should update user functions correctly")
  void shouldUpdateUserFunctions() {
    // Given
    User existingUser = new User();
    existingUser.setId(userId);
    existingUser.setStatus(UserStatus.ACTIVE);
    // Simulate existing function assignments
    Set<com.coherentsolutions.pot.insuranceservice.model.UserFunctionAssignment> assignments =
        new java.util.HashSet<>();
    com.coherentsolutions.pot.insuranceservice.model.UserFunctionAssignment assignment =
        new com.coherentsolutions.pot.insuranceservice.model.UserFunctionAssignment();
    assignment.setFunction(UserFunction.COMPANY_MANAGER);
    assignment.setUser(existingUser);
    assignments.add(assignment);
    existingUser.setFunctions(assignments);

    UserDto requestDto =
        UserDto.builder()
            .functions(Set.of(UserFunction.COMPANY_MANAGER, UserFunction.CONSUMER_CLAIM_MANAGER))
            .build();

    when(userRepository.getByIdOrThrow(userId)).thenReturn(existingUser);
    when(userRepository.save(any(User.class))).thenReturn(existingUser);
    when(userMapper.toDto(any(User.class))).thenReturn(requestDto);

    // When
    UserDto result = userManagementService.updateUser(userId, requestDto);

    // Then
    assertNotNull(result);
    assertEquals(2, result.getFunctions().size());
    verify(userRepository).save(existingUser);
    verify(userRepository).getByIdOrThrow(userId);
    verify(userMapper).toDto(existingUser);
  }
}
