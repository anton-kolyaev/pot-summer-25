package com.coherentsolutions.pot.insuranceservice.integration.controller;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
import com.coherentsolutions.pot.insuranceservice.service.InsurancePackageManagementService;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
  private InsurancePackageManagementService insurancePackageManagementService;

  @Test
  @DisplayName("Should get insurance packages with filters successfully")
  void shouldGetInsurancePackagesWithFilters() {

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

    Pageable pageable = PageRequest.of(0, 10);

    try {
      Page<InsurancePackageDto> result = insurancePackageManagementService.getInsurancePackagesWithFilters(
          filter, pageable);

      assertNotNull(result);
      assertEquals(1, result.getContent().size());
      assertEquals("Standard Health Package", result.getContent().get(0).getName());

    } finally {
      List<InsurancePackage> packages = insurancePackageRepository.findAllByCompanyId(companyId);
      insurancePackageRepository.deleteAll(packages);
      companyRepository.deleteById(companyId);
    }
  }

  @Test
  @DisplayName("Should return empty page if filter doesn't match any packages")
  void shouldReturnEmptyResultWhenNoMatch() {
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

    Pageable pageable = PageRequest.of(0, 10);

    try {
      Page<InsurancePackageDto> result = insurancePackageManagementService.getInsurancePackagesWithFilters(
          filter, pageable);
      assertNotNull(result);
      assertEquals(0, result.getContent().size());
    } finally {
      List<InsurancePackage> packages = insurancePackageRepository.findAllByCompanyId(companyId);
      insurancePackageRepository.deleteAll(packages);
      companyRepository.deleteById(companyId);
    }
  }
}


