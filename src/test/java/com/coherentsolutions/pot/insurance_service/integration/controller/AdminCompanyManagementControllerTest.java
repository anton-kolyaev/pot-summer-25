package com.coherentsolutions.pot.insurance_service.integration.controller;

import com.coherentsolutions.pot.insurance_service.containers.PostgresTestContainer;
import com.coherentsolutions.pot.insurance_service.enums.CompanyStatus;
import com.coherentsolutions.pot.insurance_service.enums.UserStatus;
import com.coherentsolutions.pot.insurance_service.model.Company;
import com.coherentsolutions.pot.insurance_service.model.User;
import com.coherentsolutions.pot.insurance_service.repository.CompanyRepository;
import com.coherentsolutions.pot.insurance_service.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Integration test for AdminCompanyManagementController")
public class AdminCompanyManagementControllerTest extends PostgresTestContainer {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private UserRepository userRepository;

    private UUID companyId;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        companyRepository.deleteAll();

        Company company = new Company();
        company.setName("Test Company");
        company.setCountryCode("USA");
        company.setEmail("test@example.com");
        company.setWebsite("https://example.com");
        company.setStatus(CompanyStatus.ACTIVE);
        company = companyRepository.save(company);
        companyId = company.getId();

        User user1 = new User();
        user1.setFirstName("Alice");
        user1.setLastName("Johnson");
        user1.setUsername("alice.johnson");
        user1.setEmail("alice@example.com");
        user1.setCompany(company);
        user1.setStatus(UserStatus.ACTIVE);
        user1.setDateOfBirth(LocalDate.of(1990, 1, 1));
        user1.setSsn("111-22-3333");

        User user2 = new User();
        user2.setFirstName("Bob");
        user2.setLastName("Smith");
        user2.setUsername("bob.smith");
        user2.setEmail("bob@example.com");
        user2.setCompany(company);
        user2.setStatus(UserStatus.INACTIVE);
        user2.setDateOfBirth(LocalDate.of(1985, 5, 15));
        user2.setSsn("444-55-6666");

        userRepository.save(user1);
        userRepository.save(user2);
    }

    @Test
    @DisplayName("Should return all users of a company by companyId")
    void shouldReturnAllUsersOfExistingCompany() throws Exception {
        mockMvc.perform(get("/v1/companies/{id}/users", companyId)
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].username").value("alice.johnson"))
                .andExpect(jsonPath("$.content[1].username").value("bob.smith"))
                .andExpect(jsonPath("$.content[0].companyId").value(companyId.toString()))
                .andExpect(jsonPath("$.content[1].companyId").value(companyId.toString()));
    }

    @Test
    @DisplayName("Should return empty page if no users found for company")
    void shouldReturnEmptyPageIfNoUsersFoundForCompany() throws Exception {
        Company emptyCompany = new Company();
        emptyCompany.setName("Empty Company");
        emptyCompany.setCountryCode("USA");
        emptyCompany.setEmail("empty@example.com");
        emptyCompany.setStatus(CompanyStatus.ACTIVE);
        emptyCompany = companyRepository.save(emptyCompany);

        mockMvc.perform(get("/v1/companies/{id}/users", emptyCompany.getId())
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(0))
                .andExpect(jsonPath("$.totalElements").value(0));
    }
}
