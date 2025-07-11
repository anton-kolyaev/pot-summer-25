package com.coherentsolutions.pot.insurance_service.controller;

import com.coherentsolutions.pot.insurance_service.dto.CompanyResponseDto;
import com.coherentsolutions.pot.insurance_service.dto.CompanyFilter;
import com.coherentsolutions.pot.insurance_service.dto.CreateCompanyRequest;
import com.coherentsolutions.pot.insurance_service.dto.CreateCompanyResponse;
import com.coherentsolutions.pot.insurance_service.service.CompanyManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.coherentsolutions.pot.insurance_service.dto.CompanyDetailsResponse;

import java.util.Optional;
import java.util.UUID;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/companies")
public class AdminCompanyManagementController {
    private final CompanyManagementService companyManagementService;

    @GetMapping
    public List<CompanyResponseDto> getCompanies(CompanyFilter filter) {
        return companyManagementService.getCompaniesWithFilters(filter);
    }
}
