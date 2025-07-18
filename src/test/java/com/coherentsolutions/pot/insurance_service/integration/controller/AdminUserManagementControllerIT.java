package com.coherentsolutions.pot.insurance_service.integration.controller;

import com.coherentsolutions.pot.insurance_service.dto.user.UserDto;
import com.coherentsolutions.pot.insurance_service.enums.UserStatus;
import com.coherentsolutions.pot.insurance_service.integration.containers.PostgresTestContainer;
import com.coherentsolutions.pot.insurance_service.model.Company;
import com.coherentsolutions.pot.insurance_service.model.User;
import com.coherentsolutions.pot.insurance_service.repository.CompanyRepository;
import com.coherentsolutions.pot.insurance_service.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Integration test for AdminUserManagementController")
public class AdminUserManagementControllerIT extends PostgresTestContainer {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    // Constants
    private static final String BASE_URL = "/v1/users";
    private static final String APPLICATION_JSON = MediaType.APPLICATION_JSON_VALUE;
    private static final String TEST_EMAIL = "jane.doe@example.com";
    private static final String TEST_USERNAME = "jane.doe";
    private static final String TEST_FIRST_NAME = "Jane";
    private static final String TEST_LAST_NAME = "Doe";
    private static final String TEST_SSN = "999-88-7777";

    @Test
    @DisplayName("Should create a user successfully")
    void shouldCreateUser() throws Exception {
        Company company = new Company();
        company.setName("Test Company");
        company.setEmail("company@example.com");
        company.setCountryCode("USA");
        company.setWebsite("https://example.com");
        company = companyRepository.save(company);

        UserDto userDto = UserDto.builder()
                .firstName(TEST_FIRST_NAME)
                .lastName(TEST_LAST_NAME)
                .username(TEST_USERNAME)
                .email(TEST_EMAIL)
                .companyId(company.getId())
                .status(UserStatus.ACTIVE)
                .dateOfBirth(LocalDate.of(1992, 3, 14))
                .ssn(TEST_SSN)
                .build();

        try {
            mockMvc.perform(post(BASE_URL)
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(userDto)))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                    .andExpect(jsonPath("$.username").value(TEST_USERNAME))
                    .andExpect(jsonPath("$.companyId").value(company.getId().toString()));
        } finally {
            userRepository.deleteAll();
            companyRepository.deleteById(company.getId());
        }
    }

    @Test
    @DisplayName("Should retrieve users using filter and pagination")
    void shouldGetUsersWithFilters() throws Exception {
        Company company = new Company();
        company.setName("Filter Company");
        company.setEmail("filter@example.com");
        company.setCountryCode("USA");
        company.setWebsite("https://filter.com");
        company = companyRepository.save(company);

        User user = new User();
        user.setFirstName("Filter");
        user.setLastName("Test");
        user.setUsername("filter.user");
        user.setEmail("filter.user@example.com");
        user.setCompany(company);
        user.setStatus(UserStatus.ACTIVE);
        user.setDateOfBirth(LocalDate.of(1995, 6, 10));
        user.setSsn("123-45-6789");
        user = userRepository.save(user);

        try {
            mockMvc.perform(get(BASE_URL)
                    .param("page", "0")
                    .param("size", "10")
                    .param("status", "ACTIVE")
                    .param("companyId", company.getId().toString())
                    .contentType(APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content.length()").value(1))
                    .andExpect(jsonPath("$.content[0].username").value("filter.user"));
        } finally {
            userRepository.deleteById(user.getId());
            companyRepository.deleteById(company.getId());
        }
    }

    @Test
    @DisplayName("Should return internal server error for missing all domain fields")
    void shouldReturnInternalServerErrorForInvalidUserDto() throws Exception {
        UserDto invalidUserDto = new UserDto(); // all fields null

        mockMvc.perform(post(BASE_URL)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidUserDto)))
                .andExpect(status().isInternalServerError());
    }
}
