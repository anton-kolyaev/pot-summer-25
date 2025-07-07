package com.coherentsolutions.pot.insurance_service.dto.user;

import java.util.UUID;

import org.springframework.boot.autoconfigure.amqp.RabbitConnectionDetails.Address;

import java.time.LocalDate;
import lombok.Data;

@Data
public class UserResponseDTO {
    private String firstName;
    private String lastName;
    private String userName;
    private String email;
    private LocalDate DoB; //Date of Birth
    private String SSN;
    private Address address;
    private String phone;
    private UserFunction function;
    private UserStatus status;
    private UUID companyId; 
}
