package com.coherentsolutions.pot.insuranceservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.coherentsolutions.pot.insuranceservice.dto.CompanyDto;
import com.coherentsolutions.pot.insuranceservice.model.Company;

@Mapper(componentModel = "spring")
public interface CompanyMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "status", source = "status")
    Company toEntity(CompanyDto dto);

    CompanyDto toCompanyDto(Company company);
}
