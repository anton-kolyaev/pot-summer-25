package com.coherentsolutions.pot.insurance_service.controller;

import com.coherentsolutions.pot.insurance_service.dto.user.UserRequestDto;
import com.coherentsolutions.pot.insurance_service.dto.user.UserResponseDto;
import com.coherentsolutions.pot.insurance_service.service.UserService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/users/requests")
    public List<UserRequestDto> getAllUserRequests() {
        return userService.getAllUserRequestDtos();
    }

    @GetMapping("/users")
    public List<UserResponseDto> getAllUserResponses() {
        return userService.getAllUserResponseDtos();
    }
}
