package com.coherentsolutions.pot.insuranceservice.dto.consumer;

import com.coherentsolutions.pot.insuranceservice.model.Phone;
import com.fasterxml.jackson.annotation.JsonProperty;
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

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private String firstName;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private String lastName;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private Phone phone;
}
