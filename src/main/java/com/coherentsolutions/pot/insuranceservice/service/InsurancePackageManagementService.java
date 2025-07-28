package com.coherentsolutions.pot.insuranceservice.service;

import com.coherentsolutions.pot.insuranceservice.dto.insurancepackage.InsurancePackageDto;
import com.coherentsolutions.pot.insuranceservice.mapper.InsurancePackageMapper;
import com.coherentsolutions.pot.insuranceservice.model.InsurancePackage;
import com.coherentsolutions.pot.insuranceservice.repository.InsurancePackageRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

@Service
@RequiredArgsConstructor
public class InsurancePackageManagementService {

  private final InsurancePackageRepository insurancePackageRepository;
  private final InsurancePackageMapper insurancePackageMapper;

  public InsurancePackageDto getInsurancePackageById(@PathVariable UUID id) {
    InsurancePackage insurancePackage = insurancePackageRepository.findByIdOrThrow(id);
    return insurancePackageMapper.toInsurancePackageDto(insurancePackage);
  }

}
