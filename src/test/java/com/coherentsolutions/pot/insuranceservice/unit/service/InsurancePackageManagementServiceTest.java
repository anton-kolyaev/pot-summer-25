package com.coherentsolutions.pot.insuranceservice.unit.service;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.coherentsolutions.pot.insuranceservice.dto.insurancepackage.InsurancePackageDto;
import com.coherentsolutions.pot.insuranceservice.dto.insurancepackage.InsurancePackageFilter;
import com.coherentsolutions.pot.insuranceservice.enums.PackageStatus;
import com.coherentsolutions.pot.insuranceservice.enums.PayrollFrequency;
import com.coherentsolutions.pot.insuranceservice.mapper.InsurancePackageMapper;
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
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;


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
  @DisplayName("Should get insurance packages with filters including status, startDate and endDate")
  void shouldGetInsurancePackagesWithFilters() {
    InsurancePackageFilter filter = new InsurancePackageFilter();
    filter.setName("Standard");
    filter.setPayrollFrequency(PayrollFrequency.MONTHLY);
    filter.setStatus(PackageStatus.ACTIVE);
    filter.setStartDate(LocalDate.of(2025, 8, 1));
    filter.setEndDate(LocalDate.of(2025, 12, 31));

    InsurancePackage insurancePackage = new InsurancePackage();
    UUID packageId = UUID.randomUUID();
    insurancePackage.setId(packageId);
    insurancePackage.setName("Standard Health Package");
    insurancePackage.setPayrollFrequency(PayrollFrequency.MONTHLY);
    insurancePackage.setStatus(PackageStatus.ACTIVE);
    insurancePackage.setStartDate(LocalDate.of(2025, 8, 1));
    insurancePackage.setEndDate(LocalDate.of(2025, 12, 31));

    InsurancePackage nonMatchingPackage = new InsurancePackage();
    nonMatchingPackage.setId(UUID.randomUUID());
    nonMatchingPackage.setName("Premium Health Package");
    nonMatchingPackage.setPayrollFrequency(PayrollFrequency.WEEKLY);
    nonMatchingPackage.setStatus(PackageStatus.DEACTIVATED);
    nonMatchingPackage.setStartDate(LocalDate.of(2025, 6, 1));
    nonMatchingPackage.setEndDate(LocalDate.of(2025, 7, 1));

    Pageable pageable = PageRequest.of(0, 10);
    Page<InsurancePackage> packagePage = new PageImpl<>(List.of(insurancePackage), pageable, 1);

    InsurancePackageDto dto = InsurancePackageDto.builder()
        .id(packageId)
        .name("Standard Health Package")
        .payrollFrequency(PayrollFrequency.MONTHLY)
        .startDate(LocalDate.of(2025, 8, 1))
        .endDate(LocalDate.of(2025, 12, 31))
        .status(PackageStatus.ACTIVE)
        .build();

    when(insurancePackageRepository.findAll(any(Specification.class), eq(pageable)))
        .thenReturn(packagePage);
    when(insurancePackageMapper.toInsurancePackageDto(insurancePackage))
        .thenReturn(dto);

    Page<InsurancePackageDto> result =
        insurancePackageManagementService.getInsurancePackagesWithFilters(filter, pageable);

    assertNotNull(result);
    assertEquals(1, result.getContent().size());
    assertEquals(dto, result.getContent().get(0));

    verify(insurancePackageRepository).findAll(any(Specification.class), eq(pageable));
    verify(insurancePackageMapper).toInsurancePackageDto(insurancePackage);
  }

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
  @DisplayName("Should return empty page when no insurance packages match filters")
  void shouldReturnEmptyPageWhenNoPackagesMatch() {

    InsurancePackageFilter filter = new InsurancePackageFilter();
    Pageable pageable = PageRequest.of(0, 10);
    Page<InsurancePackage> emptyPage = Page.empty(pageable);

    when(insurancePackageRepository.findAll(any(Specification.class), eq(pageable)))
        .thenReturn(emptyPage);

    Page<InsurancePackageDto> result = insurancePackageManagementService
        .getInsurancePackagesWithFilters(filter, pageable);

    assertNotNull(result);
    assertEquals(0, result.getTotalElements());

    verify(insurancePackageRepository).findAll(any(Specification.class), eq(pageable));
    verifyNoInteractions(insurancePackageMapper);
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

  @Test
  @DisplayName("Should deactivate active insurance package successfully")
  void shouldDeactivateInsurancePackageSuccessfully() {
    UUID packageId = UUID.randomUUID();
    InsurancePackage insurancePackage = new InsurancePackage();
    insurancePackage.setId(packageId);
    insurancePackage.setStatus(PackageStatus.ACTIVE);

    InsurancePackageDto insurancePackageDto = InsurancePackageDto.builder()
        .id(packageId)
        .status(PackageStatus.DEACTIVATED)
        .build();

    when(insurancePackageRepository.findByIdOrThrow(packageId)).thenReturn(insurancePackage);
    when(insurancePackageRepository.save(insurancePackage)).thenReturn(insurancePackage);
    when(insurancePackageMapper.toInsurancePackageDto(insurancePackage)).thenReturn(
        insurancePackageDto);

    InsurancePackageDto result = insurancePackageManagementService.deactivateInsurancePackage(
        packageId);

    assertNotNull(result);
    assertEquals(PackageStatus.DEACTIVATED, result.getStatus());

    verify(insurancePackageRepository).findByIdOrThrow(packageId);
    verify(insurancePackageRepository).save(insurancePackage);
    verify(insurancePackageMapper).toInsurancePackageDto(insurancePackage);
  }

  @Test
  @DisplayName("Should throw BAD_REQUEST when deactivating already deactivated package")
  void shouldFailToDeactivateAlreadyDeactivatedPackage() {

    UUID packageId = UUID.randomUUID();
    InsurancePackage insurancePackage = new InsurancePackage();
    insurancePackage.setId(packageId);
    insurancePackage.setStatus(PackageStatus.DEACTIVATED);

    when(insurancePackageRepository.findByIdOrThrow(packageId)).thenReturn(insurancePackage);

    ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
        insurancePackageManagementService.deactivateInsurancePackage(packageId)
    );

    assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    assertEquals("Insurance package is already deactivated", exception.getReason());
    verify(insurancePackageRepository).findByIdOrThrow(packageId);
    verifyNoInteractions(insurancePackageMapper);
    verify(insurancePackageRepository, org.mockito.Mockito.never()).save(any());
  }

  @Test
  @DisplayName("Should throw NOT_FOUND when deactivating non-existent package")
  void shouldThrowNotFoundWhenPackageDoesNotExist() {
    UUID packageId = UUID.randomUUID();

    when(insurancePackageRepository.findByIdOrThrow(packageId))
        .thenThrow(
            new ResponseStatusException(HttpStatus.NOT_FOUND, "Insurance package not found"));

    assertThrows(ResponseStatusException.class, () ->
        insurancePackageManagementService.deactivateInsurancePackage(packageId)
    );

    verify(insurancePackageRepository).findByIdOrThrow(packageId);
    verifyNoInteractions(insurancePackageMapper);
  }

  @Test
  @DisplayName("Should update insurance package")
  void shouldUpdateInsurancePackageWithCorrectDates() {
    UUID packageId = UUID.randomUUID();

    InsurancePackage existingPackage = new InsurancePackage();
    existingPackage.setId(packageId);
    existingPackage.setStatus(PackageStatus.DEACTIVATED);
    existingPackage.setStartDate(LocalDate.of(2025, 1, 1));
    existingPackage.setEndDate(LocalDate.of(2025, 6, 1));

    InsurancePackageDto updateDto = InsurancePackageDto.builder()
        .name("Updated Plan")
        .startDate(LocalDate.now().plusDays(1))
        .endDate(LocalDate.now().plusMonths(2))
        .payrollFrequency(PayrollFrequency.WEEKLY)
        .status(PackageStatus.INITIALIZED)
        .build();

    InsurancePackage updatedPackage = new InsurancePackage();
    updatedPackage.setId(packageId);
    updatedPackage.setName(updateDto.getName());
    updatedPackage.setStartDate(updateDto.getStartDate());
    updatedPackage.setEndDate(updateDto.getEndDate());
    updatedPackage.setPayrollFrequency(updateDto.getPayrollFrequency());
    updatedPackage.setStatus(updateDto.getStatus());

    when(insurancePackageRepository.findByIdOrThrow(packageId)).thenReturn(existingPackage);
    when(insurancePackageRepository.save(existingPackage)).thenReturn(existingPackage);
    when(insurancePackageMapper.toInsurancePackageDto(existingPackage)).thenReturn(updateDto);

    InsurancePackageDto result = insurancePackageManagementService.updateInsurancePackage(packageId,
        updateDto);

    assertNotNull(result);
    assertEquals("Updated Plan", result.getName());
    assertEquals(updateDto.getStartDate(), result.getStartDate());
    assertEquals(updateDto.getEndDate(), result.getEndDate());
    assertEquals(PayrollFrequency.WEEKLY, result.getPayrollFrequency());
    assertEquals(PackageStatus.INITIALIZED, result.getStatus());

    verify(insurancePackageRepository).findByIdOrThrow(packageId);
    verify(insurancePackageRepository).save(existingPackage);
    verify(insurancePackageMapper).toInsurancePackageDto(existingPackage);
  }

  @Test
  @DisplayName("Should return 400 when updating active insurance package")
  void shouldReturn400WhenUpdatingActiveInsurancePackage() {
    UUID packageId = UUID.randomUUID();

    InsurancePackage existingPackage = new InsurancePackage();
    existingPackage.setId(packageId);
    existingPackage.setStatus(PackageStatus.ACTIVE);
    existingPackage.setStartDate(LocalDate.now().minusDays(10));
    existingPackage.setEndDate(LocalDate.now().plusMonths(1));

    InsurancePackageDto updateDto = InsurancePackageDto.builder()
        .name("Attempted Update")
        .startDate(LocalDate.now())
        .endDate(LocalDate.now().plusMonths(2))
        .payrollFrequency(PayrollFrequency.MONTHLY)
        .status(PackageStatus.INITIALIZED)
        .build();

    when(insurancePackageRepository.findByIdOrThrow(packageId)).thenReturn(existingPackage);

    ResponseStatusException e = assertThrows(ResponseStatusException.class, () -> {
      insurancePackageManagementService.updateInsurancePackage(packageId, updateDto);
    });

    assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
    assertEquals("Cannot update active insurance package", e.getReason());

    verify(insurancePackageRepository).findByIdOrThrow(packageId);
  }

  @Test
  @DisplayName("Should return 400 when updating insurance package with start date before today")
  void shouldReturn400WhenUpdatingInsurancePackageWithStartDateBeforeToday() {
    UUID packageId = UUID.randomUUID();

    InsurancePackage existingPackage = new InsurancePackage();
    existingPackage.setId(packageId);
    existingPackage.setStatus(PackageStatus.DEACTIVATED);
    existingPackage.setStartDate(LocalDate.now().plusDays(1));
    existingPackage.setEndDate(LocalDate.now().plusMonths(1));

    InsurancePackageDto updateDto = InsurancePackageDto.builder()
        .name("Invalid Start Date Update")
        .startDate(LocalDate.now().minusDays(1))
        .endDate(LocalDate.now().plusMonths(2))
        .payrollFrequency(PayrollFrequency.MONTHLY)
        .status(PackageStatus.INITIALIZED)
        .build();

    when(insurancePackageRepository.findByIdOrThrow(packageId)).thenReturn(existingPackage);

    ResponseStatusException e = assertThrows(ResponseStatusException.class, () -> {
      insurancePackageManagementService.updateInsurancePackage(packageId, updateDto);
    });

    assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
    assertEquals("Updated start date cannot be earlier than today", e.getReason());

    verify(insurancePackageRepository).findByIdOrThrow(packageId);
  }
}
