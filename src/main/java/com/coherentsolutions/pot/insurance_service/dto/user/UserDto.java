package com.coherentsolutions.pot.insurance_service.dto.user;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import com.coherentsolutions.pot.insurance_service.enums.UserFunction;
import com.coherentsolutions.pot.insurance_service.enums.UserStatus;
import com.coherentsolutions.pot.insurance_service.model.Address;
import com.coherentsolutions.pot.insurance_service.model.Phone;

import lombok.Data;

@Data
public class UserDto {
    private UUID id;
    private String firstName;
    private String lastName;
    private String userName;
    private String email;
    private LocalDate dateOfBirth;
    private String ssn;
    private List<Address> address;
    private List<Phone> phone;
    private List<UserFunction> functions;
    private UserStatus status;
    private UUID companyId; 
}
