package com.coherentsolutions.pot.insuranceservice.dto.plan;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlanTypeDto {

  private Integer id;
  private String code;
  private String name;
}