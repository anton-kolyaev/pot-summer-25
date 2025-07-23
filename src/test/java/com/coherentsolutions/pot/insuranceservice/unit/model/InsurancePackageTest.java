package com.coherentsolutions.pot.insuranceservice.unit.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.coherentsolutions.pot.insuranceservice.enums.PackageStatus;
import com.coherentsolutions.pot.insuranceservice.model.InsurancePackage;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("InsurancePackage Entity Tests")
public class InsurancePackageTest {

  @Test
  void testGetStatus() {
    InsurancePackage pkg = new InsurancePackage();

    pkg.setStartDate(null);
    pkg.setEndDate(null);
    assertEquals(PackageStatus.DEACTIVATED, pkg.getStatus());

    pkg.setStartDate(LocalDate.now().plusDays(1));
    pkg.setEndDate(LocalDate.now().plusDays(10));
    assertEquals(PackageStatus.INITIALIZED, pkg.getStatus());

    pkg.setStartDate(LocalDate.now().minusDays(1));
    pkg.setEndDate(LocalDate.now().plusDays(1));
    assertEquals(PackageStatus.ACTIVE, pkg.getStatus());

    pkg.setStartDate(LocalDate.now().minusDays(10));
    pkg.setEndDate(LocalDate.now().minusDays(1));
    assertEquals(PackageStatus.EXPIRED, pkg.getStatus());
  }
}

