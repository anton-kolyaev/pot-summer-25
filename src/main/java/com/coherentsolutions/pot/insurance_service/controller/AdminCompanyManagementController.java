package com.coherentsolutions.pot.insurance_service.controller;


import com.coherentsolutions.pot.insurance_service.dto.CreateCompanyRequest;
import com.coherentsolutions.pot.insurance_service.dto.CreateCompanyResponse;
import com.coherentsolutions.pot.insurance_service.service.CompanyManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.coherentsolutions.pot.insurance_service.dto.CompanyDetailsResponse;


import java.util.Optional;
import java.util.UUID;


@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/companies")
public class AdminCompanyManagementController {
    private final CompanyManagementService companyManagementService;


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreateCompanyResponse addCompany(@RequestBody CreateCompanyRequest companyDto) {
        return companyManagementService.createCompany(companyDto);
    }

    @GetMapping("/{id}")
    public CompanyDetailsResponse viewCompanyDetails(@PathVariable UUID id) {
        return companyManagementService.getCompanyDetails(id);

    }

}
