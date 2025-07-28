package com.coherentsolutions.pot.insuranceservice.service;

import com.coherentsolutions.pot.insuranceservice.dto.insurancepackage.InsurancePackageDto;
import com.coherentsolutions.pot.insuranceservice.dto.insurancepackage.InsurancePackageFilter;
import com.coherentsolutions.pot.insuranceservice.mapper.InsurancePackageMapper;
import com.coherentsolutions.pot.insuranceservice.model.InsurancePackage;
import com.coherentsolutions.pot.insuranceservice.repository.InsurancePackageRepository;
import com.coherentsolutions.pot.insuranceservice.repository.InsurancePackageSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InsurancePackageManagementService {

  private final InsurancePackageRepository insurancePackageRepository;
  private final InsurancePackageMapper insurancePackageMapper;

  public Page<InsurancePackageDto> getInsurancePackagesWithFilters(
      InsurancePackageFilter filter, Pageable pageable) {
    Page<InsurancePackage> insurancePackages = insurancePackageRepository.findAll(
        InsurancePackageSpecification.withFilters(filter), pageable);
    return insurancePackages.map(insurancePackageMapper::toInsurancePackageDto);
  }

}
