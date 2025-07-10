package com.coherentsolutions.pot.insurance_service.service;

import org.springframework.stereotype.Service;
import com.coherentsolutions.pot.insurance_service.model.User;
import com.coherentsolutions.pot.insurance_service.repository.UserRepository;
import com.coherentsolutions.pot.insurance_service.dto.user.UserRequestDto;
import com.coherentsolutions.pot.insurance_service.dto.user.UserResponseDto;
import com.coherentsolutions.pot.insurance_service.model.UserFunctionAssignment;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    private UserRepository userRepository;

    public List<UserRequestDto> getAllUserRequestDtos() {
        return userRepository.findAll()
                .stream()
                .map(this::convertEntityToRequestDto)
                .collect(Collectors.toList());
    }

    private UserRequestDto convertEntityToRequestDto(User user) {
        UserRequestDto userRequestDto = new UserRequestDto();
        userRequestDto.setFirstName(user.getFirstName());
        userRequestDto.setLastName(user.getLastName());
        userRequestDto.setUserName(user.getUsername());
        userRequestDto.setEmail(user.getEmail());
        userRequestDto.setDateOfBirth(user.getDateOfBirth());
        userRequestDto.setSsn(user.getSsn());
        userRequestDto.setFunctions(
            user.getFunctions()
                .stream()
                .map(UserFunctionAssignment::getFunction)
                .collect(Collectors.toList())
        );
        userRequestDto.setStatus(user.getStatus());
        //userRequestDto.setCompanyId(user.getCompanyId());
        return userRequestDto;
    }

    public List<UserResponseDto> getAllUserResponseDtos() {
        return userRepository.findAll()
                .stream()
                .map(this::convertEntityToResponseDto)
                .collect(Collectors.toList());
    }

    private UserResponseDto convertEntityToResponseDto(User user) {
        UserResponseDto userResponseDto = new UserResponseDto();
        userResponseDto.setFirstName(user.getFirstName());
        userResponseDto.setLastName(user.getLastName());
        userResponseDto.setUserName(user.getUsername());
        userResponseDto.setEmail(user.getEmail());
        userResponseDto.setDateOfBirth(user.getDateOfBirth());
        userResponseDto.setSsn(user.getSsn());
        userResponseDto.setFunctions(
            user.getFunctions()
                .stream()
                .map(UserFunctionAssignment::getFunction)
                .collect(Collectors.toList())
        );
        userResponseDto.setStatus(user.getStatus());
        //userResponseDto.setCompanyId(user.getCompanyId());
        return userResponseDto;
    }

}
