package com.coherentsolutions.pot.insurance_service.controller;

import com.coherentsolutions.pot.insurance_service.dto.CompanyResponseDto;
import com.coherentsolutions.pot.insurance_service.dto.CompanyFilter;
import com.coherentsolutions.pot.insurance_service.service.CompanyManagementService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AdminCompanyManagementController {
    private final CompanyManagementService companyManagementService;

    public AdminCompanyManagementController(CompanyManagementService companyManagementService) {
        this.companyManagementService = companyManagementService;
    }

    @GetMapping("/companies")
    public List<CompanyResponseDto> getCompanies(CompanyFilter filter) {
        return companyManagementService.getCompaniesWithFilters(filter);
    }

}
