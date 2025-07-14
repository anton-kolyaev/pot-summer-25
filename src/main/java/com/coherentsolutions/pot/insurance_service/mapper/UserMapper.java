package com.coherentsolutions.pot.insurance_service.mapper;

import java.util.stream.Collectors;
import java.util.List;
import java.util.Set;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import com.coherentsolutions.pot.insurance_service.dto.user.UserDto;
import com.coherentsolutions.pot.insurance_service.enums.UserFunction;
import com.coherentsolutions.pot.insurance_service.model.User;
import com.coherentsolutions.pot.insurance_service.model.UserFunctionAssignment;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    UserDto toDto(User user);

    User toEntity(UserDto userDto);


    default Set<UserFunction> map(List<UserFunctionAssignment> assignments) {
        if (assignments == null) return null;
        return assignments.stream()
                .map(UserFunctionAssignment::getFunction)
                .collect(Collectors.toSet());
    }

    default List<UserFunctionAssignment> map(Set<UserFunction> functions){
        if (functions == null) return null;
        return functions.stream()
            .map (f -> {
                UserFunctionAssignment ufa = new UserFunctionAssignment();
                ufa.setFunction(f);
                return ufa;
            })
            .collect(Collectors.toList());
    }

}
