package com.coherentsolutions.pot.insurance_service.controller;

import com.coherentsolutions.pot.insurance_service.dto.CompanyFilter;
import com.coherentsolutions.pot.insurance_service.dto.CompanyDetailsResponse;
import com.coherentsolutions.pot.insurance_service.dto.CreateCompanyRequest;
import com.coherentsolutions.pot.insurance_service.dto.CreateCompanyResponse;
import com.coherentsolutions.pot.insurance_service.dto.UpdateCompanyRequest;
import com.coherentsolutions.pot.insurance_service.service.CompanyManagementService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;

import java.util.UUID;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/companies")
public class AdminCompanyManagementController {
    private final CompanyManagementService companyManagementService;

    @GetMapping
    public List<CompanyDetailsResponse> getCompanies(CompanyFilter filter) {
        return companyManagementService.getCompaniesWithFilters(filter);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreateCompanyResponse addCompany(@RequestBody CreateCompanyRequest companyDto) {
        return companyManagementService.createCompany(companyDto);
    }

    @GetMapping("/{id}")
    public CompanyDetailsResponse viewCompanyDetails(@PathVariable UUID id) {
        return companyManagementService.getCompanyDetails(id);
    }
  
  @PutMapping("/{id}")
    public CompanyDetailsResponse updateCompany(@PathVariable UUID id, @RequestBody UpdateCompanyRequest request) {
        return companyManagementService.updateCompany(id, request);
    }
}
