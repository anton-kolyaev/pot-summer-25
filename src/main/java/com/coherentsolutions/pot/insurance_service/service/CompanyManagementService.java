package com.coherentsolutions.pot.insurance_service.service;

import com.coherentsolutions.pot.insurance_service.repository.CompanyRepository;
import org.springframework.stereotype.Service;

@Service
public class CompanyManagementService {
    private final CompanyRepository companyRepository;
    public CompanyManagementService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

}
