package com.coherentsolutions.pot.insuranceservice.dto.user;

import com.coherentsolutions.pot.insuranceservice.enums.UserFunction;
import com.coherentsolutions.pot.insuranceservice.enums.UserStatus;
import com.coherentsolutions.pot.insuranceservice.model.Address;
import com.coherentsolutions.pot.insuranceservice.model.Phone;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
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
  @Email(message = "Email should be valid")
  private String email;

  @Past(message = "Date of birth must be in the past")
  @NotNull(message = "Date of Birth is required")
  private LocalDate dateOfBirth;

  @NotBlank(message = "Social security number is required")
  private String ssn;

  @Size(min = 1, message = "At least one address is required")
  @Valid
  private List<Address> addressData;

  @Size(min = 1, message = "At least one phone is required")
  @Valid
  private List<Phone> phoneData;
  private Set<UserFunction> functions;
  private UserStatus status;
  private UUID companyId;
}
