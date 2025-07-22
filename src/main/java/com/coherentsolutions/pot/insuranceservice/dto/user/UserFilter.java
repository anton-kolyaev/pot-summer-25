package com.coherentsolutions.pot.insuranceservice.dto.user;

import com.coherentsolutions.pot.insuranceservice.enums.UserFunction;
import com.coherentsolutions.pot.insuranceservice.enums.UserStatus;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Filter criteria for searching users.
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserFilter {
  private UUID companyId;
  private String name;
  private String email;
  private LocalDate dateOfBirth;
  private UserStatus status;
  private String ssn;
  private Set<UserFunction> functions;
}
