package com.coherentsolutions.pot.insurance_service.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;
@Data
@Builder
public class UserDto {
    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
}

