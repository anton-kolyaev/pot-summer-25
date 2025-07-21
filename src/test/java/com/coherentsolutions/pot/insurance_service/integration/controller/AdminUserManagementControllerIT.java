package com.coherentsolutions.pot.insurance_service.integration.controller;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.coherentsolutions.pot.insurance_service.enums.UserStatus;
import com.coherentsolutions.pot.insurance_service.integration.IntegrationTestConfiguration;
import com.coherentsolutions.pot.insurance_service.integration.containers.PostgresTestContainer;
import com.coherentsolutions.pot.insurance_service.model.Company;
import com.coherentsolutions.pot.insurance_service.model.User;
import com.coherentsolutions.pot.insurance_service.repository.CompanyRepository;
import com.coherentsolutions.pot.insurance_service.repository.UserRepository;

@ActiveProfiles("test")
@SpringBootTest
@Import(IntegrationTestConfiguration.class)
@AutoConfigureMockMvc
@DisplayName("Integration test for AdminUserManagementController")
public class AdminUserManagementControllerIT extends PostgresTestContainer {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Test
    @DisplayName("Should deactivate active user")
    void shouldDeactivateUser() throws Exception {
        Company company = new Company();
        company.setName("Example Inc.");
        company.setCountryCode("US");
        company.setEmail("info@example.com");
        company.setStatus(com.coherentsolutions.pot.insurance_service.enums.CompanyStatus.ACTIVE);
        company = companyRepository.save(company);

        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setUsername("john.doe");
        user.setEmail("john.doe@example.com");
        user.setCompany(company);
        user.setDateOfBirth(LocalDate.of(1992, 4, 23));
        user.setSsn("111-22-3333");
        user.setStatus(UserStatus.ACTIVE);
        user = userRepository.save(user);

        try {
            mockMvc.perform(delete("/v1/users/{id}", user.getId()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(UserStatus.INACTIVE.name()));
        } finally {
            userRepository.deleteById(user.getId());
            companyRepository.deleteById(company.getId());
        }
    }

    @Test
    @DisplayName("Should reactivate inactive user")
    void shouldReactivateUser() throws Exception {
        Company company = new Company();
        company.setName("Example Inc.");
        company.setCountryCode("US");
        company.setEmail("info@example.com");
        company.setStatus(com.coherentsolutions.pot.insurance_service.enums.CompanyStatus.ACTIVE);
        company = companyRepository.save(company);

        User user = new User();
        user.setFirstName("Jane");
        user.setLastName("Smith");
        user.setUsername("jane.smith");
        user.setEmail("jane.smith@example.com");
        user.setCompany(company);
        user.setDateOfBirth(LocalDate.of(1988, 12, 1));
        user.setSsn("999-88-7777");
        user.setStatus(UserStatus.INACTIVE);
        user = userRepository.save(user);

        try {
            mockMvc.perform(put("/v1/users/{id}/reactivation", user.getId()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(UserStatus.ACTIVE.name()));
        } finally {
            userRepository.deleteById(user.getId());
            companyRepository.deleteById(company.getId());
        }
    }
}
