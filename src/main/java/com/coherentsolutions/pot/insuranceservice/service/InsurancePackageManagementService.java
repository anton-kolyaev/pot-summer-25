package com.coherentsolutions.pot.insuranceservice.service;

import com.coherentsolutions.pot.insuranceservice.dto.insurancepackage.InsurancePackageDto;
import com.coherentsolutions.pot.insuranceservice.dto.insurancepackage.InsurancePackageFilter;
import com.coherentsolutions.pot.insuranceservice.enums.PackageStatus;
import com.coherentsolutions.pot.insuranceservice.mapper.InsurancePackageMapper;
import com.coherentsolutions.pot.insuranceservice.model.Company;
import com.coherentsolutions.pot.insuranceservice.model.InsurancePackage;
import com.coherentsolutions.pot.insuranceservice.repository.CompanyRepository;
import com.coherentsolutions.pot.insuranceservice.repository.InsurancePackageRepository;
import com.coherentsolutions.pot.insuranceservice.repository.InsurancePackageSpecification;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

@Service
@RequiredArgsConstructor
public class InsurancePackageManagementService {

  private final InsurancePackageRepository insurancePackageRepository;
  private final InsurancePackageMapper insurancePackageMapper;
  private final CompanyRepository companyRepository;

  public Page<InsurancePackageDto> getInsurancePackagesWithFilters(
      InsurancePackageFilter filter, Pageable pageable) {
    Page<InsurancePackage> insurancePackages = insurancePackageRepository.findAll(
        InsurancePackageSpecification.withFilters(filter), pageable);
    return insurancePackages.map(insurancePackageMapper::toInsurancePackageDto);
  }

  public InsurancePackageDto getInsurancePackageById(@PathVariable UUID id) {
    InsurancePackage insurancePackage = insurancePackageRepository.findByIdOrThrow(id);
    return insurancePackageMapper.toInsurancePackageDto(insurancePackage);
  }

  public InsurancePackageDto createInsurancePackage(
      UUID companyId,
      InsurancePackageDto insurancePackageDto) {
    InsurancePackage insurancePackage = insurancePackageMapper.toInsurancePackage(
        insurancePackageDto);
    Company company = companyRepository.findByIdOrThrow(companyId);
    insurancePackage.setCompany(company);
    insurancePackage.setStatus(PackageStatus.INITIALIZED);
    insurancePackageRepository.save(insurancePackage);

    return insurancePackageMapper.toInsurancePackageDto(insurancePackage);
  }

}
