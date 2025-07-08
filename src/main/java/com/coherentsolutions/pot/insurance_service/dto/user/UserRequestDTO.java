package com.coherentsolutions.pot.insurance_service.dto.user;

import java.time.LocalDate;
import java.util.UUID;

import com.coherentsolutions.pot.insurance_service.model.Status.UserStatus;
import com.coherentsolutions.pot.insurance_service.model.UserFunction;

import lombok.Data;

@Data
public class UserRequestDto {
    private String firstName;
    private String lastName;
    private String userName;
    private String email;
    private LocalDate dateOfBirth;
    private String ssn;
    private List<UserFunction> functions;
    private UserStatus status;
    private UUID companyId; 
}
