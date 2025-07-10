package com.coherentsolutions.pot.insurance_service.mapper;

import com.coherentsolutions.pot.insurance_service.dto.AddressDto;
import com.coherentsolutions.pot.insurance_service.dto.CreateCompanyRequest;
import com.coherentsolutions.pot.insurance_service.dto.CreateCompanyResponse;
import com.coherentsolutions.pot.insurance_service.dto.PhoneDto;
import com.coherentsolutions.pot.insurance_service.model.Address;
import com.coherentsolutions.pot.insurance_service.model.Company;
import com.coherentsolutions.pot.insurance_service.model.Phone;
import com.coherentsolutions.pot.insurance_service.dto.CompanyDetailsResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CompanyMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "status", constant = "ACTIVE")
    Company toEntity(CreateCompanyRequest dto);

    List<Address> toAddressEntities(List<AddressDto> dtos);
    List<AddressDto> toAddressDtos(List<Address> entities);

    List<Phone> toPhoneEntities(List<PhoneDto> dtos);
    List<PhoneDto> toPhoneDtos(List<Phone> entities);

    @Mapping(target = "companyStatus", source = "status")
    CreateCompanyResponse toCreateCompanyResponse(Company company);

    CompanyDetailsResponse toCompanyDetailsResponse(Company company);


}