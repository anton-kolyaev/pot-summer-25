package com.coherentsolutions.pot.insuranceservice.integration.controller;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.coherentsolutions.pot.insuranceservice.enums.CompanyStatus;
import com.coherentsolutions.pot.insuranceservice.enums.UserStatus;
import com.coherentsolutions.pot.insuranceservice.integration.IntegrationTestConfiguration;
import com.coherentsolutions.pot.insuranceservice.integration.containers.PostgresTestContainer;
import com.coherentsolutions.pot.insuranceservice.model.Company;
import com.coherentsolutions.pot.insuranceservice.model.User;
import com.coherentsolutions.pot.insuranceservice.repository.CompanyRepository;
import com.coherentsolutions.pot.insuranceservice.repository.UserRepository;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Integration tests for AdminUserManagementController. This test class verifies user management
 * functionality such as deactivation and reactivation of users, including edge cases like already
 * inactive or non-existent users.
 */
@ActiveProfiles("test")
@SpringBootTest
@Import(IntegrationTestConfiguration.class)
@AutoConfigureMockMvc
@DisplayName("Integration test for AdminUserManagementController")
public class AdminUserManagementControllerIt extends PostgresTestContainer {

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
    company.setStatus(com.coherentsolutions.pot.insuranceservice.enums.CompanyStatus.ACTIVE);
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
      mockMvc.perform(delete("/v1/users/{id}", user.getId())).andExpect(status().isOk())
          .andExpect(jsonPath("$.status").value(UserStatus.INACTIVE.name()));
    } finally {
      userRepository.deleteById(user.getId());
      companyRepository.deleteById(company.getId());
    }
  }

  @Test
  @DisplayName("Should return 400 when deactivating an already inactive user")
  void shouldReturn400WhenDeactivatingAlreadyInactiveUser() throws Exception {
    Company company = new Company();
    company.setName("Inactive Co.");
    company.setCountryCode("US");
    company.setEmail("inactive@example.com");
    company.setStatus(com.coherentsolutions.pot.insuranceservice.enums.CompanyStatus.ACTIVE);
    company = companyRepository.save(company);

    User user = new User();
    user.setFirstName("Mark");
    user.setLastName("Twain");
    user.setUsername("mark.twain");
    user.setEmail("mark@example.com");
    user.setCompany(company);
    user.setDateOfBirth(LocalDate.of(1980, 6, 15));
    user.setSsn("123-45-6789");
    user.setStatus(UserStatus.INACTIVE);
    user = userRepository.save(user);

    try {
      mockMvc.perform(delete("/v1/users/{id}", user.getId())).andExpect(status().isBadRequest());
    } finally {
      userRepository.deleteById(user.getId());
      companyRepository.deleteById(company.getId());
    }
  }

  @Test
  @DisplayName("Should return 404 when deactivating a non-existent user")
  void shouldReturn404WhenDeactivatingNonExistentUser() throws Exception {
    UUID fakeId = UUID.randomUUID();
    mockMvc.perform(delete("/v1/users/{id}", fakeId)).andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("Should reactivate inactive user")
  void shouldReactivateUser() throws Exception {
    Company company = new Company();
    company.setName("Example Inc.");
    company.setCountryCode("US");
    company.setEmail("info@example.com");
    company.setStatus(com.coherentsolutions.pot.insuranceservice.enums.CompanyStatus.ACTIVE);
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
      mockMvc.perform(put("/v1/users/{id}/reactivation", user.getId())).andExpect(status().isOk())
          .andExpect(jsonPath("$.status").value(UserStatus.ACTIVE.name()));
    } finally {
      userRepository.deleteById(user.getId());
      companyRepository.deleteById(company.getId());
    }
  }

  @Test
  @DisplayName("Should return 400 when reactivating an already active user")
  void shouldReturn400WhenReactivatingAlreadyActiveUser() throws Exception {
    Company company = new Company();
    company.setName("Active Co.");
    company.setCountryCode("US");
    company.setEmail("active@example.com");
    company.setStatus(com.coherentsolutions.pot.insuranceservice.enums.CompanyStatus.ACTIVE);
    company = companyRepository.save(company);

    User user = new User();
    user.setFirstName("Lisa");
    user.setLastName("Simpson");
    user.setUsername("lisa.simpson");
    user.setEmail("lisa@example.com");
    user.setCompany(company);
    user.setDateOfBirth(LocalDate.of(2000, 1, 1));
    user.setSsn("555-66-7777");
    user.setStatus(UserStatus.ACTIVE);
    user = userRepository.save(user);

    try {
      mockMvc.perform(put("/v1/users/{id}/reactivation", user.getId()))
          .andExpect(status().isBadRequest());
    } finally {
      userRepository.deleteById(user.getId());
      companyRepository.deleteById(company.getId());
    }
  }

  @Test
  @DisplayName("Should return 404 when reactivating a non-existent user")
  void shouldReturn404WhenReactivatingNonExistentUser() throws Exception {
    UUID fakeId = UUID.randomUUID();
    mockMvc.perform(put("/v1/users/{id}/reactivation", fakeId)).andExpect(status().isNotFound());
  }


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

    try {
      mockMvc.perform(
              get("/v1/companies/{id}/users", companyId).param("page", "0").param("size", "10")
                  .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
          .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$.content").isArray())
          .andExpect(jsonPath("$.content.length()").value(2))

          .andExpect(
              jsonPath("$.content[*].username", containsInAnyOrder("alice.johnson", "bob.smith")))
          .andExpect(jsonPath("$.content[*].companyId",
              containsInAnyOrder(companyId.toString(), companyId.toString())));
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

    try {
      mockMvc.perform(get("/v1/companies/{id}/users", emptyCompany.getId())
              .param("page", "0")
              .param("size", "10").contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$.content").isArray())
          .andExpect(jsonPath("$.content.length()").value(0))
          .andExpect(jsonPath("$.totalElements").value(0));
    } finally {
      companyRepository.deleteById(emptyCompany.getId());
    }

  }

  @Test
  @DisplayName("Should return Bad Request when companyId has invalid format")
  void shouldReturnBadRequestForInvalidCompanyId() throws Exception {
    String invalidCompanyId = "invalid-company-id";
    mockMvc.perform(
        get("/v1/companies/{id}/users", invalidCompanyId).param("page", "0").param("size", "10")
            .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
  }
}
