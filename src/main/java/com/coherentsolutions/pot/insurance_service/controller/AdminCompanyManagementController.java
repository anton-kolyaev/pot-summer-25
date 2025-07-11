package com.coherentsolutions.pot.insurance_service.controller;


import com.coherentsolutions.pot.insurance_service.service.CompanyManagementService;
import lombok.RequiredArgsConstructor;
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

    @GetMapping("/companies/{id}")
    public CompanyDetailsResponse viewCompanyDetails(@PathVariable UUID id) {
        return companyManagementService.getCompanyDetails(id);
    }

}
