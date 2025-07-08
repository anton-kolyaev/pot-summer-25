package com.coherentsolutions.pot.insurance_service.service;

import com.coherentsolutions.pot.insurance_service.dto.*;
import com.coherentsolutions.pot.insurance_service.model.Address;
import com.coherentsolutions.pot.insurance_service.model.Company;
import com.coherentsolutions.pot.insurance_service.model.Phone;
import com.coherentsolutions.pot.insurance_service.repository.CompanyRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CompanyManagementService {
    private final CompanyRepository companyRepository;
    private final AddressService addressService;
    private final PhoneService phoneService;

    public CompanyManagementService(CompanyRepository companyRepository, AddressService addressService, PhoneService phoneService) {
        this.companyRepository = companyRepository;
        this.addressService = addressService;
        this.phoneService = phoneService;
    }


    public CreateCompanyResponse createCompany(CreateCompanyRequest companyDto) {
        Company company = new Company();
        company.setName(companyDto.getName());
        company.setCountryCode(companyDto.getCountryCode());
        company.setEmail(companyDto.getEmail());
        company.setWebsite(companyDto.getWebsite());
        company.setCreatedBy(companyDto.getCreatedBy());
        company.setWebsite(companyDto.getWebsite());
        Company savedCompany = companyRepository.save(company);

        List<Address> addresses = new ArrayList<>(addressService.createAddresses(companyDto.getAddresses(), savedCompany)) ;
        List<Phone> phones = new ArrayList<>(phoneService.createPhones(companyDto.getPhones(), savedCompany));

        savedCompany.setAddresses(addresses);
        savedCompany.setPhones(phones);

        companyRepository.save(savedCompany);

        CreateCompanyResponse response = CreateCompanyResponse.builder()
                .id(savedCompany.getId())
                .name(savedCompany.getName())
                .countryCode(savedCompany.getCountryCode())
                .addresses(addresses.stream()
                        .map(address -> AddressDto.builder()
                                .id(address.getId())
                                .country(address.getCountry())
                                .city(address.getCity())
                                .state(address.getState())
                                .street(address.getStreet())
                                .building(address.getBuilding())
                                .room(address.getRoom())
                                .build())
                        .toList())
                .phones(phones.stream()
                        .map(phone -> PhoneDto.builder()
                                .id(phone.getId())
                                .code(phone.getCode())
                                .number(phone.getNumber())
                                .build())
                        .toList())
                .email(savedCompany.getEmail())
                .website(savedCompany.getWebsite())
                .companyStatus(savedCompany.getStatus())
                .createdBy(savedCompany.getCreatedBy())
                .createdAt(savedCompany.getCreatedAt())
                .updatedAt(savedCompany.getUpdatedAt())
                .build();

        return response;
    }

    public Optional<CompanyDetailsResponse> getCompanyDetails(UUID id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Company not found"));
        CompanyDetailsResponse companyResponse = CompanyDetailsResponse.builder()
                .id(company.getId())
                .name(company.getName())
                .addresses(
                        company.getAddresses().stream()
                                .map(address -> AddressDto.builder()
                                        .id(address.getId())
                                        .country(address.getCountry())
                                        .city(address.getCity())
                                        .state(address.getState())
                                        .street(address.getStreet())
                                        .building(address.getBuilding())
                                        .room(address.getRoom())
                                        .build())
                                        .collect(Collectors.toList())
                                )

                .phones(
                        company.getPhones().stream()
                                .map(phone -> PhoneDto.builder()
                                        .id(phone.getId())
                                        .code(phone.getCode())
                                        .number(phone.getNumber())
                                        .build()
                                )
                                .collect(Collectors.toList())
                )
                .email(company.getEmail())
                .website(company.getWebsite())
                .status(company.getStatus())
                .build();

        return Optional.of(companyResponse);
    }

}
