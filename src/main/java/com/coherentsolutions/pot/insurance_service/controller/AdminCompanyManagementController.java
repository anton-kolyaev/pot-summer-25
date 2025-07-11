package com.coherentsolutions.pot.insurance_service.controller;


import com.coherentsolutions.pot.insurance_service.dto.CreateCompanyRequest;
import com.coherentsolutions.pot.insurance_service.dto.CreateCompanyResponse;
import com.coherentsolutions.pot.insurance_service.service.CompanyManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.web.bind.annotation.RestController;
import com.coherentsolutions.pot.insurance_service.dto.CompanyDetailsResponse;


import java.util.Optional;
import java.util.UUID;


@RestController
@RequiredArgsConstructor
public class AdminCompanyManagementController {
    private final CompanyManagementService companyManagementService;


    @PostMapping("/companies")
    @ResponseStatus(HttpStatus.CREATED)
    public CreateCompanyResponse addCompany(@RequestBody CreateCompanyRequest companyDto) {
        return companyManagementService.createCompany(companyDto);
    }

    @GetMapping("/companies/{id}")
    public CompanyDetailsResponse viewCompanyDetails(@PathVariable UUID id) {
        return companyManagementService.getCompanyDetails(id);

    }

}
