package com.coherentsolutions.pot.insuranceservice.integration.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
}
