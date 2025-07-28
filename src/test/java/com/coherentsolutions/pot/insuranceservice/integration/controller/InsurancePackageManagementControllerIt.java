package com.coherentsolutions.pot.insuranceservice.integration.controller;


import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.coherentsolutions.pot.insuranceservice.enums.PackageStatus;
import com.coherentsolutions.pot.insuranceservice.enums.PayrollFrequency;
import com.coherentsolutions.pot.insuranceservice.integration.IntegrationTestConfiguration;
import com.coherentsolutions.pot.insuranceservice.integration.containers.PostgresTestContainer;
import com.coherentsolutions.pot.insuranceservice.model.Company;
import com.coherentsolutions.pot.insuranceservice.model.InsurancePackage;
import com.coherentsolutions.pot.insuranceservice.repository.CompanyRepository;
import com.coherentsolutions.pot.insuranceservice.repository.InsurancePackageRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@ActiveProfiles("test")
@SpringBootTest
@Import(IntegrationTestConfiguration.class)
@AutoConfigureMockMvc
@DisplayName("Integration test for AdminInsurancePackageManagementController")
public class InsurancePackageManagementControllerIt extends PostgresTestContainer {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private InsurancePackageRepository insurancePackageRepository;

  @Autowired
  private CompanyRepository companyRepository;

  @Test
  @DisplayName("Should retrieve Insurance Package by its ID")
  void shouldRetrieveInsurancePackageById() throws Exception {
    Company company = new Company();
    company.setName("Retrieve Company");
    company.setEmail("retrieve@company.com");
    company.setCountryCode("USA");
    company.setWebsite("https://retrieve.com");
    company = companyRepository.save(company);

    InsurancePackage insurancePackage = new InsurancePackage();
    insurancePackage.setName("Gold Health Plan");
    insurancePackage.setStartDate(LocalDate.of(2025, 9, 1));
    insurancePackage.setEndDate(LocalDate.of(2025, 12, 31));
    insurancePackage.setPayrollFrequency(PayrollFrequency.MONTHLY);
    insurancePackage.setStatus(PackageStatus.INITIALIZED);
    insurancePackage.setCompany(company);
    insurancePackage = insurancePackageRepository.save(insurancePackage);

    UUID companyId = company.getId();
    UUID packageId = insurancePackage.getId();

    try {
      mockMvc.perform(get("/v1/company/{companyId}/plan-package/{id}", companyId, packageId)
              .contentType(APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.id").value(packageId.toString()))
          .andExpect(jsonPath("$.name").value("Gold Health Plan"))
          .andExpect(jsonPath("$.startDate").value("2025-09-01"))
          .andExpect(jsonPath("$.endDate").value("2025-12-31"))
          .andExpect(jsonPath("$.payrollFrequency").value("MONTHLY"));
    } finally {
      List<InsurancePackage> packages = insurancePackageRepository.findAllByCompanyId(companyId);
      insurancePackageRepository.deleteAll(packages);
      companyRepository.deleteById(companyId);
    }
  }

  @Test
  @DisplayName("Should return 404 when insurance package is not found by ID")
  void shouldReturnNotFoundWhenInsurancePackageDoesNotExist() throws Exception {
    UUID companyId = UUID.randomUUID();
    UUID nonExistentPackageId = UUID.randomUUID();

    try {
      mockMvc.perform(
              get("/v1/company/{companyId}/plan-package/{id}", companyId, nonExistentPackageId)
                  .contentType(APPLICATION_JSON))
          .andExpect(status().isNotFound());
    } finally {
      companyRepository.deleteById(companyId);
    }
  }

}
