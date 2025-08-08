package com.coherentsolutions.pot.insuranceservice.dto.claim;

import com.coherentsolutions.pot.insuranceservice.enums.ClaimStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
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
public class ClaimDto {

  private UUID id;

  @NotBlank(message = "Claim number is required")
  private String claimNumber;

  @NotNull(message = "Number is required")
  private Integer number;

  @NotNull(message = "Status is required")
  private ClaimStatus status;

  @PastOrPresent(message = "Service date can’t be in the future")
  @NotNull(message = "Service date is required")
  private LocalDate serviceDate;

  @NotNull(message = "Consumer info is required")
  @Valid
  private ConsumerDto consumer;

  @NotNull(message = "Amount is required")
  @Positive(message = "Amount must be positive")
  @Digits(integer = 12, fraction = 2, message = "Amount must have max 2 decimals")
  private BigDecimal amount;

  @NotNull(message = "Plan ID is required")
  private UUID planId;
}
