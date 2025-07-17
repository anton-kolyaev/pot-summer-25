package com.coherentsolutions.pot.insurance_service.unit.controller;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.coherentsolutions.pot.insurance_service.controller.AdminUserManagementController;
import com.coherentsolutions.pot.insurance_service.dto.user.UserDto;
import com.coherentsolutions.pot.insurance_service.enums.UserStatus;
import com.coherentsolutions.pot.insurance_service.service.UserManagementService;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
@DisplayName("Admin User Management Controller Tests")
class AdminUserManagementControllerTest {

    @Mock
    private UserManagementService userManagementService;

    @InjectMocks
    private AdminUserManagementController controller;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private UUID testUserId;
    private UserDto updatedUserDto;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        objectMapper = new ObjectMapper();

        testUserId = UUID.randomUUID();
        updatedUserDto = UserDto.builder()
                .id(testUserId)
                .firstName("Updated")
                .lastName("User")
                .email("updated@example.com")
                .username("updated_user")
                .status(UserStatus.ACTIVE)
                .build();
    }

    @Test
    @DisplayName("Should update user by ID")
    void shouldUpdateUserById() throws Exception {
        UserDto updateRequest = UserDto.builder()
                .firstName("Updated")
                .lastName("User")
                .email("updated@example.com")
                .username("updated_user")
                .build();

        when(userManagementService.updateUser(eq(testUserId), any(UserDto.class)))
                .thenReturn(updatedUserDto);

        mockMvc.perform(put("/v1/users/{id}", testUserId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(testUserId.toString()))
                .andExpect(jsonPath("$.firstName").value("Updated"))
                .andExpect(jsonPath("$.lastName").value("User"))
                .andExpect(jsonPath("$.email").value("updated@example.com"))
                .andExpect(jsonPath("$.username").value("updated_user"));

        verify(userManagementService).updateUser(eq(testUserId), any(UserDto.class));
    }
}
