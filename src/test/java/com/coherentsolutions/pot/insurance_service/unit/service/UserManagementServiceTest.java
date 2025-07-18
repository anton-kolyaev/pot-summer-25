package com.coherentsolutions.pot.insurance_service.unit.service;

import com.coherentsolutions.pot.insurance_service.dto.user.UserDto;
import com.coherentsolutions.pot.insurance_service.enums.UserFunction;
import com.coherentsolutions.pot.insurance_service.enums.UserStatus;
import com.coherentsolutions.pot.insurance_service.mapper.UserMapper;
import com.coherentsolutions.pot.insurance_service.model.User;
import com.coherentsolutions.pot.insurance_service.model.UserFunctionAssignment;
import com.coherentsolutions.pot.insurance_service.repository.UserRepository;
import com.coherentsolutions.pot.insurance_service.service.UserManagementService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("User Management Service Tests")
class UserManagementServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserManagementService userManagementService;

    private UUID testUserId;
    private User testUser;
    private UserDto testUserDto;
    private UserFunctionAssignment testAssignment;

    @BeforeEach
    void setUp() {
        testUserId = UUID.randomUUID();

        testAssignment = new UserFunctionAssignment();
        testAssignment.setFunction(UserFunction.COMPANY_MANAGER);

        testUser = new User();
        testUser.setId(testUserId);
        testUser.setFirstName("Test User");
        testUser.setEmail("test@user.com");
        testUser.setDateOfBirth(LocalDate.of(1990, 1, 1));
        testUser.setStatus(UserStatus.ACTIVE);
        testUser.setSsn("123-45-6789");
        testUser.setFunctions(Set.of(testAssignment));

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
    @DisplayName("Should get user details by ID")
    void shouldGetUserDetailsById() {
        // Given
        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
        when(userMapper.toDto(testUser)).thenReturn(testUserDto);

        // When
        UserDto result = userManagementService.getUsersDetails(testUserId);

        // Then
        assertNotNull(result);
        assertEquals(testUserDto, result);
        verify(userRepository).findById(testUserId);
        verify(userMapper).toDto(testUser);
    }
}
