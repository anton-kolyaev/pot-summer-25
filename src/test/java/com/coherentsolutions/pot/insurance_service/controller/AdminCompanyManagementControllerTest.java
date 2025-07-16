package com.coherentsolutions.pot.insurance_service.controller;

import com.coherentsolutions.pot.insurance_service.dto.user.UserDto;
import com.coherentsolutions.pot.insurance_service.dto.user.UserFilter;
import com.coherentsolutions.pot.insurance_service.enums.UserStatus;
import com.coherentsolutions.pot.insurance_service.service.UserManagementService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ExtendWith(MockitoExtension.class)
@DisplayName("User Company Management Service Tests")
class AdminCompanyManagementControllerTest {

    @Mock
    private UserManagementService userManagementService;

    @InjectMocks
    private AdminCompanyManagementController adminCompanyManagementController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(adminCompanyManagementController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
    }

    @Test
    @DisplayName("Should get users of a company by companyId")
    void shouldGetUsersOfCompanyById() throws Exception {
        UUID companyId = UUID.randomUUID();

        UserDto user1 = UserDto.builder()
                .id(UUID.randomUUID())
                .firstName("Alice")
                .lastName("Johnson")
                .username("alice.johnson")
                .email("alice@example.com")
                .companyId(companyId)
                .status(UserStatus.ACTIVE)
                .build();

        UserDto user2 = UserDto.builder()
                .id(UUID.randomUUID())
                .firstName("Bob")
                .lastName("Smith")
                .username("bob.smith")
                .email("bob.smith@example.com")
                .companyId(companyId)
                .status(UserStatus.INACTIVE)
                .build();

        Page<UserDto> userPage = new PageImpl<>(List.of(user1, user2), PageRequest.of(0, 10), 2);

        when(userManagementService.getUsersWithFilters(any(UserFilter.class), any(Pageable.class)))
                .thenReturn(userPage);

        mockMvc.perform(get("/v1/companies/{id}/users", companyId)
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].username").value("alice.johnson"))
                .andExpect(jsonPath("$.content[1].username").value("bob.smith"))
                .andExpect(jsonPath("$.content[0].companyId").value(companyId.toString()));

        verify(userManagementService).getUsersWithFilters(any(UserFilter.class), any(Pageable.class));
    }

    @Test
    @DisplayName("Should return empty page if no users found for company")
    void shouldReturnEmptyPageIfNoUsersFoundForCompany() throws Exception {
        UUID companyId = UUID.randomUUID();

        Page<UserDto> emptyUserPage = new PageImpl<>(List.of(), PageRequest.of(0, 10), 0);

        when(userManagementService.getUsersWithFilters(any(UserFilter.class), any(Pageable.class)))
                .thenReturn(emptyUserPage);

        mockMvc.perform(get("/v1/companies/{id}/users", companyId)
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(0))
                .andExpect(jsonPath("$.totalElements").value(0));

        verify(userManagementService).getUsersWithFilters(any(UserFilter.class), any(Pageable.class));
    }

}