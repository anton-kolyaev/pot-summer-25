package com.coherentsolutions.pot.insurance_service.dto.user;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.coherentsolutions.pot.insurance_service.enums.UserFunction;
import com.coherentsolutions.pot.insurance_service.enums.UserStatus;
import com.coherentsolutions.pot.insurance_service.model.Address;
import com.coherentsolutions.pot.insurance_service.model.Phone;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private UUID id;
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private LocalDate dateOfBirth;
    private String ssn;
    private List<Address> addressData;
    private List<Phone> phoneData;
    private Set<UserFunction> functions;
    private UserStatus status;
    private UUID companyId; 
}
