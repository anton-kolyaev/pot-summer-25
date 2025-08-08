package com.coherentsolutions.pot.insuranceservice.dto.consumer;

import com.coherentsolutions.pot.insuranceservice.model.Phone;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConsumerDto {
  
  @NotNull(message = "Consumer userId is required")
  private UUID userId;
  
  @NotBlank(message = "First name is required")
  private String firstName;

  @NotBlank(message = "Last name is required")
  private String lastName;
  
  @NotNull(message = "Phone is required")
  @Valid
  private Phone phone;
}
