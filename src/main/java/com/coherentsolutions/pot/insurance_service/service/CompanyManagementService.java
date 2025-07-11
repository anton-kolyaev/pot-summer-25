package com.coherentsolutions.pot.insurance_service.service;


import com.coherentsolutions.pot.insurance_service.dto.CreateCompanyRequest;
import com.coherentsolutions.pot.insurance_service.dto.CreateCompanyResponse;

import com.coherentsolutions.pot.insurance_service.dto.CompanyDetailsResponse;

import com.coherentsolutions.pot.insurance_service.mapper.CompanyMapper;
import com.coherentsolutions.pot.insurance_service.model.Company;
import com.coherentsolutions.pot.insurance_service.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;

import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class CompanyManagementService {
    private final CompanyRepository companyRepository;
    private final CompanyMapper companyMapper;


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
