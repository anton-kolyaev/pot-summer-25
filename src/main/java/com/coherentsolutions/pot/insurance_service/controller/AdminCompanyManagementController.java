package com.coherentsolutions.pot.insurance_service.controller;

import com.coherentsolutions.pot.insurance_service.dto.CompanyDetailsResponse;
import com.coherentsolutions.pot.insurance_service.dto.CreateCompanyRequest;
import com.coherentsolutions.pot.insurance_service.dto.CreateCompanyResponse;
import com.coherentsolutions.pot.insurance_service.service.CompanyManagementService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.UUID;

@RestController
public class AdminCompanyManagementController {
    private final CompanyManagementService companyManagementService;

    public AdminCompanyManagementController(CompanyManagementService companyManagementService) {
        this.companyManagementService = companyManagementService;
    }


    @PostMapping("/companies")
    public ResponseEntity<CreateCompanyResponse> addCompany(@RequestBody CreateCompanyRequest companyDto) {
        CreateCompanyResponse response = companyManagementService.createCompany(companyDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @ResponseStatus(HttpStatus.FOUND)
    @GetMapping("/companies/{id}")
    public Optional<CompanyDetailsResponse> viewCompanyDetails(@PathVariable String id)
    {
        return companyManagementService.getCompanyDetails(UUID.fromString(id));
    }

}
