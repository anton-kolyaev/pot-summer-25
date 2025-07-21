package com.coherentsolutions.pot.insuranceservice.dto.user;

import com.coherentsolutions.pot.insuranceservice.enums.UserFunction;
import com.coherentsolutions.pot.insuranceservice.enums.UserStatus;
import com.coherentsolutions.pot.insuranceservice.model.Address;
import com.coherentsolutions.pot.insuranceservice.model.Phone;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object representing a user.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

  private UUID id;
  private String firstName;
  private String lastName;
  private String username;
  private String email;
  private LocalDate dateOfBirth;
  private String ssn;
  private List<Address> addressData;
  private List<Phone> phoneData;
  private Set<UserFunction> functions;
  private UserStatus status;
  private UUID companyId;
}
