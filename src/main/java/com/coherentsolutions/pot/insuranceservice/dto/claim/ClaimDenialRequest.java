package com.coherentsolutions.pot.insuranceservice.dto.claim;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ClaimDenialRequest {

  @NotBlank(message = "Reason is required")
  private String reason;

  private String notes;
}
