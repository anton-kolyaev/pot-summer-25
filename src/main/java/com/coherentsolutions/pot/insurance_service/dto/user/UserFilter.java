package com.coherentsolutions.pot.insurance_service.dto.user;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

import com.coherentsolutions.pot.insurance_service.enums.UserFunction;
import com.coherentsolutions.pot.insurance_service.enums.UserStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserFilter {
    private UUID companyId;
    private String name;
    private String email;
    private LocalDate dateOfBirth;
    private UserStatus status;
    private String ssn;
    private Set<UserFunction> functions;
}
