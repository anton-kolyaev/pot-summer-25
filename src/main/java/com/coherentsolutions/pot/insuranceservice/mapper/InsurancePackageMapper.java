package com.coherentsolutions.pot.insuranceservice.mapper;

import com.coherentsolutions.pot.insuranceservice.dto.insurancepackage.InsurancePackageDto;
import com.coherentsolutions.pot.insuranceservice.model.InsurancePackage;
import com.coherentsolutions.pot.insuranceservice.model.Plan;
import java.util.List;
import java.util.UUID;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface InsurancePackageMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "company", ignore = true)
  @Mapping(target = "plans", ignore = true)
  InsurancePackage toInsurancePackage(InsurancePackageDto insurancePackageDto);

  @Mapping(target = "planIds", source = "plans", qualifiedByName = "mapPlansToPlanIds")
  InsurancePackageDto toInsurancePackageDto(InsurancePackage insurancePackage);

  @Named("mapPlansToPlanIds")
  default List<UUID> mapPlansToPlanIds(List<Plan> plans) {
    if (plans == null) {
      return null;
    }
    return plans.stream()
        .map(Plan::getId)
        .toList();
  }
}
