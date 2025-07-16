package com.coherentsolutions.pot.insurance_service.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.web.server.ResponseStatusException;

import com.coherentsolutions.pot.insurance_service.dto.AddressDto;
import com.coherentsolutions.pot.insurance_service.dto.PhoneDto;
import com.coherentsolutions.pot.insurance_service.dto.user.UserDto;
import com.coherentsolutions.pot.insurance_service.enums.UserStatus;
import com.coherentsolutions.pot.insurance_service.mapper.UserMapper;
import com.coherentsolutions.pot.insurance_service.model.User;
import com.coherentsolutions.pot.insurance_service.repository.UserRepository;

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

    //UpdateUser (PUT /users/{id})
    @Test
    void shouldUpdateUserFieldsSuccessfully() {

        UserDto requestDto = new UserDto();
        requestDto.setFirstName("New");
        requestDto.setLastName("User");
        requestDto.setUsername("new_username");
        requestDto.setEmail("new@email.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toDto(any(User.class))).thenReturn(requestDto);

        UserDto result = userManagementService.updateUser(userId, requestDto);

        assertEquals("new@email.com", result.getEmail());
        assertEquals("New", result.getFirstName());
        assertEquals("User", result.getLastName());
        assertEquals("new_username", result.getUsername());
        verify(userRepository).save(user);
    }

    //UpdateUser (PUT /users/{id})
    @Test
    void shouldUpdatePhoneAndAddressData() {

        List<PhoneDto> phoneDtos = List.of(new PhoneDto());
        List<AddressDto> addressDtos = List.of(new AddressDto());

        UserDto requestDto = new UserDto();
        requestDto.setPhoneData(phoneDtos);
        requestDto.setAddressData(addressDtos);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toPhoneList(phoneDtos)).thenReturn(List.of());
        when(userMapper.toAddressList(addressDtos)).thenReturn(List.of());
        when(userMapper.toDto(any(User.class))).thenReturn(requestDto);

        UserDto result = userManagementService.updateUser(userId, requestDto);

        assertEquals(phoneDtos, result.getPhoneData());
        assertEquals(addressDtos, result.getAddressData());

        verify(userMapper).toPhoneList(phoneDtos);
        verify(userMapper).toAddressList(addressDtos);
        verify(userRepository).save(user);
    }

    //UpdateUser (PUT /users/{id})
    @Test
    void shouldThrowExceptionWhenUserNotFound() {

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(
                ResponseStatusException.class,
                () -> userManagementService.updateUser(userId, new UserDto()));

    }
}
