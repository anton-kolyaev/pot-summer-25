package com.coherentsolutions.pot.insurance_service.controller;

import com.coherentsolutions.pot.insurance_service.dto.CreateCompanyRequest;
import com.coherentsolutions.pot.insurance_service.dto.CreateCompanyResponse;
import com.coherentsolutions.pot.insurance_service.service.CompanyManagementService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


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

}
