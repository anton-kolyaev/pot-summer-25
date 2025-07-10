package com.coherentsolutions.pot.insurance_service.service;

import org.springframework.stereotype.Service;
import com.coherentsolutions.pot.insurance_service.model.User;
import com.coherentsolutions.pot.insurance_service.repository.UserRepository;
import com.coherentsolutions.pot.insurance_service.dto.user.UserDto;
import com.coherentsolutions.pot.insurance_service.model.UserFunctionAssignment;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    private UserRepository userRepository;


    public List<UserDto> getAllUserRequestDtos() {
        return userRepository.findAll()
                .stream()
                .map(this::convertEntityToRequestDto)
                .collect(Collectors.toList());
    }

    private UserDto convertEntityToRequestDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setFirstName(user.getFirstName());
        userDto.setLastName(user.getLastName());
        userDto.setUserName(user.getUsername());
        userDto.setEmail(user.getEmail());
        userDto.setDateOfBirth(user.getDateOfBirth());
        userDto.setSsn(user.getSsn());
        userDto.setAddress(user.getAddress());
        userDto.setPhone(user.getPhones());
        userDto.setFunctions(
            user.getFunctions()
                .stream()
                .map(UserFunctionAssignment::getFunction)
                .collect(Collectors.toList())
        );
        userDto.setStatus(user.getStatus());
        //userDto.setCompanyId(user.getCompanyId());
        return userDto;
    }

}
