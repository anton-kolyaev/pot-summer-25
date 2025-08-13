package com.coherentsolutions.pot.insuranceservice.mapper;

import com.coherentsolutions.pot.insuranceservice.dto.enrollment.EnrollmentDto;
import com.coherentsolutions.pot.insuranceservice.model.Enrollment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EnrollmentMapper {

  @Mapping(target = "user", ignore = true)
  @Mapping(target = "plan", ignore = true)
  @Mapping(target = "planContribution", ignore = true)
  Enrollment toEntity(EnrollmentDto dto);

  @Mapping(source = "user.id", target = "userId")
  @Mapping(source = "plan.id", target = "planId")
  EnrollmentDto toDto(Enrollment entity);
}