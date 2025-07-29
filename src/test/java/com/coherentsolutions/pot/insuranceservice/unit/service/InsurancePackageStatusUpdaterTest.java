package com.coherentsolutions.pot.insuranceservice.unit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.coherentsolutions.pot.insuranceservice.enums.PackageStatus;
import com.coherentsolutions.pot.insuranceservice.model.InsurancePackage;
import com.coherentsolutions.pot.insuranceservice.repository.InsurancePackageRepository;
import com.coherentsolutions.pot.insuranceservice.service.InsurancePackageStatusUpdater;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class InsurancePackageStatusUpdaterTest {

  private final LocalDate today = LocalDate.now();
  @Mock
  private InsurancePackageRepository insurancePackageRepository;
  @InjectMocks
  private InsurancePackageStatusUpdater statusUpdater;

  @Test
  void shouldSetStatusToInitializedIfStartDateIsInFuture() {
    InsurancePackage pkg = new InsurancePackage();
    pkg.setStartDate(today.plusDays(5));
    pkg.setEndDate(today.plusMonths(1));
    pkg.setStatus(PackageStatus.ACTIVE);

    when(insurancePackageRepository.findAll()).thenReturn(List.of(pkg));

    statusUpdater.updatePackageStatus();

    assertEquals(PackageStatus.INITIALIZED, pkg.getStatus());
    verify(insurancePackageRepository).saveAll(List.of(pkg));
  }

  @Test
  void shouldSetStatusToActiveIfTodayIsBetweenStartAndEnd() {
    InsurancePackage pkg = new InsurancePackage();
    pkg.setStartDate(today.minusDays(1));
    pkg.setEndDate(today.plusDays(1));
    pkg.setStatus(PackageStatus.INITIALIZED);

    when(insurancePackageRepository.findAll()).thenReturn(List.of(pkg));

    statusUpdater.updatePackageStatus();

    assertEquals(PackageStatus.ACTIVE, pkg.getStatus());
    verify(insurancePackageRepository).saveAll(List.of(pkg));
  }

  @Test
  void shouldSetStatusToExpiredIfEndDateIsBeforeToday() {
    InsurancePackage pkg = new InsurancePackage();
    pkg.setStartDate(today.minusMonths(2));
    pkg.setEndDate(today.minusDays(1));
    pkg.setStatus(PackageStatus.ACTIVE);

    when(insurancePackageRepository.findAll()).thenReturn(List.of(pkg));

    statusUpdater.updatePackageStatus();

    assertEquals(PackageStatus.EXPIRED, pkg.getStatus());
    verify(insurancePackageRepository).saveAll(List.of(pkg));
  }

  @Test
  void shouldNotChangeStatusIfAlreadyDeactivated() {
    InsurancePackage pkg = new InsurancePackage();
    pkg.setStartDate(today.minusDays(10));
    pkg.setEndDate(today.plusDays(10));
    pkg.setStatus(PackageStatus.DEACTIVATED);

    when(insurancePackageRepository.findAll()).thenReturn(List.of(pkg));

    statusUpdater.updatePackageStatus();

    assertEquals(PackageStatus.DEACTIVATED, pkg.getStatus());
    verify(insurancePackageRepository).saveAll(List.of(pkg));
  }

  @Test
  void shouldHandleEmptyPackageListGracefully() {
    when(insurancePackageRepository.findAll()).thenReturn(List.of());

    statusUpdater.updatePackageStatus();

    verify(insurancePackageRepository).findAll();
    verify(insurancePackageRepository).saveAll(List.of());
  }
}