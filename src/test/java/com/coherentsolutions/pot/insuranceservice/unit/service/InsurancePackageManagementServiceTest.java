package com.coherentsolutions.pot.insuranceservice.unit.service;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.coherentsolutions.pot.insuranceservice.dto.insurancepackage.InsurancePackageDto;
import com.coherentsolutions.pot.insuranceservice.enums.PayrollFrequency;
import com.coherentsolutions.pot.insuranceservice.mapper.InsurancePackageMapper;
import com.coherentsolutions.pot.insuranceservice.model.InsurancePackage;
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
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
@DisplayName("Insurance Packages Management Service Tests")
public class InsurancePackageManagementServiceTest {

  @Mock
  private InsurancePackageRepository insurancePackageRepository;

  @Mock
  private InsurancePackageMapper insurancePackageMapper;

  @InjectMocks
  private InsurancePackageManagementService insurancePackageManagementService;

  @Test
  @DisplayName("Should get insurance package by it's id")
  void shouldGetInsurancePackageById() throws Exception {
    UUID packageId = UUID.randomUUID();

    InsurancePackage insurancePackage = new InsurancePackage();
    insurancePackage.setId(packageId);
    insurancePackage.setName("Standard Health Package");
    insurancePackage.setStartDate(LocalDate.of(2025, 8, 1));
    insurancePackage.setEndDate(LocalDate.of(2025, 12, 31));
    insurancePackage.setPayrollFrequency(PayrollFrequency.MONTHLY);

    InsurancePackageDto insurancePackageDto = InsurancePackageDto.builder()
        .id(packageId)
        .name("Standard Health Package")
        .startDate(LocalDate.of(2025, 8, 1))
        .endDate(LocalDate.of(2025, 12, 31))
        .payrollFrequency(PayrollFrequency.MONTHLY)
        .build();

    when(insurancePackageRepository.findByIdOrThrow(packageId)).thenReturn(insurancePackage);
    when(insurancePackageMapper.toInsurancePackageDto(insurancePackage)).thenReturn(
        insurancePackageDto);

    InsurancePackageDto result = insurancePackageManagementService.getInsurancePackageById(
        packageId);

    assertNotNull(result);
    assertEquals(insurancePackageDto, result);

    verify(insurancePackageRepository).findByIdOrThrow(packageId);
    verify(insurancePackageMapper).toInsurancePackageDto(insurancePackage);
  }

  @Test
  @DisplayName("Should throw Insurance package not found when trying to get non-existent package")
  void shouldThrowInsurancePackageNotFound() {
    UUID packageId = UUID.randomUUID();

    when(insurancePackageRepository.findByIdOrThrow(packageId)).thenThrow(
        new ResponseStatusException(HttpStatus.NOT_FOUND,
            "Insurance package not found")
    );

    assertThrows(ResponseStatusException.class, () ->
        insurancePackageManagementService.getInsurancePackageById(packageId));

    verify(insurancePackageRepository).findByIdOrThrow(packageId);
  }
}
