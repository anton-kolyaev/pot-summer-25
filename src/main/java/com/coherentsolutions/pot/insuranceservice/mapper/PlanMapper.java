package com.coherentsolutions.pot.insuranceservice.mapper;

import com.coherentsolutions.pot.insuranceservice.dto.plan.PlanDto;
import com.coherentsolutions.pot.insuranceservice.model.Plan;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PlanMapper {

  @Mapping(source = "type.code", target = "type")
  PlanDto toDto(Plan plan);

  @Mapping(target = "type", ignore = true)
  PlanDto toEntity(PlanDto planDto);
}
