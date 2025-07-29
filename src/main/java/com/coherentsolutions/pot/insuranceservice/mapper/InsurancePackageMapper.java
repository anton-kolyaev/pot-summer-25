package com.coherentsolutions.pot.insuranceservice.mapper;

import com.coherentsolutions.pot.insuranceservice.dto.insurancepackage.InsurancePackageDto;
import com.coherentsolutions.pot.insuranceservice.model.InsurancePackage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface InsurancePackageMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "company", ignore = true)
  InsurancePackage toInsurancePackage(InsurancePackageDto insurancePackageDto);

  InsurancePackageDto toInsurancePackageDto(InsurancePackage insurancePackage);
}
