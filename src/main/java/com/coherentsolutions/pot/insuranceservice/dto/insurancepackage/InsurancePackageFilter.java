package com.coherentsolutions.pot.insuranceservice.dto.insurancepackage;

import com.coherentsolutions.pot.insuranceservice.enums.PackageStatus;
import com.coherentsolutions.pot.insuranceservice.enums.PayrollFrequency;
import java.time.LocalDate;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class InsurancePackageFilter {

  private UUID companyId;
  private String name;
  private LocalDate startDate;
  private LocalDate endDate;
  private PayrollFrequency payrollFrequency;
  private PackageStatus status;

}
