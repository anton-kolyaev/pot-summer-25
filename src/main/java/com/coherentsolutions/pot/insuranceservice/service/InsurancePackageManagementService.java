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
import java.time.LocalDate;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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

  public InsurancePackageDto getInsurancePackageById(UUID id) {
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

  public InsurancePackageDto deactivateInsurancePackage(UUID id) {
    InsurancePackage insurancePackage = insurancePackageRepository.findByIdOrThrow(id);

    if (insurancePackage.getStatus() == PackageStatus.DEACTIVATED) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          "Insurance package is already deactivated");
    }
    insurancePackage.setStatus(PackageStatus.DEACTIVATED);
    insurancePackageRepository.save(insurancePackage);

    return insurancePackageMapper.toInsurancePackageDto(insurancePackage);
  }

  public InsurancePackageDto updateInsurancePackage(UUID id,
      InsurancePackageDto insurancePackageDto) {
    InsurancePackage insurancePackage = insurancePackageRepository.findByIdOrThrow(id);
    if (insurancePackage.getStatus() != PackageStatus.ACTIVE) {
      insurancePackage.setName(insurancePackageDto.getName());
      insurancePackage.setStartDate(insurancePackageDto.getStartDate());
      insurancePackage.setEndDate(insurancePackageDto.getEndDate());
      insurancePackage.setPayrollFrequency(insurancePackageDto.getPayrollFrequency());

      if (insurancePackageDto.getStatus() == PackageStatus.DEACTIVATED || (
          insurancePackageDto.getStatus() == null
              && insurancePackage.getStatus() == PackageStatus.DEACTIVATED)) {
        insurancePackage.setStatus(PackageStatus.DEACTIVATED);
      } else {
        LocalDate now = LocalDate.now();
        if (now.isBefore(insurancePackage.getStartDate())) {
          insurancePackage.setStatus(PackageStatus.INITIALIZED);
        } else if (!now.isAfter(insurancePackage.getEndDate())) {
          insurancePackage.setStatus(PackageStatus.ACTIVE);
        } else {
          insurancePackage.setStatus(PackageStatus.EXPIRED);
        }
      }

      insurancePackageRepository.save(insurancePackage);
      return insurancePackageMapper.toInsurancePackageDto(insurancePackage);

    } else {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          "Cannot update active insurance package");
    }
  }


}
