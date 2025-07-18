package com.coherentsolutions.pot.insurance_service.unit.service;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.coherentsolutions.pot.insurance_service.dto.user.UserDto;
import com.coherentsolutions.pot.insurance_service.enums.UserFunction;
import com.coherentsolutions.pot.insurance_service.mapper.UserMapper;
import com.coherentsolutions.pot.insurance_service.model.User;
import com.coherentsolutions.pot.insurance_service.model.UserFunctionAssignment;
import com.coherentsolutions.pot.insurance_service.repository.UserRepository;
import com.coherentsolutions.pot.insurance_service.service.UserManagementService;

@RunWith(MockitoJUnitRunner.class)
public class UserManagementServiceTest {
    private static final UUID COMPANY_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();

    private static final String FIRST_NAME = "John";
    private static final String LAST_NAME = "Doe";
    private static final String EMAIL = "john.doe@example.com";
    private static final String USERNAME = "john.doe";

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserManagementService userManagementService;


    @Test
    @DisplayName("Should create user and assign functions correctly")
    public void shouldCreateUserSuccessfully() {
        // Arrange
        UserDto inputDto = UserDto.builder()
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .email(EMAIL)
                .username(USERNAME)
                .companyId(COMPANY_ID)
                .build();

        User userEntity = new User();
        userEntity.setFirstName(FIRST_NAME);
        userEntity.setLastName(LAST_NAME);
        userEntity.setEmail(EMAIL);
        userEntity.setUsername(USERNAME);

        UserFunctionAssignment ufa = new UserFunctionAssignment();
        ufa.setFunction(UserFunction.COMPANY_REPORT_MANAGER);
        ufa.setUser(userEntity);

        User savedUser = new User();
        savedUser.setId(USER_ID);
        savedUser.setFirstName(FIRST_NAME);
        savedUser.setLastName(LAST_NAME);
        savedUser.setEmail(EMAIL);
        savedUser.setUsername(USERNAME);
        ufa.setFunction(UserFunction.COMPANY_REPORT_MANAGER);

        UserDto outputDto = UserDto.builder()
                .id(USER_ID)
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .email(EMAIL)
                .username(USERNAME)
                .companyId(COMPANY_ID)
                .build();

        when(userMapper.toEntity(inputDto)).thenReturn(userEntity);
        when(userRepository.save(userEntity)).thenReturn(savedUser);
        when(userMapper.toDto(savedUser)).thenReturn(outputDto);

        UserDto result = userManagementService.createUser(inputDto);

        // Assert
        Assertions.assertNotNull(result);
        Assertions.assertEquals(USER_ID, result.getId());
        Assertions.assertEquals(USERNAME, result.getUsername());
        Assertions.assertEquals(FIRST_NAME, result.getFirstName());

        verify(userMapper).toEntity(inputDto);
        verify(userRepository).save(userEntity);
        verify(userMapper).toDto(savedUser);
    }

}
