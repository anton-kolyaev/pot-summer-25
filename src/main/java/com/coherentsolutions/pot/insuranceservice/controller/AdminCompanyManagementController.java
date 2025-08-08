package com.coherentsolutions.pot.insuranceservice.controller;

import com.coherentsolutions.pot.insuranceservice.dto.company.CompanyDto;
import com.coherentsolutions.pot.insuranceservice.dto.company.CompanyFilter;
import com.coherentsolutions.pot.insuranceservice.dto.company.CompanyReactivationRequest;
import com.coherentsolutions.pot.insuranceservice.dto.user.UserDto;
import com.coherentsolutions.pot.insuranceservice.dto.user.UserFilter;
import com.coherentsolutions.pot.insuranceservice.service.CompanyManagementService;
import com.coherentsolutions.pot.insuranceservice.service.UserManagementService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing companies and their users by admin.
 *
 * <p>Provides endpoints for CRUD operations on companies, reactivation,
 * and listing users belonging to a company.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/companies")
public class AdminCompanyManagementController {

  private final CompanyManagementService companyManagementService;
  private final UserManagementService userManagementService;

  /**
   * Retrieves a paginated list of companies filtered by given criteria.
   */
  @GetMapping
  public Page<CompanyDto> getCompanies(CompanyFilter filter, Pageable pageable) {
    return companyManagementService.getCompaniesWithFilters(filter, pageable);
  }

  /**
   * Creates a new company.
   */
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public CompanyDto addCompany(@RequestBody CompanyDto companyDto) {
    return companyManagementService.createCompany(companyDto);
  }

  /**
   * Retrieves details of a specific company by ID.
   */
  @GetMapping("/{id}")
  public CompanyDto viewCompanyDetails(@PathVariable UUID id) {
    return companyManagementService.getCompanyDetails(id);
  }

  /**
   * Updates an existing company.
   */
  @PutMapping("/{id}")
  public CompanyDto updateCompany(@PathVariable UUID id, @RequestBody CompanyDto request) {
    return companyManagementService.updateCompany(id, request);
  }

  /**
   * Deactivates a company with users by company ID.
   */
  @DeleteMapping("/{id}")
  public CompanyDto deactivateCompany(@PathVariable UUID id) {
    return companyManagementService.deactivateCompany(id);
  }

  /**
   * Reactivates a deactivated company with users.
   */
  @PostMapping("/{id}/reactivate")
  public CompanyDto reactivateCompany(@PathVariable UUID id,
      @RequestBody CompanyReactivationRequest request) {
    return companyManagementService.reactivateCompany(id, request);
  }

  /**
   * Retrieves a paginated list of users belonging to a specific company.
   */

  @GetMapping("/{id}/users")
  public Page<UserDto> getUsersOfCompany(
      @PathVariable UUID id,
      @ParameterObject UserFilter filter,
      @ParameterObject Pageable pageable
  ) {
    filter.setCompanyId(id);  // Still need to set this manually since it's path-based
    return userManagementService.getUsersWithFilters(filter, pageable);
  }
}
