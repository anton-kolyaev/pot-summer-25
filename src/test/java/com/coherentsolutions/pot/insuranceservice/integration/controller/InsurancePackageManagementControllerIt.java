package com.coherentsolutions.pot.insuranceservice.integration.controller;


import static org.hamcrest.Matchers.hasSize;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
import com.coherentsolutions.pot.insuranceservice.model.Plan;
import com.coherentsolutions.pot.insuranceservice.repository.CompanyRepository;
import com.coherentsolutions.pot.insuranceservice.repository.InsurancePackageRepository;
import com.coherentsolutions.pot.insuranceservice.repository.PlanRepository;
import com.coherentsolutions.pot.insuranceservice.repository.PlanTypeRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
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
import org.springframework.transaction.annotation.Transactional;

@Transactional
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

  @Autowired
  private PlanRepository planRepository;

  @Autowired
  private PlanTypeRepository planTypeRepository;

  private Company createCompany(String name, String email, String website) {
    Company company = new Company();
    company.setName(name);
    company.setEmail(email);
    company.setCountryCode("USA");
    company.setWebsite(website);
    return companyRepository.save(company);
  }

  private Plan createPlan(String name, BigDecimal contribution) {
    Plan plan = new Plan();
    plan.setName(name);
    plan.setType(planTypeRepository.findByIdOrThrow(1));
    plan.setContribution(contribution);
    return planRepository.save(plan);
  }

  @Test
  @DisplayName("Should get insurance packages with filters successfully")
  void shouldGetInsurancePackagesWithFilters() throws Exception {

    Company company = createCompany("Test Company", "test@example.com", "https://test.com");

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

    UUID companyId = company.getId();

    mockMvc.perform(get("/v1/company/{companyId}/plan-package", companyId)
            .param("name", "standard")
            .param("payrollFrequency", "MONTHLY")
            .contentType(APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.length()").value(1))
        .andExpect(jsonPath("$.content[0].name").value("Standard Health Package"));

  }

  @Test
  @DisplayName("Should return empty page if filter doesn't match any packages")
  void shouldReturnEmptyResultWhenNoMatch() throws Exception {
    Company company = createCompany("Test Company", "test@example.com", "https://test.com");

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

    mockMvc.perform(get("/v1/company/{companyId}/plan-package", companyId)
            .param("name", "nonexistent")
            .param("payrollFrequency", "WEEKLY")
            .contentType(APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.content.length()").value(0));

  }


  @Test
  @DisplayName("Should retrieve Insurance Package by its ID")
  void shouldRetrieveInsurancePackageById() throws Exception {
    Company company = createCompany("Test Company", "test@example.com", "https://test.com");

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

    mockMvc.perform(get("/v1/company/{companyId}/plan-package/{id}", companyId, packageId)
            .contentType(APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(packageId.toString()))
        .andExpect(jsonPath("$.name").value("Gold Health Plan"))
        .andExpect(jsonPath("$.startDate").value("2025-09-01"))
        .andExpect(jsonPath("$.endDate").value("2025-12-31"))
        .andExpect(jsonPath("$.payrollFrequency").value("MONTHLY"));

  }

  @Test
  @DisplayName("Should return 404 when insurance package is not found by ID")
  void shouldReturnNotFoundWhenInsurancePackageDoesNotExist() throws Exception {
    UUID companyId = UUID.randomUUID();
    UUID nonExistentPackageId = UUID.randomUUID();

    mockMvc.perform(
            get("/v1/company/{companyId}/plan-package/{id}", companyId, nonExistentPackageId)
                .contentType(APPLICATION_JSON))
        .andExpect(status().isNotFound());

  }

  @Test
  @DisplayName("Should create and retrieve Insurance Package successfully")
  void shouldCreateAndRetrieveInsurancePackageSuccessfully() throws Exception {

    Plan plan1 = createPlan("Health Plan A", BigDecimal.valueOf(1000));
    Plan plan2 = createPlan("Dental Plan B", BigDecimal.valueOf(2000));

    InsurancePackageDto insurancePackageDto = InsurancePackageDto.builder()
        .name("Standard Health Package")
        .startDate(LocalDate.of(2025, 8, 1))
        .endDate(LocalDate.of(2025, 12, 31))
        .payrollFrequency(PayrollFrequency.MONTHLY)
        .planIds(List.of(plan1.getId(), plan2.getId()))
        .build();

    Company company = createCompany("Test Company", "test@example.com", "https://test.com");
    UUID companyId = company.getId();

    mockMvc.perform(post("/v1/company/{companyId}/plan-package", companyId)
            .contentType(String.valueOf(APPLICATION_JSON))
            .content(objectMapper.writeValueAsString(insurancePackageDto)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.name").value("Standard Health Package"))
        .andExpect(jsonPath("$.startDate").value("2025-08-01"))
        .andExpect(jsonPath("$.endDate").value("2025-12-31"))
        .andExpect(jsonPath("$.payrollFrequency").value("MONTHLY"))
        .andExpect(jsonPath("$.status").value("INITIALIZED"))
        .andExpect(jsonPath("$.planIds").isArray())
        .andExpect(jsonPath("$.planIds", hasSize(2)));


  }

  @Test
  @DisplayName("Should fail to create Insurance Package when name is missing")
  void shouldFailWhenNameIsMissing() throws Exception {
    InsurancePackageDto insurancePackageDto = InsurancePackageDto.builder()
        .startDate(LocalDate.of(2025, 8, 1))
        .endDate(LocalDate.of(2025, 12, 31))
        .payrollFrequency(PayrollFrequency.MONTHLY)
        .build();

    Company company = createCompany("Test Company", "test@example.com", "https://test.com");

    UUID companyId = company.getId();

    mockMvc.perform(post("/v1/company/{companyId}/plan-package", companyId)
            .contentType(APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(insurancePackageDto)))
        .andExpect(status().isBadRequest());

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

    Company company = createCompany("Test Company", "test@example.com", "https://test.com");

    UUID companyId = company.getId();

    mockMvc.perform(post("/v1/company/{companyId}/plan-package", companyId)
            .contentType(APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(insurancePackageDto)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error.code").value("BAD_REQUEST"))
        .andExpect(jsonPath("$.error.details.validationErrors.endDate[0]").value(
            "End date must be after start date"));

  }

  @Test
  @DisplayName("Should deactivate Insurance Package successfully")
  void shouldDeactivateInsurancePackageSuccessfully() throws Exception {
    Company company = createCompany("Test Company", "test@example.com", "https://test.com");

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

    mockMvc.perform(delete("/v1/company/{companyId}/plan-package/{id}", companyId, packageId)
            .contentType(APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("DEACTIVATED"));

  }

  @Test
  @DisplayName("Should fail to deactivate already deactivated Insurance Package")
  void shouldFailToDeactivateAlreadyDeactivatedPackage() throws Exception {
    Company company = createCompany("Test Company", "test@example.com", "https://test.com");

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

    mockMvc.perform(delete("/v1/company/{companyId}/plan-package/{id}", companyId, packageId)
            .contentType(APPLICATION_JSON))
        .andExpect(status().isBadRequest());

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

  @Test
  @DisplayName("Should update insurance package")
  void shouldUpdateInsurancePackageWithCorrectDates() throws Exception {
    Company company = createCompany("Test Company", "test@example.com", "https://test.com");

    InsurancePackage insurancePackage = new InsurancePackage();
    insurancePackage.setName("Original Plan");
    insurancePackage.setStartDate(LocalDate.of(2025, 1, 1));
    insurancePackage.setEndDate(LocalDate.of(2025, 6, 1));
    insurancePackage.setPayrollFrequency(PayrollFrequency.MONTHLY);
    insurancePackage.setCompany(company);
    insurancePackage.setStatus(PackageStatus.DEACTIVATED);
    insurancePackage = insurancePackageRepository.save(insurancePackage);

    UUID companyId = company.getId();
    UUID packageId = insurancePackage.getId();

    LocalDate newStartDate = LocalDate.now().plusDays(1);
    LocalDate newEndDate = LocalDate.now().plusMonths(2);

    InsurancePackageDto updatedDto = InsurancePackageDto.builder()
        .name("Updated Plan")
        .startDate(newStartDate)
        .endDate(newEndDate)
        .payrollFrequency(PayrollFrequency.WEEKLY)
        .status(PackageStatus.INITIALIZED)
        .build();

    mockMvc.perform(
            put("/v1/company/{companyId}/plan-package/{id}", companyId, packageId)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedDto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Updated Plan"))
        .andExpect(jsonPath("$.startDate").value(newStartDate.toString()))
        .andExpect(jsonPath("$.endDate").value(newEndDate.toString()))
        .andExpect(jsonPath("$.payrollFrequency").value("WEEKLY"))
        .andExpect(jsonPath("$.status").value("INITIALIZED"));

  }

  @Test
  @DisplayName("Should fail update validation when end date is before start date")
  void shouldFailWhenUpdateEndDateBeforeStartDate() throws Exception {
    Company company = createCompany("Test Company", "test@example.com", "https://test.com");

    InsurancePackage insurancePackage = new InsurancePackage();
    insurancePackage.setName("Invalid Plan");
    insurancePackage.setStartDate(LocalDate.of(2025, 1, 1));
    insurancePackage.setEndDate(LocalDate.of(2025, 6, 1));
    insurancePackage.setPayrollFrequency(PayrollFrequency.MONTHLY);
    insurancePackage.setCompany(company);
    insurancePackage.setStatus(PackageStatus.DEACTIVATED);
    insurancePackage = insurancePackageRepository.save(insurancePackage);

    UUID companyId = company.getId();
    UUID packageId = insurancePackage.getId();

    LocalDate invalidStartDate = LocalDate.now().plusDays(10);
    LocalDate invalidEndDate = LocalDate.now().plusDays(5);

    InsurancePackageDto invalidDto = InsurancePackageDto.builder()
        .name("Invalid Plan")
        .startDate(invalidStartDate)
        .endDate(invalidEndDate)
        .payrollFrequency(PayrollFrequency.WEEKLY)
        .build();

    mockMvc.perform(
            put("/v1/company/{companyId}/plan-package/{id}", companyId, packageId)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error.code").value("BAD_REQUEST"))
        .andExpect(jsonPath("$.error.details.validationErrors.endDate[0]").value(
            "End date must be after start date"));

  }

  @Test
  @DisplayName("Should return 400 when updating active insurance package")
  void shouldFailWhenUpdatingActivePackage() throws Exception {
    Company company = createCompany("Test Company", "test@example.com", "https://test.com");

    InsurancePackage insurancePackage = new InsurancePackage();
    insurancePackage.setName("Active Plan");
    insurancePackage.setStartDate(LocalDate.now().minusDays(10));
    insurancePackage.setEndDate(LocalDate.now().plusDays(10));
    insurancePackage.setPayrollFrequency(PayrollFrequency.MONTHLY);
    insurancePackage.setCompany(company);
    insurancePackage.setStatus(PackageStatus.ACTIVE);
    insurancePackage = insurancePackageRepository.save(insurancePackage);

    UUID companyId = company.getId();
    UUID packageId = insurancePackage.getId();

    InsurancePackageDto updatedDto = InsurancePackageDto.builder()
        .name("Attempted Update")
        .startDate(LocalDate.now().plusDays(1))
        .endDate(LocalDate.now().plusMonths(1))
        .payrollFrequency(PayrollFrequency.WEEKLY)
        .status(PackageStatus.ACTIVE)
        .build();

    mockMvc.perform(
            put("/v1/company/{companyId}/plan-package/{id}", companyId, packageId)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedDto)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error.code").value("BAD_REQUEST"))
        .andExpect(jsonPath("$.error.message").value(
            "400 BAD_REQUEST \"Cannot update active insurance package\""));

  }

  @Test
  @DisplayName("Should return 400 when updating insurance package with start date before today")
  void shouldFailWhenUpdatingStartDateBeforeToday() throws Exception {
    Company company = createCompany("Test Company", "test@example.com", "https://test.com");

    InsurancePackage insurancePackage = new InsurancePackage();
    insurancePackage.setName("Date Validation Plan");
    insurancePackage.setStartDate(LocalDate.now().plusDays(5));
    insurancePackage.setEndDate(LocalDate.now().plusMonths(3));
    insurancePackage.setPayrollFrequency(PayrollFrequency.MONTHLY);
    insurancePackage.setCompany(company);
    insurancePackage.setStatus(PackageStatus.INITIALIZED);
    insurancePackage = insurancePackageRepository.save(insurancePackage);

    UUID companyId = company.getId();
    UUID packageId = insurancePackage.getId();

    InsurancePackageDto invalidUpdateDto = InsurancePackageDto.builder()
        .name("Date Validation Plan")
        .startDate(LocalDate.now().minusDays(1))
        .endDate(LocalDate.now().plusMonths(3))
        .payrollFrequency(PayrollFrequency.MONTHLY)
        .status(PackageStatus.INITIALIZED)
        .build();

    mockMvc.perform(
            put("/v1/company/{companyId}/plan-package/{id}", companyId, packageId)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidUpdateDto)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error.code").value("BAD_REQUEST"))
        .andExpect(jsonPath("$.error.message").value(
            "400 BAD_REQUEST \"Updated start date cannot be earlier than today\""));
  }

}
