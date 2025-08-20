package com.coherentsolutions.pot.insuranceservice.dto.claim;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class ClaimApprovalRequest {

  @NotNull(message = "approvedAmount is required")
  @Positive(message = "approvedAmount must be positive")
  @Digits(integer = 17, fraction = 2, message = "approvedAmount must have max 2 decimals")
  private BigDecimal approvedAmount;

  private String notes;
}
