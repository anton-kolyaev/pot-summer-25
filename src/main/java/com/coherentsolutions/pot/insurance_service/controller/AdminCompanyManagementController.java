package com.coherentsolutions.pot.insurance_service.controller;

import com.coherentsolutions.pot.insurance_service.dto.CompanyDto;
import com.coherentsolutions.pot.insurance_service.dto.CompanyFilter;
import com.coherentsolutions.pot.insurance_service.dto.CompanyReactivationRequest;
import com.coherentsolutions.pot.insurance_service.dto.user.UserDto;
import com.coherentsolutions.pot.insurance_service.dto.user.UserFilter;
import com.coherentsolutions.pot.insurance_service.service.CompanyManagementService;
import com.coherentsolutions.pot.insurance_service.service.UserManagementService;
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
import org.springframework.web.bind.annotation.DeleteMapping;

import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/companies")
public class AdminCompanyManagementController {
    private final CompanyManagementService companyManagementService;
    private final UserManagementService userManagementService;

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

    @DeleteMapping("/{id}")
    public CompanyDto deactivateCompany(@PathVariable UUID id) {
        return companyManagementService.deactivateCompany(id);
    }

    @PostMapping("/{id}/reactivate")
    public CompanyDto reactivateCompany(@PathVariable UUID id, @RequestBody CompanyReactivationRequest request) {
        return companyManagementService.reactivateCompany(id, request);
    }

    @GetMapping("/{id}/users")
    public Page<UserDto> getUsersOfCompany(@PathVariable UUID id, UserFilter filter, Pageable pageable) {
        filter.setCompanyId(id);
        return userManagementService.getUsersWithFilters(filter, pageable);
    }
}
