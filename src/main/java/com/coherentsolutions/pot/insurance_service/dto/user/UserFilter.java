package com.coherentsolutions.pot.insurance_service.dto.user;

import java.time.LocalDate;
import java.util.List;

import com.coherentsolutions.pot.insurance_service.enums.UserFunction;
import com.coherentsolutions.pot.insurance_service.enums.UserStatus;

public class UserFilter {
    private String name;
    private String email;
    private LocalDate dateOfBirth;
    private UserStatus status;
    private String ssn;
    private List<UserFunction> functions;

    // Default constructor
    public UserFilter() {
    }

    // Constructor with parameters
    public UserFilter(String name, String email, LocalDate dateOfBirth,
     UserStatus status, String ssn, List<UserFunction> functions) {
        this.name = name;
        this.email = email;
        this.dateOfBirth = dateOfBirth;
        this.status = status;
        this.ssn = ssn;
        this.functions = functions;
    }

    //Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public String getSsn() {
        return ssn;
    }

    public void setSsn(String ssn) {
        this.ssn = ssn;
    }

    public List<UserFunction> getFunctions() {
        return functions;
    }

    public void setFunctions(List<UserFunction> functions) {
        this.functions = functions;
    }
}
