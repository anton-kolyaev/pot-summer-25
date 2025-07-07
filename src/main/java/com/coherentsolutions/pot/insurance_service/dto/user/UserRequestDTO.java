package com.coherentsolutions.pot.insurance_service.dto.user;

import java.util.UUID;
import java.time.LocalDate;
import lombok.Data;

@Data
public class UserRequestDTO {
    private String firstName;
    private String lastName;
    private String userName;
    private String email;
    private LocalDate DoB; //Date of Birth
    private String SSN;
    private UserFunction function;
    private UserStatus status;
    private UUID companyId; 
}
