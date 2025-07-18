package com.coherentsolutions.pot.insurance_service.unit.service;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.coherentsolutions.pot.insurance_service.dto.user.UserDto;
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
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> userManagementService.deactivateUser(userId));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        assertEquals("User is already inactive", ex.getReason());
    }
}
