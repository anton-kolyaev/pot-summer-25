package com.coherentsolutions.pot.insurance_service.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.coherentsolutions.pot.insurance_service.dto.user.UserDto;
import com.coherentsolutions.pot.insurance_service.dto.user.UserFilter;
import com.coherentsolutions.pot.insurance_service.enums.UserFunction;
import com.coherentsolutions.pot.insurance_service.enums.UserStatus;
import com.coherentsolutions.pot.insurance_service.service.UserManagementService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/v1/users")
public class AdminUserManagementController {
    private final UserManagementService userManagementService;

    public AdminUserManagementController(UserManagementService userManagementService) {
        this.userManagementService = userManagementService;
    }
  
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(@Valid @RequestBody UserDto userDto) {
        return userManagementService.createUser(userDto);
    }

    @GetMapping("/users")
    public List<UserDto> getUsersWithFilters(
    @RequestParam(required = false) String name,
    @RequestParam(required = false) String email,
    @RequestParam(required = false) LocalDate dateOfBirth,
    @RequestParam(required = false) UserStatus status,
    @RequestParam(required = false) String ssn,
    @RequestParam(required = false) Set<UserFunction> functions
    ) {
    UserFilter filter = new UserFilter(name, email, dateOfBirth, status, ssn, functions);
    return userManagementService.getUsersWithFilters(filter);
    }  

}
