package com.coherentsolutions.pot.insurance_service.controller;

import com.coherentsolutions.pot.insurance_service.dto.CreateCompanyRequest;
import com.coherentsolutions.pot.insurance_service.dto.CreateCompanyResponse;
import com.coherentsolutions.pot.insurance_service.service.CompanyManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
public class AdminCompanyManagementController {
    private final CompanyManagementService companyManagementService;

    @PostMapping("/companies")
    public CreateCompanyResponse addCompany(@RequestBody CreateCompanyRequest companyDto) {
        return companyManagementService.createCompany(companyDto);
    }

}
