package com.coherentsolutions.pot.insurance_service.controller;


import com.coherentsolutions.pot.insurance_service.service.CompanyManagementService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import com.coherentsolutions.pot.insurance_service.dto.CompanyDetailsResponse;


import java.util.Optional;
import java.util.UUID;


@RestController
public class AdminCompanyManagementController {
    private final CompanyManagementService companyManagementService;

    public AdminCompanyManagementController(CompanyManagementService companyManagementService) {
        this.companyManagementService = companyManagementService;
    }


    @GetMapping("/companies/{id}")
    public Optional<CompanyDetailsResponse> viewCompanyDetails(@PathVariable String id)
    {
        return companyManagementService.getCompanyDetails(UUID.fromString(id));
    }


}
