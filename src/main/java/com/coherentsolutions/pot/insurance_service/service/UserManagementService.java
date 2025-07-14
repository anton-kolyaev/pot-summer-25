package com.coherentsolutions.pot.insurance_service.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.coherentsolutions.pot.insurance_service.dto.user.UserDto;
import com.coherentsolutions.pot.insurance_service.dto.user.UserFilter;
import com.coherentsolutions.pot.insurance_service.mapper.UserMapper;
import com.coherentsolutions.pot.insurance_service.model.User;
import com.coherentsolutions.pot.insurance_service.model.UserFunctionAssignment;
import com.coherentsolutions.pot.insurance_service.repository.UserRepository;
import com.coherentsolutions.pot.insurance_service.repository.UserSpecification;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserManagementService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public List<UserDto> getUsersWithFilters(UserFilter filter) {
        List<User> users = userRepository.findAll(UserSpecification.withFilters(filter));
        return users.stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
        
    public UserManagementService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public UserDto createUser(UserDto dto) {
        User user = userMapper.toEntity(dto);

        if (user.getFunctions() != null) {
            for (UserFunctionAssignment ufa : user.getFunctions()){
                ufa.setUser(user);
            }
        }

        user = userRepository.save(user);
        return userMapper.toDto(user);
    }
}
