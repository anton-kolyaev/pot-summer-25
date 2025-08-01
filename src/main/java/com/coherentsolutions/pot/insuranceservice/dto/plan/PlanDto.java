package com.coherentsolutions.pot.insuranceservice.dto.plan;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlanDto {

  private UUID id;

  @NotBlank(message = "Name is mandatory")
  private String name;

  @NotNull(message = "Type is required")
  private Integer type;

  @NotNull(message = "Contribution is required")
  @Positive(message = "Contribution must be positive")
  private BigDecimal contribution;

  private UUID insurancePackageId;

}
