package com.coherentsolutions.pot.insurance_service.service;

import java.time.Instant;
import java.util.UUID;
import java.util.function.Consumer;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.coherentsolutions.pot.insurance_service.dto.CompanyDto;
import com.coherentsolutions.pot.insurance_service.dto.CompanyReactivationRequest;
import com.coherentsolutions.pot.insurance_service.mapper.CompanyMapper;
import com.coherentsolutions.pot.insurance_service.model.Company;
import com.coherentsolutions.pot.insurance_service.repository.CompanyRepository;
import com.coherentsolutions.pot.insurance_service.repository.UserRepository;
import com.coherentsolutions.pot.insurance_service.enums.CompanyStatus;
import com.coherentsolutions.pot.insurance_service.enums.UserStatus;
import com.coherentsolutions.pot.insurance_service.dto.CompanyFilter;
import com.coherentsolutions.pot.insurance_service.repository.CompanySpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import com.coherentsolutions.pot.insurance_service.dto.CompanyFilter;
import com.coherentsolutions.pot.insurance_service.enums.CompanyStatus;
import com.coherentsolutions.pot.insurance_service.mapper.CompanyMapper;
import com.coherentsolutions.pot.insurance_service.model.Company;
import com.coherentsolutions.pot.insurance_service.repository.CompanyRepository;
import com.coherentsolutions.pot.insurance_service.repository.CompanySpecification;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CompanyManagementService {
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final CompanyMapper companyMapper;

    public Page<CompanyDto> getCompaniesWithFilters(CompanyFilter filter, Pageable pageable) {
        // Use JPA Specification to filter at database level with pagination
        Page<Company> companies = companyRepository.findAll(CompanySpecification.withFilters(filter), pageable);
        return companies.map(companyMapper::toCompanyDto);
    }

    public CompanyDto updateCompany(UUID id, CompanyDto request) {
        Company company = companyRepository.findByIdOrThrow(id);

        // Prevent modifications of deactivated companies
        if (company.getStatus() == CompanyStatus.DEACTIVATED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot modify a deactivated company");
        }

        // Update basic fields using higher-order function
        setIfNotNull(request.getName(), company::setName);
        setIfNotNull(request.getCountryCode(), company::setCountryCode);
        setIfNotNull(request.getEmail(), company::setEmail);
        setIfNotNull(request.getWebsite(), company::setWebsite);
        
        if (request.getStatus() != null) {
            company.setStatus(CompanyStatus.valueOf(String.valueOf(request.getStatus())));
        }

        // Update address data
        company.setAddressData(request.getAddressData());

        // Update phone data
        company.setPhoneData(request.getPhoneData());

        Company updated = companyRepository.save(company);
        return companyMapper.toCompanyDto(updated);
    }

    public CompanyDto createCompany(CompanyDto companyDto) {
        Company company = companyMapper.toEntity(companyDto);
        company.setAddressData(companyDto.getAddressData());
        company.setPhoneData(companyDto.getPhoneData());
        company.setStatus(CompanyStatus.ACTIVE);
        companyRepository.save(company);

        return companyMapper.toCompanyDto(company);
    }

    public CompanyDto getCompanyDetails(UUID id) {
        Company company = companyRepository.findByIdOrThrow(id);
        return companyMapper.toCompanyDto(company);
    }

    private <T> void setIfNotNull(T value, Consumer<T> setFunction) {
        if (value != null) {
            setFunction.accept(value);
        }
    }

    @Transactional
    public CompanyDto deactivateCompany(UUID id) {
        Company company = companyRepository.findByIdOrThrow(id);

        if (company.getStatus() == CompanyStatus.DEACTIVATED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Company is already deactivated");
        }

        // Deactivate the company
        company.setStatus(CompanyStatus.DEACTIVATED);
        companyRepository.save(company);

        // Deactivate all users of the company
        userRepository.updateUserStatusByCompanyId(id, UserStatus.INACTIVE);

        return companyMapper.toCompanyDto(company);
    }

    @Transactional
    public CompanyDto reactivateCompany(UUID id, CompanyReactivationRequest request) {
        Company company = companyRepository.findByIdOrThrow(id);

        if (company.getStatus() == CompanyStatus.ACTIVE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Company is already active");
        }

        // Reactivate the company
        company.setStatus(CompanyStatus.ACTIVE);
        companyRepository.save(company);

        // Handle user reactivation based on the request
        switch (request.getUserReactivationOption()) {
            case ALL -> userRepository.updateUserStatusByCompanyId(id, UserStatus.ACTIVE);
            case SELECTED -> {
                if (request.getSelectedUserIds() == null || request.getSelectedUserIds().isEmpty()) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Selected user IDs are required when option is SELECTED");
                }
                userRepository.updateUserStatusByIds(request.getSelectedUserIds(), UserStatus.ACTIVE);
            }
            case NONE -> {
                // No users are reactivated
            }
        }

        return companyMapper.toCompanyDto(company);
    }
}
