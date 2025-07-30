package com.coherentsolutions.pot.insuranceservice.controller;


import com.coherentsolutions.pot.insuranceservice.dto.insurancepackage.InsurancePackageDto;
import com.coherentsolutions.pot.insuranceservice.dto.insurancepackage.InsurancePackageFilter;
import com.coherentsolutions.pot.insuranceservice.service.InsurancePackageManagementService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/company/{companyId}/plan-package")
public class InsurancePackageManagementController {

  private final InsurancePackageManagementService insurancePackageManagementService;


  @GetMapping
  public Page<InsurancePackageDto> getInsurancePackages(@PathVariable UUID companyId,
      InsurancePackageFilter filter, Pageable pageable) {
    filter.setCompanyId(companyId);
    return insurancePackageManagementService.getInsurancePackagesWithFilters(filter, pageable);
  }

  @GetMapping("{id}")
  public InsurancePackageDto getInsurancePackage(@PathVariable UUID id) {
    return insurancePackageManagementService.getInsurancePackageById(id);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public InsurancePackageDto createInsurancePackage(
      @PathVariable UUID companyId,
      @Valid @RequestBody InsurancePackageDto insurancePackageDto) {
    return insurancePackageManagementService.createInsurancePackage(companyId, insurancePackageDto);
  }

  @DeleteMapping("{id}")
  public InsurancePackageDto deactivateInsurancePackage(@PathVariable UUID id) {
    return insurancePackageManagementService.deactivateInsurancePackage(id);
  }

}
