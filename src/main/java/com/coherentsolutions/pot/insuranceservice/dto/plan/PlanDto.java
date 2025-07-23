package com.coherentsolutions.pot.insuranceservice.dto.plan;


import com.coherentsolutions.pot.insuranceservice.enums.PlanType;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlanDto {

  private UUID id;
  private String name;
  private PlanType type;
  private BigDecimal contribution;

}
