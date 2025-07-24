package com.coherentsolutions.pot.insuranceservice.dto.insurancepackage;

import com.coherentsolutions.pot.insuranceservice.enums.PackageStatus;
import com.coherentsolutions.pot.insuranceservice.enums.PayrollFrequency;
import java.time.LocalDate;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InsurancePackageDto {

  private UUID id;
  private String name;
  private LocalDate startDate;
  private LocalDate endDate;
  private PayrollFrequency payrollFrequency;
  private PackageStatus status;
}
