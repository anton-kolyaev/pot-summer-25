package com.coherentsolutions.pot.insuranceservice.dto.enrollment;

import com.coherentsolutions.pot.insuranceservice.enums.EnrollmentStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnrollmentDto {

  private UUID id;

  @NotNull(message = "User ID is required")
  private UUID userId;

  @NotNull(message = "Plan ID is required")
  private UUID planId;

  @NotNull(message = "Election amount is required")
  @Positive(message = "Election amount must be positive")
  private BigDecimal electionAmount;

  @NotNull(message = "Plan contribution is required")
  @Positive(message = "Plan contribution must be positive")
  private BigDecimal planContribution;

  private EnrollmentStatus status;

  @NotNull(message = "Start date is required")
  private LocalDate startDate;

  private LocalDate endDate;


}
