package com.coherentsolutions.pot.insurance_service.mapper;
  
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.coherentsolutions.pot.insurance_service.dto.AddressDto;
import com.coherentsolutions.pot.insurance_service.dto.PhoneDto;
import com.coherentsolutions.pot.insurance_service.dto.user.UserDto;
import com.coherentsolutions.pot.insurance_service.enums.UserFunction;
import com.coherentsolutions.pot.insurance_service.model.Address;
import com.coherentsolutions.pot.insurance_service.model.Phone;
import com.coherentsolutions.pot.insurance_service.model.User;
import com.coherentsolutions.pot.insurance_service.model.UserFunctionAssignment;


@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(source = "company.id", target = "companyId")
    UserDto toDto(User user);


    @Mapping(source = "companyId", target = "company.id")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "status", constant = "ACTIVE")
    User toEntity(UserDto dto);

    default Set<UserFunction> mapToFunctions(Set<UserFunctionAssignment> assignments) {
        if (assignments == null) return null;
        return assignments.stream()
                .map(UserFunctionAssignment::getFunction)
                .collect(Collectors.toSet());
    }

    default Set<UserFunctionAssignment> mapToAssignments(Set<UserFunction> functions){
        if (functions == null) return null;
        return functions.stream()
            .map (f -> {
                UserFunctionAssignment ufa = new UserFunctionAssignment();
                ufa.setFunction(f);
                return ufa;
            })
            .collect(Collectors.toSet());
    }

    List<Address> toAddressList(List<AddressDto> dtoList);
    List<AddressDto> toAddressDtoList(List<Address> entityList);

    List<Phone> toPhoneList(List<PhoneDto> dtoList);
    List<PhoneDto> toPhoneDtoList(List<Phone> entityList);


}