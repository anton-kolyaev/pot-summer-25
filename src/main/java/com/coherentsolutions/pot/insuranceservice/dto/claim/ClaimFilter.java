package com.coherentsolutions.pot.insuranceservice.dto.claim;

import com.coherentsolutions.pot.insuranceservice.enums.ClaimStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ClaimFilter {

  private UUID claimId;
  private ClaimStatus status;
  private String planName;
  private BigDecimal amountMin;
  private BigDecimal amountMax;
  private LocalDate serviceDateFrom;
  private LocalDate serviceDateTo;
  private UUID userId;
  private UUID companyId;
}
