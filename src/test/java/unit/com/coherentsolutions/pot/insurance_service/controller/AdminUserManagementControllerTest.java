package unit.com.coherentsolutions.pot.insurance_service.controller;

import com.coherentsolutions.pot.insurance_service.controller.AdminUserManagementController;
import com.coherentsolutions.pot.insurance_service.dto.user.UserDto;
import com.coherentsolutions.pot.insurance_service.enums.UserFunction;
import com.coherentsolutions.pot.insurance_service.enums.UserStatus;
import com.coherentsolutions.pot.insurance_service.service.UserManagementService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Admin User Management Controller Tests")
class AdminUserManagementControllerTest {

    @Mock
    private UserManagementService userManagementService;

    @InjectMocks
    private AdminUserManagementController controller;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private UserDto testUserDto;
    private UUID testUserId;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
        objectMapper = new ObjectMapper();
        
        testUserId = UUID.randomUUID();
        testUserDto = UserDto.builder()
                .id(testUserId)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .status(UserStatus.ACTIVE)
                .functions(Set.of(UserFunction.COMPANY_CLAIM_MANAGER))
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .build();
    }

    @Test
    @DisplayName("Should get user details by ID")
    void shouldGetUserDetailsById() throws Exception {
        // Given
        when(userManagementService.getUsersDetails(testUserId))
                .thenReturn(testUserDto);

        // When & Then
        mockMvc.perform(get("/v1/users/{id}", testUserId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(testUserId.toString()))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.functions[0]").value("COMPANY_CLAIM_MANAGER"));

        verify(userManagementService).getUsersDetails(testUserId);
    }
}