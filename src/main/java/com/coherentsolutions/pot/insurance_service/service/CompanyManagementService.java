package com.coherentsolutions.pot.insurance_service.service;

import com.coherentsolutions.pot.insurance_service.dto.*;
import com.coherentsolutions.pot.insurance_service.dto.CompanyResponseDto;
import com.coherentsolutions.pot.insurance_service.dto.UpdateCompanyRequest;
import com.coherentsolutions.pot.insurance_service.dto.CompanyFilter;
import com.coherentsolutions.pot.insurance_service.model.Address;
import com.coherentsolutions.pot.insurance_service.model.Company;
import com.coherentsolutions.pot.insurance_service.model.Phone;
import com.coherentsolutions.pot.insurance_service.repository.CompanyRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.util.StringUtils;
import com.coherentsolutions.pot.insurance_service.model.enums.CompanyStatus;

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
                .map(this::toCompanyResponseDto)
                .collect(Collectors.toList());
    }

    public CompanyResponseDto updateCompany(UUID id, UpdateCompanyRequest request) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Company not found"));

        // Update company fields
        company.setName(request.getName());
        company.setCountryCode(request.getCountryCode());
        company.setEmail(request.getEmail());
        company.setWebsite(request.getWebsite());
        if (request.getStatus() != null) {
            company.setStatus(CompanyStatus.valueOf(request.getStatus()));
        }

        // Replace addresses
        company.getAddresses().clear();
        if (request.getAddresses() != null) {
            List<Address> newAddresses = request.getAddresses().stream().map(dto -> {
                Address address = new Address();
                address.setCountry(dto.getCountry());
                address.setCity(dto.getCity());
                address.setState(dto.getState());
                address.setStreet(dto.getStreet());
                address.setBuilding(dto.getBuilding());
                address.setRoom(dto.getRoom());
                address.setCompany(company);
                return address;
            }).collect(Collectors.toList());
            company.getAddresses().addAll(newAddresses);
        }

        // Replace phones
        company.getPhones().clear();
        if (request.getPhones() != null) {
            List<Phone> newPhones = request.getPhones().stream().map(dto -> {
                Phone phone = new Phone();
                phone.setCode(dto.getCode());
                phone.setNumber(dto.getNumber());
                phone.setCompany(company);
                return phone;
            }).collect(Collectors.toList());
            company.getPhones().addAll(newPhones);
        }

        Company updated = companyRepository.save(company);
        return toCompanyResponseDto(updated);
    }

    private CompanyResponseDto toCompanyResponseDto(Company company) {
        return CompanyResponseDto.builder()
                .id(company.getId())
                .name(company.getName())
                .countryCode(company.getCountryCode())
                .addresses(company.getAddresses() != null ? company.getAddresses().stream().map(address -> AddressDto.builder()
                        .id(address.getId())
                        .country(address.getCountry())
                        .city(address.getCity())
                        .state(address.getState())
                        .street(address.getStreet())
                        .building(address.getBuilding())
                        .room(address.getRoom())
                        .build()).collect(Collectors.toList()) : null)
                .phones(company.getPhones() != null ? company.getPhones().stream().map(phone -> PhoneDto.builder()
                        .id(phone.getId())
                        .code(phone.getCode())
                        .number(phone.getNumber())
                        .build()).collect(Collectors.toList()) : null)
                .email(company.getEmail())
                .website(company.getWebsite())
                .status(company.getStatus() != null ? company.getStatus().name() : null)
                .whoCreated(company.getCreatedBy())
                .createdAt(company.getCreatedAt())
                .updatedAt(company.getUpdatedAt())
                .build();
    }
}
