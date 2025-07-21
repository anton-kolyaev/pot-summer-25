package com.coherentsolutions.pot.insuranceservice.dto.company;

import com.coherentsolutions.pot.insuranceservice.enums.CompanyStatus;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Filter criteria for searching companies.
 */
@Setter
@Getter
@NoArgsConstructor
public class CompanyFilter {

  private String name;
  private String countryCode;
  private CompanyStatus status;
  private Instant createdFrom;
  private Instant createdTo;
  private Instant updatedFrom;
  private Instant updatedTo;
}