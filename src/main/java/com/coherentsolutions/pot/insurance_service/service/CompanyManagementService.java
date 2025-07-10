package com.coherentsolutions.pot.insurance_service.service;

import com.coherentsolutions.pot.insurance_service.dto.CompanyDetailsResponse;
import com.coherentsolutions.pot.insurance_service.dto.CreateCompanyRequest;
import com.coherentsolutions.pot.insurance_service.dto.CreateCompanyResponse;
import com.coherentsolutions.pot.insurance_service.mapper.CompanyMapper;
import com.coherentsolutions.pot.insurance_service.model.Company;
import com.coherentsolutions.pot.insurance_service.repository.CompanyRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;

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
        company.setAddress_data(companyMapper.convertAddressListToMap(companyDto.getAddresses()));
        company.setPhone_data(companyMapper.convertPhoneListToMap(companyDto.getPhones()));
        company.setCreatedBy(companyDto.getCreatedBy());

        companyRepository.save(company);

        return companyMapper.toCreateCompanyResponse(company);
    }

    public Optional<CompanyDetailsResponse> getCompanyDetails(UUID id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Company not found"));
        return Optional.of(companyMapper.toCompanyDetailsResponse(company));
    }

}
