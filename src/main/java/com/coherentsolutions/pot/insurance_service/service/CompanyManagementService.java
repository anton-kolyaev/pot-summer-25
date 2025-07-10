package com.coherentsolutions.pot.insurance_service.service;

import com.coherentsolutions.pot.insurance_service.dto.CompanyResponseDto;
import com.coherentsolutions.pot.insurance_service.dto.UpdateCompanyRequest;
import com.coherentsolutions.pot.insurance_service.mapper.CompanyMapper;
import com.coherentsolutions.pot.insurance_service.model.Company;
import com.coherentsolutions.pot.insurance_service.repository.CompanyRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.time.Instant;
import java.util.UUID;
import com.coherentsolutions.pot.insurance_service.model.enums.CompanyStatus;


@Service
public class CompanyManagementService {
    private final CompanyRepository companyRepository;
    private final CompanyMapper companyMapper;

    public CompanyManagementService(CompanyRepository companyRepository, CompanyMapper companyMapper) {
        this.companyRepository = companyRepository;
        this.companyMapper = companyMapper;
    }

    public CompanyResponseDto updateCompany(UUID id, UpdateCompanyRequest request) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Company not found"));

        // Update basic fields
        if (request.getName() != null) {
            company.setName(request.getName());
        }
        if (request.getCountryCode() != null) {
            company.setCountryCode(request.getCountryCode());
        }
        if (request.getEmail() != null) {
            company.setEmail(request.getEmail());
        }
        if (request.getWebsite() != null) {
            company.setWebsite(request.getWebsite());
        }
        if (request.getStatus() != null) {
            company.setStatus(CompanyStatus.valueOf(request.getStatus()));
        }

        // Update address data
        if (request.getAddressData() != null) {
            company.setAddressData(companyMapper.convertAddressListToMap(request.getAddressData()));
        }

        // Update phone data
        if (request.getPhoneData() != null) {
            company.setPhoneData(companyMapper.convertPhoneListToMap(request.getPhoneData()));
        }

        company.setUpdatedAt(Instant.now());
        Company updated = companyRepository.save(company);
        return companyMapper.toCompanyResponseDto(updated);
    }
}
