package com.coherentsolutions.pot.insuranceservice.unit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.coherentsolutions.pot.insuranceservice.dto.user.UserDto;
import com.coherentsolutions.pot.insuranceservice.dto.user.UserFilter;
import com.coherentsolutions.pot.insuranceservice.enums.UserStatus;
import com.coherentsolutions.pot.insuranceservice.mapper.UserMapper;
import com.coherentsolutions.pot.insuranceservice.model.Address;
import com.coherentsolutions.pot.insuranceservice.model.Company;
import com.coherentsolutions.pot.insuranceservice.model.Phone;
import com.coherentsolutions.pot.insuranceservice.model.User;
import com.coherentsolutions.pot.insuranceservice.repository.UserRepository;
import com.coherentsolutions.pot.insuranceservice.service.UserManagementService;
import java.util.List;
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

  @Mock
  private UserRepository userRepository;

  @Mock
  private UserMapper userMapper;

  @InjectMocks
  private UserManagementService userManagementService;

  private User user;
  private UUID userId;

  @BeforeEach
  void setUp() {
    userId = UUID.randomUUID();
    user = new User();
    user.setId(userId);
    user.setFirstName("Old");
    user.setLastName("Name");
    user.setUsername("old_username");
    user.setEmail("old@email.com");
    user.setStatus(UserStatus.ACTIVE);
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

    when(userRepository.findByIdOrThrow(userId)).thenReturn(user);
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
    verify(userRepository).findByIdOrThrow(userId);
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

    when(userRepository.findByIdOrThrow(userId)).thenReturn(user);
    when(userRepository.save(any(User.class))).thenReturn(user);
    when(userMapper.toDto(any(User.class))).thenReturn(requestDto);

    // When
    UserDto result = userManagementService.updateUser(userId, requestDto);

    // Then
    assertEquals(phoneDtos, result.getPhoneData());
    assertEquals(addressDtos, result.getAddressData());
    verify(userRepository).save(user);
    verify(userRepository).findByIdOrThrow(userId);
    verify(userMapper).toDto(user);
  }

  @Test
  @DisplayName("Should throw ResponseStatusException when attempting to update non-existent user")
  void shouldThrowExceptionWhenUserNotFound() {
    // Given
    when(userRepository.findByIdOrThrow(userId))
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

    when(userRepository.findByIdOrThrow(userId)).thenReturn(activeUser);
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

    when(userRepository.findByIdOrThrow(userId)).thenReturn(inactiveUser);
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

    when(userRepository.findByIdOrThrow(userId)).thenReturn(user);

    // When // Then
    ResponseStatusException ex = assertThrows(ResponseStatusException.class,
        () -> userManagementService.deactivateUser(userId));

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

    UserDto testUserDto1 = UserDto.builder()
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

    UserDto testUserDto2 = UserDto.builder()
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

}
