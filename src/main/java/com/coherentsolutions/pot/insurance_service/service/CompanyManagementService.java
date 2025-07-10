package com.coherentsolutions.pot.insurance_service.service;

import com.coherentsolutions.pot.insurance_service.dto.CreateCompanyRequest;
import com.coherentsolutions.pot.insurance_service.dto.CreateCompanyResponse;
import com.coherentsolutions.pot.insurance_service.mapper.CompanyMapper;
import com.coherentsolutions.pot.insurance_service.model.Company;
import com.coherentsolutions.pot.insurance_service.repository.CompanyRepository;
import org.springframework.stereotype.Service;


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

}
