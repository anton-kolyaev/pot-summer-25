package com.coherentsolutions.pot.insuranceservice.dto.claim;

import com.coherentsolutions.pot.insuranceservice.dto.consumer.ConsumerDto;
import com.coherentsolutions.pot.insuranceservice.enums.ClaimStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Digits;
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

  private String claimNumber;

  private ClaimStatus status;

  @PastOrPresent(message = "Service date can’t be in the future")
  @NotNull(message = "Service date is required")
  private LocalDate serviceDate;

  @NotNull(message = "Consumer info is required")
  @Valid
  private ConsumerDto consumer;

  @NotNull(message = "Amount is required")
  @Positive(message = "Amount must be positive")
  @Digits(integer = 17, fraction = 2, message = "Amount must have max 2 decimals")
  private BigDecimal amount;

  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  @NotNull(message = "Plan ID is required")
  private UUID planId;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private String planName;
}
