package com.coherentsolutions.pot.insurance_service.service;

import org.springframework.stereotype.Service;

import com.coherentsolutions.pot.insurance_service.dto.user.UserDto;
import com.coherentsolutions.pot.insurance_service.mapper.UserMapper;
import com.coherentsolutions.pot.insurance_service.model.User;
import com.coherentsolutions.pot.insurance_service.model.UserFunctionAssignment;
import com.coherentsolutions.pot.insurance_service.repository.UserRepository;

@Service
public class UserManagementService {

    private final UserRepository userRepository; 
    private final UserMapper userMapper;  
    
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
