package com.coherentsolutions.pot.insurance_service.service;


import com.coherentsolutions.pot.insurance_service.mapper.CompanyMapper;
import com.coherentsolutions.pot.insurance_service.dto.CompanyResponseDto;
import com.coherentsolutions.pot.insurance_service.dto.CompanyFilter;
import com.coherentsolutions.pot.insurance_service.model.Company;
import com.coherentsolutions.pot.insurance_service.model.enums.CompanyStatus;
import com.coherentsolutions.pot.insurance_service.repository.CompanyRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.util.StringUtils;



@Service
public class CompanyManagementService {
    private final CompanyRepository companyRepository;
    private final CompanyMapper companyMapper;

    public CompanyManagementService(CompanyRepository companyRepository, CompanyMapper companyMapper) {
        this.companyRepository = companyRepository;
        this.companyMapper = companyMapper;
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
                    if (StringUtils.hasText(filter.getStatus())) {
                        try {
                            CompanyStatus filterStatus = CompanyStatus.valueOf(filter.getStatus().toUpperCase());
                            if (company.getStatus() == null || company.getStatus() != filterStatus) {
                                return false;
                            }
                        } catch (IllegalArgumentException e) {
                            // If the status is not a valid enum value, filter it out
                            return false;
                        }
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
}
