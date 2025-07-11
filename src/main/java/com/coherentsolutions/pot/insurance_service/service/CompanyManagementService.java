package com.coherentsolutions.pot.insurance_service.service;


import com.coherentsolutions.pot.insurance_service.mapper.CompanyMapper;
import com.coherentsolutions.pot.insurance_service.dto.CompanyResponseDto;
import com.coherentsolutions.pot.insurance_service.dto.CompanyFilter;
import com.coherentsolutions.pot.insurance_service.model.Company;
import com.coherentsolutions.pot.insurance_service.repository.CompanyRepository;
import com.coherentsolutions.pot.insurance_service.repository.CompanySpecification;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import java.util.List;
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
}
