package com.coherentsolutions.pot.insurance_service.service;

import com.coherentsolutions.pot.insurance_service.dto.CompanyResponseDto;
import com.coherentsolutions.pot.insurance_service.dto.UpdateCompanyRequest;
import com.coherentsolutions.pot.insurance_service.mapper.CompanyMapper;
import com.coherentsolutions.pot.insurance_service.model.Company;
import com.coherentsolutions.pot.insurance_service.repository.CompanyRepository;
import com.coherentsolutions.pot.insurance_service.model.enums.CompanyStatus;
import com.coherentsolutions.pot.insurance_service.dto.CreateCompanyRequest;
import com.coherentsolutions.pot.insurance_service.dto.CreateCompanyResponse;
import com.coherentsolutions.pot.insurance_service.dto.CompanyDetailsResponse;
import com.coherentsolutions.pot.insurance_service.dto.CompanyFilter;
import com.coherentsolutions.pot.insurance_service.repository.CompanySpecification;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.UUID;
import java.util.stream.Collectors;



@Service
@RequiredArgsConstructor
public class CompanyManagementService {
    private final CompanyRepository companyRepository;
    private final CompanyMapper companyMapper;

    public List<CompanyResponseDto> getCompaniesWithFilters(CompanyFilter filter) {
        // Use JPA Specification to filter at database level
        List<Company> companies = companyRepository.findAll(CompanySpecification.withFilters(filter));
        return companies.stream()
                .map(companyMapper::toCompanyResponseDto)
                .collect(Collectors.toList());
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

    public CreateCompanyResponse createCompany(CreateCompanyRequest companyDto) {
        Company company = companyMapper.toEntity(companyDto);
        company.setAddressData(companyMapper.convertAddressListToMap(companyDto.getAddressData()));
        company.setPhoneData(companyMapper.convertPhoneListToMap(companyDto.getPhoneData()));

        companyRepository.save(company);

        return companyMapper.toCreateCompanyResponse(company);
    }

    public CompanyDetailsResponse getCompanyDetails(UUID id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Company not found"));
        return companyMapper.toCompanyDetailsResponse(company);

    }
}
