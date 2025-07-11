package com.coherentsolutions.pot.insurance_service.mapper;

import com.coherentsolutions.pot.insurance_service.dto.*;
import com.coherentsolutions.pot.insurance_service.model.Company;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mapper(componentModel = "spring")
public interface CompanyMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "status", constant = "ACTIVE")
    @Mapping(target = "addressData", expression = "java(convertAddressListToMap(dto.getAddressData()))")
    @Mapping(target = "phoneData", expression = "java(convertPhoneListToMap(dto.getPhoneData()))")
    Company toEntity(CreateCompanyRequest dto);

    @Mapping(target = "companyStatus", source = "status")
    @Mapping(target = "addressData", expression = "java(convertAddressMapToList(company.getAddressData()))")
    @Mapping(target = "phoneData", expression = "java(convertPhoneMapToList(company.getPhoneData()))")
    CreateCompanyResponse toCreateCompanyResponse(Company company);

    CompanyDetailsResponse toCompanyDetailsResponse(Company company);

    @Mapping(target = "status", expression = "java(company.getStatus() != null ? company.getStatus().name() : null)")
    @Mapping(target = "whoCreated", source = "createdBy")
    CompanyResponseDto toCompanyResponseDto(Company company);

    default Map<String, Object> convertAddressListToMap(List<AddressDto> addresses) {
        Map<String, Object> map = new HashMap<>();
        map.put("items", addresses);
        return map;
    }

    default List<AddressDto> convertAddressMapToList(Map<String, Object> addressData) {
        if (addressData == null) return Collections.emptyList();
        Object items = addressData.get("items");
        if (items instanceof List<?>) {
            return (List<AddressDto>) items;
        }
        return Collections.emptyList();
    }

    default Map<String, Object> convertPhoneListToMap(List<PhoneDto> phones) {
        Map<String, Object> map = new HashMap<>();
        map.put("items", phones);
        return map;
    }

    default List<PhoneDto> convertPhoneMapToList(Map<String, Object> phoneData) {
        if (phoneData == null) return Collections.emptyList();
        Object items = phoneData.get("items");
        if (items instanceof List<?>) {
            return (List<PhoneDto>) items;
        }
        return Collections.emptyList();
    }

}

