package com.coherentsolutions.pot.insurance_service.unit.service;

import java.util.List;
import java.util.Optional;
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
}
