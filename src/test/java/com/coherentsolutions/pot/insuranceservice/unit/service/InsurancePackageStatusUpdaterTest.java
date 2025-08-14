package com.coherentsolutions.pot.insuranceservice.unit.service;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.coherentsolutions.pot.insuranceservice.enums.PackageStatus;
import com.coherentsolutions.pot.insuranceservice.model.InsurancePackage;
import com.coherentsolutions.pot.insuranceservice.repository.InsurancePackageRepository;
import com.coherentsolutions.pot.insuranceservice.service.InsurancePackageStatusUpdater;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class InsurancePackageStatusUpdaterTest {

  @Mock
  private InsurancePackageRepository insurancePackageRepository;

  private InsurancePackageStatusUpdater insurancePackageStatusUpdater;

  @BeforeEach
  void setUp() {
    insurancePackageStatusUpdater = new InsurancePackageStatusUpdater(insurancePackageRepository);
  }

  @Test
  @DisplayName("Should update package status successfully")
  void shouldUpdatePackageStatusSuccessfully() {
    // Given
    InsurancePackage package1 = new InsurancePackage();
    package1.setStartDate(LocalDate.now().plusDays(1));
    package1.setEndDate(LocalDate.now().plusMonths(1));
    package1.setStatus(PackageStatus.ACTIVE);

    InsurancePackage package2 = new InsurancePackage();
    package2.setStartDate(LocalDate.now().minusDays(1));
    package2.setEndDate(LocalDate.now().plusDays(1));
    package2.setStatus(PackageStatus.INITIALIZED);

    List<InsurancePackage> packages = Arrays.asList(package1, package2);

    when(insurancePackageRepository.findAll()).thenReturn(packages);
    when(insurancePackageRepository.saveAll(anyList())).thenReturn(packages);

    // When
    insurancePackageStatusUpdater.updatePackageStatus();

    // Then
    verify(insurancePackageRepository).findAll();
    verify(insurancePackageRepository).saveAll(packages);
  }

  @Test
  @DisplayName("Should handle empty package list")
  void shouldHandleEmptyPackageList() {
    // Given
    List<InsurancePackage> packages = Arrays.asList();

    when(insurancePackageRepository.findAll()).thenReturn(packages);
    when(insurancePackageRepository.saveAll(anyList())).thenReturn(packages);

    // When
    insurancePackageStatusUpdater.updatePackageStatus();

    // Then
    verify(insurancePackageRepository).findAll();
    verify(insurancePackageRepository).saveAll(packages);
  }

  @Test
  @DisplayName("Should handle single package")
  void shouldHandleSinglePackage() {
    // Given
    InsurancePackage package1 = new InsurancePackage();
    package1.setStartDate(LocalDate.now().minusDays(1));
    package1.setEndDate(LocalDate.now().plusDays(1));
    package1.setStatus(PackageStatus.INITIALIZED);

    List<InsurancePackage> packages = Arrays.asList(package1);

    when(insurancePackageRepository.findAll()).thenReturn(packages);
    when(insurancePackageRepository.saveAll(anyList())).thenReturn(packages);

    // When
    insurancePackageStatusUpdater.updatePackageStatus();

    // Then
    verify(insurancePackageRepository).findAll();
    verify(insurancePackageRepository).saveAll(packages);
  }
}