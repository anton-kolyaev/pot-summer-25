package com.coherentsolutions.pot.insurance_service.mapper;

import com.coherentsolutions.pot.insurance_service.dto.AddressDto;
import com.coherentsolutions.pot.insurance_service.dto.CompanyDto;

import com.coherentsolutions.pot.insurance_service.dto.PhoneDto;

import com.coherentsolutions.pot.insurance_service.model.Company;
import com.coherentsolutions.pot.insurance_service.model.Address;
import com.coherentsolutions.pot.insurance_service.model.Phone;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CompanyMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "status", source = "status")
    Company toEntity(CompanyDto dto);

    CompanyDto toCompanyDto(Company company);

    List<Address> toAddressList(List<AddressDto> dtoList);
    List<AddressDto> toAddressDtoList(List<Address> entityList);

    List<Phone> toPhoneList(List<PhoneDto> dtoList);
    List<PhoneDto> toPhoneDtoList(List<Phone> entityList);

}

