package com.coherentsolutions.pot.insurance_service.service;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.coherentsolutions.pot.insurance_service.dto.user.UserDto;
import com.coherentsolutions.pot.insurance_service.dto.user.UserFilter;
import com.coherentsolutions.pot.insurance_service.enums.UserFunction;
import com.coherentsolutions.pot.insurance_service.mapper.UserMapper;
import com.coherentsolutions.pot.insurance_service.model.User;
import com.coherentsolutions.pot.insurance_service.model.UserFunctionAssignment;
import com.coherentsolutions.pot.insurance_service.repository.UserRepository;
import com.coherentsolutions.pot.insurance_service.repository.UserSpecification;
import static com.coherentsolutions.pot.insurance_service.util.ObjectUtils.setIfNotNull;

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

    public UserDto updateUser(UUID id, UserDto request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        setIfNotNull(request.getFirstName(), user::setFirstName);
        setIfNotNull(request.getLastName(), user::setLastName);
        setIfNotNull(request.getUsername(), user::setUsername);
        setIfNotNull(request.getEmail(), user::setEmail);

        user.setPhoneData(request.getPhoneData());
        user.setAddressData(request.getAddressData());

        if (request.getFunctions() != null) {
            Set<UserFunction> incomingFunctions = request.getFunctions();
            Set<UserFunctionAssignment> currentAssignments = user.getFunctions();

            currentAssignments.removeIf(assignment -> !incomingFunctions.contains(assignment.getFunction()));

            Set<UserFunction> currentFunctions = currentAssignments.stream()
                    .map(UserFunctionAssignment::getFunction)
                    .collect(Collectors.toSet());

            for (UserFunction function : incomingFunctions) {
                if (!currentFunctions.contains(function)) {
                    UserFunctionAssignment newAssignment = new UserFunctionAssignment();
                    newAssignment.setFunction(function);
                    newAssignment.setUser(user);
                    currentAssignments.add(newAssignment);
                }
            }

            user.setFunctions(currentAssignments);
        }

        User updated = userRepository.save(user);
        return userMapper.toDto(updated);

    }
}
