package com.coherentsolutions.pot.insurance_service.integration.controller;

import com.coherentsolutions.pot.insurance_service.containers.PostgresTestContainer;
import com.coherentsolutions.pot.insurance_service.enums.CompanyStatus;
import com.coherentsolutions.pot.insurance_service.enums.UserStatus;
import com.coherentsolutions.pot.insurance_service.model.Company;
import com.coherentsolutions.pot.insurance_service.model.User;
import com.coherentsolutions.pot.insurance_service.repository.CompanyRepository;
import com.coherentsolutions.pot.insurance_service.repository.UserRepository;
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

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Integration test for AdminCompanyManagementController")
public class AdminCompanyManagementControllerIT extends PostgresTestContainer {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private UserRepository userRepository;


    @Test
    @DisplayName("Should return all users of a company by companyId")
    void shouldReturnAllUsersOfExistingCompany() throws Exception {
        Company company = new Company();
        company.setName("Test Company");
        company.setCountryCode("USA");
        company.setEmail("test@example.com");
        company.setWebsite("https://example.com");
        company.setStatus(CompanyStatus.ACTIVE);
        company = companyRepository.save(company);
        UUID companyId = company.getId();

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

        try{
            mockMvc.perform(get("/v1/companies/{id}/users", companyId)
                            .param("page", "0")
                            .param("size", "10")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content.length()").value(2))

                    .andExpect(jsonPath("$.content[*].username", containsInAnyOrder("alice.johnson", "bob.smith")))
                    .andExpect(jsonPath("$.content[*].companyId", containsInAnyOrder(companyId.toString(), companyId.toString())));
        } finally {
            userRepository.deleteById(user1.getId());
            userRepository.deleteById(user2.getId());
            companyRepository.deleteById(companyId);
        }

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

        try{
            mockMvc.perform(get("/v1/companies/{id}/users", emptyCompany.getId())
                            .param("page", "0")
                            .param("size", "10")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content.length()").value(0))
                    .andExpect(jsonPath("$.totalElements").value(0));
        }   finally {
            companyRepository.deleteById(emptyCompany.getId());
        }

    }

    @Test
    @DisplayName("Should return Bad Request when companyId has invalid format")
    void shouldReturnBadRequestForInvalidCompanyId() throws Exception {
        String invalidCompanyId = "invalid-company-id";
        mockMvc.perform(get("/v1/companies/{id}/users", invalidCompanyId)
                .param("page", "0")
                .param("size", "10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
