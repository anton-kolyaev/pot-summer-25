package com.coherentsolutions.pot.insuranceservice.integration.controller;


import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.coherentsolutions.pot.insuranceservice.dto.insurancepackage.InsurancePackageDto;
import com.coherentsolutions.pot.insuranceservice.dto.insurancepackage.InsurancePackageFilter;
import com.coherentsolutions.pot.insuranceservice.enums.PackageStatus;
import com.coherentsolutions.pot.insuranceservice.enums.PayrollFrequency;
import com.coherentsolutions.pot.insuranceservice.integration.IntegrationTestConfiguration;
import com.coherentsolutions.pot.insuranceservice.integration.containers.PostgresTestContainer;
import com.coherentsolutions.pot.insuranceservice.model.Company;
import com.coherentsolutions.pot.insuranceservice.model.InsurancePackage;
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

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  @DisplayName("Should get insurance packages with filters successfully")
  void shouldGetInsurancePackagesWithFilters() throws Exception {

    Company company = new Company();
    company.setName("Test Company");
    company.setEmail("test@example.com");
    company.setCountryCode("USA");
    company.setWebsite("https://test.com");
    company = companyRepository.save(company);
    UUID companyId = company.getId();

    InsurancePackage package1 = new InsurancePackage();
    package1.setName("Standard Health Package");
    package1.setStartDate(LocalDate.of(2025, 8, 1));
    package1.setEndDate(LocalDate.of(2025, 12, 31));
    package1.setPayrollFrequency(PayrollFrequency.MONTHLY);
    package1.setCompany(company);
    package1.setStatus(PackageStatus.ACTIVE);
    insurancePackageRepository.save(package1);

    InsurancePackage package2 = new InsurancePackage();
    package2.setName("Premium Health Package");
    package2.setStartDate(LocalDate.of(2025, 9, 1));
    package2.setEndDate(LocalDate.of(2026, 1, 31));
    package2.setPayrollFrequency(PayrollFrequency.WEEKLY);
    package2.setCompany(company);
    package2.setStatus(PackageStatus.ACTIVE);
    insurancePackageRepository.save(package2);

    InsurancePackageFilter filter = new InsurancePackageFilter();
    filter.setName("standard");
    filter.setPayrollFrequency(PayrollFrequency.MONTHLY);
    filter.setCompanyId(company.getId());

    try {
      mockMvc.perform(get("/v1/company/{companyId}/plan-package", companyId)
              .param("name", "standard")
              .param("payrollFrequency", "MONTHLY")
              .contentType(APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.content.length()").value(1))
          .andExpect(jsonPath("$.content[0].name").value("Standard Health Package"));
    } finally {
      companyRepository.deleteById(companyId);
    }
  }

  @Test
  @DisplayName("Should return empty page if filter doesn't match any packages")
  void shouldReturnEmptyResultWhenNoMatch() throws Exception {
    Company company = new Company();
    company.setName("Empty Filter Co");
    company.setEmail("empty@example.com");
    company.setCountryCode("USA");
    company.setWebsite("https://empty.com");
    company = companyRepository.save(company);

    InsurancePackage pkg = new InsurancePackage();
    pkg.setName("Invisible Plan");
    pkg.setStartDate(LocalDate.now());
    pkg.setEndDate(LocalDate.now().plusMonths(6));
    pkg.setPayrollFrequency(PayrollFrequency.MONTHLY);
    pkg.setCompany(company);
    pkg.setStatus(PackageStatus.ACTIVE);
    insurancePackageRepository.save(pkg);

    UUID companyId = company.getId();
    InsurancePackageFilter filter = new InsurancePackageFilter();
    filter.setName("nonexistent");
    filter.setPayrollFrequency(PayrollFrequency.WEEKLY);
    filter.setCompanyId(companyId);

    try {
      mockMvc.perform(get("/v1/company/{companyId}/plan-package", companyId)
              .param("name", "nonexistent")
              .param("payrollFrequency", "WEEKLY")
              .contentType(APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.content").isArray())
          .andExpect(jsonPath("$.content.length()").value(0));
    } finally {
      companyRepository.deleteById(companyId);
    }
  }


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

  @Test
  @DisplayName("Should deactivate Insurance Package successfully")
  void shouldDeactivateInsurancePackageSuccessfully() throws Exception {
    Company company = new Company();
    company.setName("Deactivate Co");
    company.setEmail("deactivate@company.com");
    company.setCountryCode("USA");
    company.setWebsite("https://deactivate.com");
    company = companyRepository.save(company);

    InsurancePackage insurancePackage = new InsurancePackage();
    insurancePackage.setName("Deactivation Test Plan");
    insurancePackage.setStartDate(LocalDate.now());
    insurancePackage.setEndDate(LocalDate.now().plusMonths(3));
    insurancePackage.setPayrollFrequency(PayrollFrequency.MONTHLY);
    insurancePackage.setCompany(company);
    insurancePackage.setStatus(PackageStatus.ACTIVE);
    insurancePackage = insurancePackageRepository.save(insurancePackage);

    UUID packageId = insurancePackage.getId();
    UUID companyId = company.getId();

    try {
      mockMvc.perform(delete("/v1/company/{companyId}/plan-package/{id}", companyId, packageId)
              .contentType(APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.status").value("DEACTIVATED"));
    } finally {
      companyRepository.deleteById(companyId);
    }
  }

  @Test
  @DisplayName("Should fail to deactivate already deactivated Insurance Package")
  void shouldFailToDeactivateAlreadyDeactivatedPackage() throws Exception {
    Company company = new Company();
    company.setName("Deactivated Co");
    company.setEmail("deactivated@company.com");
    company.setCountryCode("USA");
    company.setWebsite("https://deactivated.com");
    company = companyRepository.save(company);

    InsurancePackage insurancePackage = new InsurancePackage();
    insurancePackage.setName("Already Deactivated Plan");
    insurancePackage.setStartDate(LocalDate.now());
    insurancePackage.setEndDate(LocalDate.now().plusMonths(3));
    insurancePackage.setPayrollFrequency(PayrollFrequency.MONTHLY);
    insurancePackage.setCompany(company);
    insurancePackage.setStatus(PackageStatus.DEACTIVATED);
    insurancePackage = insurancePackageRepository.save(insurancePackage);

    UUID packageId = insurancePackage.getId();
    UUID companyId = company.getId();

    try {
      mockMvc.perform(delete("/v1/company/{companyId}/plan-package/{id}", companyId, packageId)
              .contentType(APPLICATION_JSON))
          .andExpect(status().isBadRequest());
    } finally {
      companyRepository.deleteById(companyId);
    }
  }

  @Test
  @DisplayName("Should return 404 when trying to deactivate non-existent Insurance Package")
  void shouldReturnNotFoundWhenDeactivatingNonExistentPackage() throws Exception {
    UUID companyId = UUID.randomUUID();
    UUID nonExistentPackageId = UUID.randomUUID();

    mockMvc.perform(
            delete("/v1/company/{companyId}/plan-package/{id}", companyId, nonExistentPackageId)
                .contentType(APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

}
