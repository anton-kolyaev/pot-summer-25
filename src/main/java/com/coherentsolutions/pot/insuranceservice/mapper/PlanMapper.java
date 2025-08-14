package com.coherentsolutions.pot.insuranceservice.mapper;

import com.coherentsolutions.pot.insuranceservice.dto.plan.PlanDto;
import com.coherentsolutions.pot.insuranceservice.dto.plan.PlanTypeDto;
import com.coherentsolutions.pot.insuranceservice.model.Plan;
import com.coherentsolutions.pot.insuranceservice.model.PlanType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PlanMapper {

  @Mapping(source = "type.id", target = "type")
  PlanDto toDto(Plan plan);

  PlanTypeDto toDto(PlanType planType);

  @Mapping(target = "type", ignore = true)
  @Mapping(target = "insurancePackages", ignore = true)
  Plan toEntity(PlanDto planDto);

}
