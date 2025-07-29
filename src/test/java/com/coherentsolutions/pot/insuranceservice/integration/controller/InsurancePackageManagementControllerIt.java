package com.coherentsolutions.pot.insuranceservice.integration.controller;


import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.coherentsolutions.pot.insuranceservice.enums.PackageStatus;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.coherentsolutions.pot.insuranceservice.dto.insurancepackage.InsurancePackageDto;
import com.coherentsolutions.pot.insuranceservice.enums.PayrollFrequency;
import com.coherentsolutions.pot.insuranceservice.integration.IntegrationTestConfiguration;
import com.coherentsolutions.pot.insuranceservice.integration.containers.PostgresTestContainer;
import com.coherentsolutions.pot.insuranceservice.model.Company;
import com.coherentsolutions.pot.insuranceservice.model.InsurancePackage;
import com.coherentsolutions.pot.insuranceservice.repository.CompanyRepository;
import com.coherentsolutions.pot.insuranceservice.repository.InsurancePackageRepository;
import java.time.LocalDate;
import java.util.List;
import com.coherentsolutions.pot.insuranceservice.repository.CompanyRepository;
import com.coherentsolutions.pot.insuranceservice.repository.InsurancePackageRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
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

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  @DisplayName("Should create and retrieve Insurance Package successfully")
  void shouldCreateAndRetrieveInsurancePackageSuccessfully() throws Exception {
    InsurancePackageDto insurancePackageDto = InsurancePackageDto.builder()
        .name("Standard Health Package")
        .startDate(LocalDate.of(2025, 8, 1))
        .endDate(LocalDate.of(2025, 12, 31))
        .payrollFrequency(PayrollFrequency.MONTHLY)
        .build();

    Company company = new Company();
    company.setName("Test Company");
    company.setEmail("company@example.com");
    company.setCountryCode("USA");
    company.setWebsite("https://example.com");
    company = companyRepository.save(company);
    UUID companyId = company.getId();

    try {
      mockMvc.perform(post("/v1/company/{companyId}/plan-package", companyId)
              .contentType(String.valueOf(APPLICATION_JSON))
              .content(objectMapper.writeValueAsString(insurancePackageDto)))
          .andExpect(status().isCreated())
          .andExpect(jsonPath("$.name").value("Standard Health Package"))
          .andExpect(jsonPath("$.startDate").value("2025-08-01"))
          .andExpect(jsonPath("$.endDate").value("2025-12-31"))
          .andExpect(jsonPath("$.payrollFrequency").value("MONTHLY"))
          .andExpect(jsonPath("$.status").value("INITIALIZED"));

    } finally {

      companyRepository.deleteById(companyId);
    }

  }

  @Test
  @DisplayName("Should fail to create Insurance Package when name is missing")
  void shouldFailWhenNameIsMissing() throws Exception {
    InsurancePackageDto insurancePackageDto = InsurancePackageDto.builder()
        .startDate(LocalDate.of(2025, 8, 1))
        .endDate(LocalDate.of(2025, 12, 31))
        .payrollFrequency(PayrollFrequency.MONTHLY)
        .build();

    Company company = new Company();
    company.setName("Invalid Name Company");
    company.setEmail("test@company.com");
    company.setCountryCode("USA");
    company.setWebsite("https://example.com");
    company = companyRepository.save(company);

    UUID companyId = company.getId();

    try {
      mockMvc.perform(post("/v1/company/{companyId}/plan-package", companyId)
              .contentType(APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(insurancePackageDto)))
          .andExpect(status().isBadRequest());
    } finally {
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
      
  @Test
  @DisplayName("Should fail to create Insurance Package when endDate is before startDate")
  void shouldFailWhenEndDateBeforeStartDate() throws Exception {
    InsurancePackageDto insurancePackageDto = InsurancePackageDto.builder()
        .name("Invalid Date Package")
        .startDate(LocalDate.of(2025, 12, 31))
        .endDate(LocalDate.of(2025, 8, 1))
        .payrollFrequency(PayrollFrequency.MONTHLY)
        .build();

    Company company = new Company();
    company.setName("Invalid Date Company");
    company.setEmail("test@company.com");
    company.setCountryCode("USA");
    company.setWebsite("https://example.com");
    company = companyRepository.save(company);

    UUID companyId = company.getId();

    try {
      mockMvc.perform(post("/v1/company/{companyId}/plan-package", companyId)
              .contentType(APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(insurancePackageDto)))
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.error.code").value("BAD_REQUEST"))
          .andExpect(jsonPath("$.error.details.validationErrors.endDate[0]").value(
              "End date must be after start date"));
    } finally {
      companyRepository.deleteById(companyId);
    }
  }

}
