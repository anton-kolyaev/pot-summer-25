package com.coherentsolutions.pot.insurance_service.service;


import com.coherentsolutions.pot.insurance_service.mapper.CompanyMapper;
import com.coherentsolutions.pot.insurance_service.dto.CompanyResponseDto;
import com.coherentsolutions.pot.insurance_service.dto.CompanyFilter;
import com.coherentsolutions.pot.insurance_service.model.Company;
import com.coherentsolutions.pot.insurance_service.repository.CompanyRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;



@Service
public class CompanyManagementService {
    private final CompanyRepository companyRepository;
    private final CompanyMapper companyMapper;
    private final CompanyFilterStrategyManager filterStrategyManager;

    public CompanyManagementService(
            CompanyRepository companyRepository, 
            CompanyMapper companyMapper,
            CompanyFilterStrategyManager filterStrategyManager) {
        this.companyRepository = companyRepository;
        this.companyMapper = companyMapper;
        this.filterStrategyManager = filterStrategyManager;
    }

    public List<CompanyResponseDto> getCompaniesWithFilters(CompanyFilter filter) {
        List<Company> companies = companyRepository.findAll();
        return companies.stream()
                .filter(filterStrategyManager.createFilterPredicate(filter))
                .map(companyMapper::toCompanyResponseDto)
                .collect(Collectors.toList());
    }
}
