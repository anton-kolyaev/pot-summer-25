package com.coherentsolutions.pot.insurance_service.service;

import com.coherentsolutions.pot.insurance_service.dto.CompanyDto;
import com.coherentsolutions.pot.insurance_service.mapper.CompanyMapper;
import com.coherentsolutions.pot.insurance_service.model.Company;
import com.coherentsolutions.pot.insurance_service.repository.CompanyRepository;
import com.coherentsolutions.pot.insurance_service.enums.CompanyStatus;
import com.coherentsolutions.pot.insurance_service.dto.CompanyFilter;
import com.coherentsolutions.pot.insurance_service.repository.CompanySpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.UUID;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class CompanyManagementService {
    private final CompanyRepository companyRepository;
    private final CompanyMapper companyMapper;

    public Page<CompanyDto> getCompaniesWithFilters(CompanyFilter filter, Pageable pageable) {
        // Use JPA Specification to filter at database level with pagination
        Page<Company> companies = companyRepository.findAll(CompanySpecification.withFilters(filter), pageable);
        return companies.map(companyMapper::toCompanyDto);
    }

    public CompanyDto updateCompany(UUID id, CompanyDto request) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Company not found"));

        // Update basic fields using higher-order function
        setIfNotNull(request.getName(), company::setName);
        setIfNotNull(request.getCountryCode(), company::setCountryCode);
        setIfNotNull(request.getEmail(), company::setEmail);
        setIfNotNull(request.getWebsite(), company::setWebsite);
        
        if (request.getStatus() != null) {
            company.setStatus(CompanyStatus.valueOf(String.valueOf(request.getStatus())));
        }

        // Update address data
        if (request.getAddressData() != null) {
            company.setAddressData(companyMapper.toAddressList(request.getAddressData()));
        }

        // Update phone data
        if (request.getPhoneData() != null) {
            company.setPhoneData(companyMapper.toPhoneList(request.getPhoneData()));
        }

        company.setUpdatedAt(Instant.now());
        Company updated = companyRepository.save(company);
        return companyMapper.toCompanyDto(updated);
    }

    public CompanyDto createCompany(CompanyDto companyDto) {
        Company company = companyMapper.toEntity(companyDto);
        company.setAddressData(companyMapper.toAddressList(companyDto.getAddressData()));
        company.setPhoneData(companyMapper.toPhoneList(companyDto.getPhoneData()));
        company.setStatus(CompanyStatus.ACTIVE);
        companyRepository.save(company);

        return companyMapper.toCompanyDto(company);
    }

    public CompanyDto getCompanyDetails(UUID id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Company not found"));
        return companyMapper.toCompanyDto(company);
    }

    private <T> void setIfNotNull(T value, Consumer<T> setFunction) {
        if (value != null) {
            setFunction.accept(value);
        }
    }
}
