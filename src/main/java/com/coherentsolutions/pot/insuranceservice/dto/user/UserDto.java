package com.coherentsolutions.pot.insuranceservice.dto.user;

import com.coherentsolutions.pot.insuranceservice.enums.UserFunction;
import com.coherentsolutions.pot.insuranceservice.enums.UserStatus;
import com.coherentsolutions.pot.insuranceservice.model.Address;
import com.coherentsolutions.pot.insuranceservice.model.Phone;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

  @NotBlank(message = "First name is mandatory")
  private String firstName;

  @NotBlank(message = "Last name is mandatory")
  private String lastName;

  @NotBlank(message = "User name is required")
  private String username;

  @NotBlank(message = "Email is required")
  private String email;

  @NotNull(message = "Date of Birth is required")
  private LocalDate dateOfBirth;

  @NotBlank(message = "Social security number is required")
  private String ssn;

  @NotNull(message = "Address is required")
  private List<Address> addressData;

  @NotNull(message = "Phone is required")
  private List<Phone> phoneData;
  private Set<UserFunction> functions;
  private UserStatus status;
  private UUID companyId;
}
