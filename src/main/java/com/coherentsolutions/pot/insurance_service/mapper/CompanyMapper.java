package com.coherentsolutions.pot.insurance_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.coherentsolutions.pot.insurance_service.dto.CompanyDto;
import com.coherentsolutions.pot.insurance_service.model.Company;

@Mapper(componentModel = "spring")
public interface CompanyMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "status", source = "status")
    Company toEntity(CompanyDto dto);

    CompanyDto toCompanyDto(Company company);
}
