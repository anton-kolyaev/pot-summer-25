package com.coherentsolutions.pot.insuranceservice.unit.service;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.coherentsolutions.pot.insuranceservice.dto.insurancepackage.InsurancePackageDto;
import com.coherentsolutions.pot.insuranceservice.enums.PayrollFrequency;
import com.coherentsolutions.pot.insuranceservice.mapper.InsurancePackageMapper;
import com.coherentsolutions.pot.insuranceservice.model.Company;
import com.coherentsolutions.pot.insuranceservice.model.InsurancePackage;
import com.coherentsolutions.pot.insuranceservice.repository.CompanyRepository;
import com.coherentsolutions.pot.insuranceservice.repository.InsurancePackageRepository;
import com.coherentsolutions.pot.insuranceservice.service.InsurancePackageManagementService;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("Insurance Packages Management Service Tests")
public class InsurancePackageManagementServiceTest {

  @Mock
  private InsurancePackageRepository insurancePackageRepository;

  @Mock
  private InsurancePackageMapper insurancePackageMapper;

  @Mock
  private CompanyRepository companyRepository;

  @InjectMocks
  private InsurancePackageManagementService insurancePackageManagementService;


  @Test
  @DisplayName("Should create insurance package and return it's dto")
  void shouldCreateInsurancePackageSuccessfully() {
    InsurancePackageDto insurancePackageDto = InsurancePackageDto.builder()
        .name("Standard Health Package")
        .startDate(LocalDate.of(2025, 8, 1))
        .endDate(LocalDate.of(2025, 12, 31))
        .payrollFrequency(PayrollFrequency.MONTHLY)
        .build();

    InsurancePackage insurancePackage = new InsurancePackage();
    insurancePackage.setName(insurancePackageDto.getName());
    insurancePackage.setStartDate(insurancePackageDto.getStartDate());
    insurancePackage.setEndDate(insurancePackageDto.getEndDate());
    insurancePackage.setPayrollFrequency(insurancePackageDto.getPayrollFrequency());

    UUID companyId = UUID.randomUUID();

    Company company = new Company();
    company.setId(companyId);

    when(insurancePackageMapper.toInsurancePackage(insurancePackageDto)).thenReturn(
        insurancePackage);
    when(companyRepository.findByIdOrThrow(companyId)).thenReturn(company);
    when(insurancePackageRepository.save(insurancePackage)).thenReturn(insurancePackage);
    when(insurancePackageMapper.toInsurancePackageDto(insurancePackage)).thenReturn(
        insurancePackageDto);

    InsurancePackageDto result = insurancePackageManagementService.createInsurancePackage(companyId,
        insurancePackageDto);

    assertNotNull(result);
    assertEquals(insurancePackageDto, result);

    verify(insurancePackageMapper).toInsurancePackage(insurancePackageDto);
    verify(insurancePackageRepository).save(insurancePackage);
    verify(insurancePackageMapper).toInsurancePackageDto(insurancePackage);
    verify(companyRepository).findByIdOrThrow(companyId);
  }
}
