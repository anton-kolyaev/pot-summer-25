package com.coherentsolutions.pot.insurance_service.dto.user;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.boot.autoconfigure.amqp.RabbitConnectionDetails.Address;

import com.coherentsolutions.pot.insurance_service.model.Status.UserStatus;
import com.coherentsolutions.pot.insurance_service.model.UserFunction;

import lombok.Data;

@Data
public class UserResponseDto {
    private String firstName;
    private String lastName;
    private String userName;
    private String email;
    private LocalDate dateOfBirth; 
    private String ssn;
    private Address address;
    private String phone;
    private List<UserFunction> functions;
    private UserStatus status;
    private UUID companyId; 
}
