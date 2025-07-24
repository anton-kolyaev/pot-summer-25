package com.coherentsolutions.pot.insuranceservice.unit.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.coherentsolutions.pot.insuranceservice.enums.PackageStatus;
import com.coherentsolutions.pot.insuranceservice.enums.PayrollFrequency;
import com.coherentsolutions.pot.insuranceservice.model.InsurancePackage;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("InsurancePackage Entity Tests")
public class InsurancePackageTest {


  private InsurancePackage buildPackage(PackageStatus manualStatus, LocalDate start,
      LocalDate end) {
    InsurancePackage insurancePackage = new InsurancePackage();
    insurancePackage.setManualStatus(manualStatus);
    insurancePackage.setStartDate(start);
    insurancePackage.setEndDate(end);
    insurancePackage.setPayrollFrequency(PayrollFrequency.MONTHLY);
    return insurancePackage;
  }

  @Test
  void returnsDeactivatedWhenManualStatusIsDeactivated() {
    InsurancePackage pkg = buildPackage(
        PackageStatus.DEACTIVATED,
        LocalDate.now().minusDays(10),
        LocalDate.now().plusDays(10)
    );

    assertEquals(PackageStatus.DEACTIVATED, pkg.getEffectiveStatus());
  }

  @Test
  void returnsInitializedWhenNowIsBeforeStartDate() {
    InsurancePackage pkg = buildPackage(
        PackageStatus.INITIALIZED,
        LocalDate.now().plusDays(5),
        LocalDate.now().plusDays(10)
    );

    assertEquals(PackageStatus.INITIALIZED, pkg.getEffectiveStatus());
  }

  @Test
  void returnsActiveWhenNowIsBetweenStartAndEndDate() {
    InsurancePackage pkg = buildPackage(
        PackageStatus.INITIALIZED,
        LocalDate.now().minusDays(5),
        LocalDate.now().plusDays(5)
    );

    assertEquals(PackageStatus.ACTIVE, pkg.getEffectiveStatus());
  }

  @Test
  void returnsExpiredWhenNowIsAfterEndDate() {
    InsurancePackage pkg = buildPackage(
        PackageStatus.INITIALIZED,
        LocalDate.now().minusDays(10),
        LocalDate.now().minusDays(1)
    );

    assertEquals(PackageStatus.EXPIRED, pkg.getEffectiveStatus());
  }
}

