package com.coherentsolutions.pot.insurance_service.service;

import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.coherentsolutions.pot.insurance_service.dto.user.UserDto;
import com.coherentsolutions.pot.insurance_service.dto.user.UserFilter;
import com.coherentsolutions.pot.insurance_service.mapper.UserMapper;
import com.coherentsolutions.pot.insurance_service.model.User;
import com.coherentsolutions.pot.insurance_service.model.UserFunctionAssignment;
import com.coherentsolutions.pot.insurance_service.repository.UserRepository;
import com.coherentsolutions.pot.insurance_service.repository.UserSpecification;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserManagementService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public Page<UserDto> getUsersWithFilters(UserFilter filter, Pageable pageable) {
        Page<User> users = userRepository.findAll(UserSpecification.withFilters(filter), pageable);
        return users.map(userMapper::toDto);
    }

    public UserDto createUser(UserDto dto) {
        User user = userMapper.toEntity(dto);

        if (user.getFunctions() != null) {
            for (UserFunctionAssignment ufa : user.getFunctions()) {
                ufa.setUser(user);
            }
        }

        user = userRepository.save(user);
        return userMapper.toDto(user);
    }
}
