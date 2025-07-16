package com.coherentsolutions.pot.insurance_service.controller;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.coherentsolutions.pot.insurance_service.dto.user.UserDto;
import com.coherentsolutions.pot.insurance_service.service.UserManagementService;

@ExtendWith(MockitoExtension.class)
public class AdminUserManagementControllerTest {
    
    @InjectMocks
    AdminUserManagementController controller;

    @Mock
    private UserManagementService service;

    @Test
    void testCreateUser() {
        // Implement test logic for createUser method
    }

    @Test
    void testGetUsersWithFilters() {
        // Implement test logic for getUsersWithFilters method
    }

    @Test
    void testViewUsersDetails() {
        UUID userId = UUID.randomUUID();
        UserDto userDto = new UserDto();
        userDto.setId(userId);
        userDto.setFirstName("TestUser");

        when(service.getUsersDetails(userId)).thenReturn(userDto);

        UserDto result = controller.viewUsersDetails(userId);
        assertThat(result.getId()).isEqualTo(userId);
        assertThat(result.getFirstName()).isEqualTo("TestUser");
    }



}
