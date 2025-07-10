package com.coherentsolutions.pot.insurance_service.service;

import com.coherentsolutions.pot.insurance_service.dto.CompanyDetailsResponse;
import com.coherentsolutions.pot.insurance_service.dto.CreateCompanyRequest;
import com.coherentsolutions.pot.insurance_service.dto.CreateCompanyResponse;
import com.coherentsolutions.pot.insurance_service.mapper.CompanyMapper;
import com.coherentsolutions.pot.insurance_service.dto.CompanyResponseDto;
import com.coherentsolutions.pot.insurance_service.dto.UpdateCompanyRequest;
import com.coherentsolutions.pot.insurance_service.dto.CompanyFilter;
import com.coherentsolutions.pot.insurance_service.model.Company;
import com.coherentsolutions.pot.insurance_service.repository.CompanyRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.util.StringUtils;
import com.coherentsolutions.pot.insurance_service.model.enums.CompanyStatus;

@Service
public class CompanyManagementService {
    private final CompanyRepository companyRepository;
    private final CompanyMapper companyMapper;

    public CompanyManagementService(CompanyRepository companyRepository, CompanyMapper companyMapper) {
        this.companyRepository = companyRepository;
        this.companyMapper = companyMapper;
    }

    public CreateCompanyResponse createCompany(CreateCompanyRequest companyDto) {
        Company company = companyMapper.toEntity(companyDto);
        company.setAddressData(companyMapper.convertAddressListToMap(companyDto.getAddressData()));
        company.setPhoneData(companyMapper.convertPhoneListToMap(companyDto.getPhoneData()));
        company.setCreatedBy(companyDto.getCreatedBy());

        companyRepository.save(company);

        return companyMapper.toCreateCompanyResponse(company);
    }

    public Optional<CompanyDetailsResponse> getCompanyDetails(UUID id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Company not found"));
        return Optional.of(companyMapper.toCompanyDetailsResponse(company));
    }

    public List<CompanyResponseDto> getCompaniesWithFilters(CompanyFilter filter) {
        List<Company> companies = companyRepository.findAll();
        return companies.stream()
                .filter(company -> {
                    if (StringUtils.hasText(filter.getName()) && (company.getName() == null || !company.getName().toLowerCase().contains(filter.getName().toLowerCase()))) {
                        return false;
                    }
                    if (StringUtils.hasText(filter.getCountryCode()) && (company.getCountryCode() == null || !company.getCountryCode().toLowerCase().contains(filter.getCountryCode().toLowerCase()))) {
                        return false;
                    }
                    if (StringUtils.hasText(filter.getStatus()) && (company.getStatus() == null || !company.getStatus().name().equalsIgnoreCase(filter.getStatus()))) {
                        return false;
                    }
                    if (filter.getCreatedFrom() != null && (company.getCreatedAt() == null || company.getCreatedAt().isBefore(filter.getCreatedFrom()))) {
                        return false;
                    }
                    if (filter.getCreatedTo() != null && (company.getCreatedAt() == null || company.getCreatedAt().isAfter(filter.getCreatedTo()))) {
                        return false;
                    }
                    if (filter.getUpdatedFrom() != null && (company.getUpdatedAt() == null || company.getUpdatedAt().isBefore(filter.getUpdatedFrom()))) {
                        return false;
                    }
                    if (filter.getUpdatedTo() != null && (company.getUpdatedAt() == null || company.getUpdatedAt().isAfter(filter.getUpdatedTo()))) {
                        return false;
                    }
                    return true;
                })
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
}
