package com.coherentsolutions.pot.insuranceservice.dto.insurancepackage;

import com.coherentsolutions.pot.insuranceservice.enums.PackageStatus;
import com.coherentsolutions.pot.insuranceservice.enums.PayrollFrequency;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

  @NotBlank
  private String name;

  @NotNull
  private LocalDate startDate;

  @NotNull
  private LocalDate endDate;

  @NotNull
  private PayrollFrequency payrollFrequency;

  @NotNull
  private PackageStatus status;
}
