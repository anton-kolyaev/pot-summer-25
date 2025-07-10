package com.coherentsolutions.pot.insurance_service.service;

import com.coherentsolutions.pot.insurance_service.dto.*;
import com.coherentsolutions.pot.insurance_service.model.Address;
import com.coherentsolutions.pot.insurance_service.model.Company;
import com.coherentsolutions.pot.insurance_service.model.Phone;
import com.coherentsolutions.pot.insurance_service.model.enums.CompanyStatus;
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

    public CompanyManagementService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }


    public CreateCompanyResponse createCompany(CreateCompanyRequest companyDto) {
        Company company = new Company();
        company.setName(companyDto.getName());
        company.setCountryCode(companyDto.getCountryCode());
        company.setEmail(companyDto.getEmail());
        company.setWebsite(companyDto.getWebsite());
        company.setStatus(CompanyStatus.ACTIVE);
        company.setCreatedBy(companyDto.getCreatedBy());

        List<Address> addresses = companyDto.getAddresses().stream()
                .map(dto -> {
                    Address address = new Address();
                    address.setCountry(dto.getCountry());
                    address.setCity(dto.getCity());
                    address.setState(dto.getState());
                    address.setStreet(dto.getStreet());
                    address.setBuilding(dto.getBuilding());
                    address.setRoom(dto.getRoom());
                    return address;
                })
                .toList();

        List<Phone> phones = companyDto.getPhones().stream()
                .map(dto -> {
                    Phone phone = new Phone();
                    phone.setCode(dto.getCode());
                    phone.setNumber(dto.getNumber());
                    return phone;
                })
                .toList();

        company.setAddresses(addresses);
        company.setPhones(phones);

        companyRepository.save(company);

        return CreateCompanyResponse.builder()
                .id(company.getId())
                .name(company.getName())
                .countryCode(company.getCountryCode())
                .addresses(addresses.stream()
                        .map(address -> AddressDto.builder()
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
                                .code(phone.getCode())
                                .number(phone.getNumber())
                                .build())
                        .toList())
                .email(company.getEmail())
                .website(company.getWebsite())
                .companyStatus(company.getStatus())
                .createdBy(company.getCreatedBy())
                .createdAt(company.getCreatedAt())
                .updatedAt(company.getUpdatedAt())
                .build();
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
