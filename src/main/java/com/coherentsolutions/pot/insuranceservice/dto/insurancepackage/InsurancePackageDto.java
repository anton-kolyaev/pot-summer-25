package com.coherentsolutions.pot.insuranceservice.dto.insurancepackage;

import com.coherentsolutions.pot.insuranceservice.annotation.ValidateEndDateIsAfterStartDate;
import com.coherentsolutions.pot.insuranceservice.enums.PackageStatus;
import com.coherentsolutions.pot.insuranceservice.enums.PayrollFrequency;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@ValidateEndDateIsAfterStartDate(startDate = "startDate", endDate = "endDate")
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

  private PackageStatus status;
  private List<UUID> planIds;
}
