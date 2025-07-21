package com.coherentsolutions.pot.insurance_service.unit.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.web.server.ResponseStatusException;

import com.coherentsolutions.pot.insurance_service.dto.user.UserDto;
import com.coherentsolutions.pot.insurance_service.enums.UserFunction;
import com.coherentsolutions.pot.insurance_service.enums.UserStatus;
import com.coherentsolutions.pot.insurance_service.mapper.UserMapper;
import com.coherentsolutions.pot.insurance_service.model.Address;
import com.coherentsolutions.pot.insurance_service.model.Phone;
import com.coherentsolutions.pot.insurance_service.model.User;
import com.coherentsolutions.pot.insurance_service.repository.UserRepository;
import com.coherentsolutions.pot.insurance_service.service.UserManagementService;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class UserManagementServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserManagementService userManagementService;

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

        testUserDto = UserDto.builder()
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

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toDto(any(User.class))).thenReturn(requestDto);

        // When
        UserDto result = userManagementService.updateUser(userId, requestDto);

        // Then
        assertEquals("new@email.com", result.getEmail());
        assertEquals("New", result.getFirstName());
        assertEquals("User", result.getLastName());
        assertEquals("new_username", result.getUsername());
        verify(userRepository).save(user);
        verify(userRepository).findById(userId);
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

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toDto(any(User.class))).thenReturn(requestDto);

        // When
        UserDto result = userManagementService.updateUser(userId, requestDto);

        // Then
        assertEquals(phoneDtos, result.getPhoneData());
        assertEquals(addressDtos, result.getAddressData());
        verify(userRepository).save(user);
        verify(userRepository).findById(userId);
        verify(userMapper).toDto(user);
    }

    @Test
    @DisplayName("Should throw ResponseStatusException when attempting to update non-existent user")
    void shouldThrowExceptionWhenUserNotFound() {
        // Given
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When // Then
        assertThrows(
                ResponseStatusException.class,
                () -> userManagementService.updateUser(userId, new UserDto()));
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
        when(userRepository.findByIdOrThrow(notFoundId)).thenThrow(
                new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "User not found"));

        // When / Then
        assertThrows(ResponseStatusException.class, () -> userManagementService.getUsersDetails(notFoundId));
        verify(userRepository).findByIdOrThrow(notFoundId);
    }

    @Test
    @DisplayName("Should create user and return UserDto")
    void shouldCreateUserSuccessfully() {
        // Given
        UserDto createDto = UserDto.builder()
                .firstName("Create")
                .lastName("User")
                .email("create@user.com")
                .functions(Set.of(UserFunction.COMPANY_MANAGER))
                .build();
        User mappedUser = new User();
        mappedUser.setFirstName("Create");
        mappedUser.setLastName("User");
        mappedUser.setEmail("create@user.com");

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
        UserDto createDto = UserDto.builder()
            .firstName("Create")
            .lastName("User")
            .email("create@user.com")
            .functions(Set.of(UserFunction.COMPANY_MANAGER))
            .build();
        User mappedUser = new User();
        mappedUser.setFirstName("Create");
        mappedUser.setLastName("User");
        mappedUser.setEmail("create@user.com");

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
        org.mockito.Mockito.verify(userRepository, org.mockito.Mockito.never()).findByIdOrThrow(any(UUID.class));
        }

        @Test
        @DisplayName("Should throw exception when getUsersDetails is called with non-existent user")
        void shouldThrowExceptionWhenGetUsersDetailsWithNonExistentUser() {
        // Given
        UUID notFoundId = UUID.randomUUID();
        when(userRepository.findByIdOrThrow(notFoundId)).thenThrow(
            new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "User not found"));

        // When / Then
        assertThrows(ResponseStatusException.class, () -> userManagementService.getUsersDetails(notFoundId));
        verify(userRepository).findByIdOrThrow(notFoundId);
        }

        @Test
        @DisplayName("Should update user functions correctly")
        void shouldUpdateUserFunctions() {
        // Given
        User existingUser = new User();
        existingUser.setId(userId);
        // Simulate existing function assignments
        Set<com.coherentsolutions.pot.insurance_service.model.UserFunctionAssignment> assignments = new java.util.HashSet<>();
        com.coherentsolutions.pot.insurance_service.model.UserFunctionAssignment assignment = new com.coherentsolutions.pot.insurance_service.model.UserFunctionAssignment();
        assignment.setFunction(UserFunction.COMPANY_MANAGER);
        assignment.setUser(existingUser);
        assignments.add(assignment);
        existingUser.setFunctions(assignments);

        UserDto requestDto = UserDto.builder()
            .functions(Set.of(UserFunction.COMPANY_MANAGER, UserFunction.CONSUMER_CLAIM_MANAGER))
            .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(existingUser);
        when(userMapper.toDto(any(User.class))).thenReturn(requestDto);

        // When
        UserDto result = userManagementService.updateUser(userId, requestDto);

        // Then
        assertNotNull(result);
        assertEquals(2, result.getFunctions().size());
        verify(userRepository).save(existingUser);
        verify(userRepository).findById(userId);
        verify(userMapper).toDto(existingUser);
        }
    }
