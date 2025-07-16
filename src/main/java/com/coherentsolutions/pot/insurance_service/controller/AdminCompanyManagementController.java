package com.coherentsolutions.pot.insurance_service.controller;

import com.coherentsolutions.pot.insurance_service.dto.CompanyDto;
import com.coherentsolutions.pot.insurance_service.dto.CompanyFilter;
import com.coherentsolutions.pot.insurance_service.service.CompanyManagementService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/companies")
public class AdminCompanyManagementController {
    private final CompanyManagementService companyManagementService;

    @GetMapping
    public Page<CompanyDto> getCompanies(CompanyFilter filter, Pageable pageable) {
        return companyManagementService.getCompaniesWithFilters(filter, pageable);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompanyDto addCompany(@RequestBody CompanyDto companyDto) {
        return companyManagementService.createCompany(companyDto);
    }

    @GetMapping("/{id}")
    public CompanyDto viewCompanyDetails(@PathVariable UUID id) {
        return companyManagementService.getCompanyDetails(id);
    }

    @PutMapping("/{id}")
    public CompanyDto updateCompany(@PathVariable UUID id, @RequestBody CompanyDto request) {
        return companyManagementService.updateCompany(id, request);
    }
}
