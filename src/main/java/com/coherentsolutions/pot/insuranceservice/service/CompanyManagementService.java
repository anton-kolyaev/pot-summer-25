package com.coherentsolutions.pot.insuranceservice.service;

import com.coherentsolutions.pot.insuranceservice.dto.company.CompanyDto;
import com.coherentsolutions.pot.insuranceservice.dto.company.CompanyFilter;
import com.coherentsolutions.pot.insuranceservice.dto.company.CompanyReactivationRequest;
import com.coherentsolutions.pot.insuranceservice.enums.CompanyStatus;
import com.coherentsolutions.pot.insuranceservice.enums.UserStatus;
import com.coherentsolutions.pot.insuranceservice.mapper.CompanyMapper;
import com.coherentsolutions.pot.insuranceservice.model.Company;
import com.coherentsolutions.pot.insuranceservice.repository.CompanyRepository;
import com.coherentsolutions.pot.insuranceservice.repository.CompanySpecification;
import com.coherentsolutions.pot.insuranceservice.repository.UserRepository;
import java.util.UUID;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/**
 * Service class for managing {@link Company} entities including creation, updates,
 * deactivation/reactivation, and retrieval with filtering and pagination.
 */
@Service
@RequiredArgsConstructor
public class CompanyManagementService {

  private final CompanyRepository companyRepository;
  private final UserRepository userRepository;
  private final CompanyMapper companyMapper;

  /**
   * Retrieves a paginated list of companies filtered by criteria specified in
   * {@link CompanyFilter}.
   */
  public Page<CompanyDto> getCompaniesWithFilters(CompanyFilter filter, Pageable pageable) {
    // Use JPA Specification to filter at database level with pagination
    Page<Company> companies = companyRepository.findAll(CompanySpecification.withFilters(filter),
        pageable);
    return companies.map(companyMapper::toCompanyDto);
  }

  /**
   * Updates an existing company identified by {@code id} with data from {@link CompanyDto}.
   * Prevents modification of deactivated companies.
   */
  public CompanyDto updateCompany(UUID id, CompanyDto request) {
    Company company = companyRepository.findByIdOrThrow(id);

    // Prevent modifications of deactivated companies
    if (company.getStatus() == CompanyStatus.DEACTIVATED) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          "Cannot modify a deactivated company");
    }

    // Update basic fields using higher-order function
    setIfNotNull(request.getName(), company::setName);
    setIfNotNull(request.getCountryCode(), company::setCountryCode);
    setIfNotNull(request.getEmail(), company::setEmail);
    setIfNotNull(request.getWebsite(), company::setWebsite);

    if (request.getStatus() != null) {
      company.setStatus(CompanyStatus.valueOf(String.valueOf(request.getStatus())));
    }

    // Update address data
    company.setAddressData(request.getAddressData());

    // Update phone data
    company.setPhoneData(request.getPhoneData());

    Company updated = companyRepository.save(company);
    return companyMapper.toCompanyDto(updated);
  }

  /**
   * Creates a new company entity from the given {@link CompanyDto} and persists it. Sets the
   * initial company status to ACTIVE.
   */
  public CompanyDto createCompany(CompanyDto companyDto) {
    Company company = companyMapper.toEntity(companyDto);
    company.setAddressData(companyDto.getAddressData());
    company.setPhoneData(companyDto.getPhoneData());
    company.setStatus(CompanyStatus.ACTIVE);
    companyRepository.save(company);

    return companyMapper.toCompanyDto(company);
  }

  /**
   * Retrieves the details of a company by its id.
   */
  public CompanyDto getCompanyDetails(UUID id) {
    Company company = companyRepository.findByIdOrThrow(id);
    return companyMapper.toCompanyDto(company);
  }

  private <T> void setIfNotNull(T value, Consumer<T> setFunction) {
    if (value != null) {
      setFunction.accept(value);
    }
  }

  /**
   * Deactivates the company with the specified id and sets all its users to INACTIVE.
   */
  @Transactional
  public CompanyDto deactivateCompany(UUID id) {
    Company company = companyRepository.findByIdOrThrow(id);

    if (company.getStatus() == CompanyStatus.DEACTIVATED) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Company is already deactivated");
    }

    // Deactivate the company
    company.setStatus(CompanyStatus.DEACTIVATED);
    companyRepository.save(company);

    // Deactivate all users of the company
    userRepository.updateUserStatusByCompanyId(id, UserStatus.INACTIVE);

    return companyMapper.toCompanyDto(company);
  }

  /**
   * Reactivates the company with the specified id and optionally reactivates users based on the
   * options provided in {@link CompanyReactivationRequest}.
   */
  @Transactional
  public CompanyDto reactivateCompany(UUID id, CompanyReactivationRequest request) {
    Company company = companyRepository.findByIdOrThrow(id);

    if (company.getStatus() == CompanyStatus.ACTIVE) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Company is already active");
    }

    // Reactivate the company
    company.setStatus(CompanyStatus.ACTIVE);
    companyRepository.save(company);

    // Handle user reactivation based on the request
    switch (request.getUserReactivationOption()) {
      case ALL -> userRepository.updateUserStatusByCompanyId(id, UserStatus.ACTIVE);
      case SELECTED -> {
        if (request.getSelectedUserIds() == null || request.getSelectedUserIds().isEmpty()) {
          throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
              "Selected user IDs are required when option is SELECTED");
        }
        userRepository.updateUserStatusByIds(request.getSelectedUserIds(), UserStatus.ACTIVE);
      }
      case NONE -> {
        // No users are reactivated
      }
      default -> {

      }
    }

    return companyMapper.toCompanyDto(company);
  }
}
