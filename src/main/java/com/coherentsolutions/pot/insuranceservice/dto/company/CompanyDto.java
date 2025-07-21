package com.coherentsolutions.pot.insuranceservice.dto.company;

import com.coherentsolutions.pot.insuranceservice.enums.CompanyStatus;
import com.coherentsolutions.pot.insuranceservice.model.Address;
import com.coherentsolutions.pot.insuranceservice.model.Phone;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object representing company details.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyDto {

  private UUID id;
  private String name;
  private String countryCode;
  private List<Address> addressData;
  private List<Phone> phoneData;
  private String email;
  private String website;
  private CompanyStatus status;
  private UUID createdBy;
  private Instant createdAt;
  private UUID updatedBy;
  private Instant updatedAt;

}
