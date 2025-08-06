package com.coherentsolutions.pot.insuranceservice.service;


import com.coherentsolutions.pot.insuranceservice.dto.insurancepackage.InsurancePackageDto;
import com.coherentsolutions.pot.insuranceservice.dto.insurancepackage.InsurancePackageFilter;
import com.coherentsolutions.pot.insuranceservice.enums.PackageStatus;
import com.coherentsolutions.pot.insuranceservice.mapper.InsurancePackageMapper;
import com.coherentsolutions.pot.insuranceservice.model.Company;
import com.coherentsolutions.pot.insuranceservice.model.InsurancePackage;
import com.coherentsolutions.pot.insuranceservice.model.Plan;
import com.coherentsolutions.pot.insuranceservice.repository.CompanyRepository;
import com.coherentsolutions.pot.insuranceservice.repository.InsurancePackageRepository;
import com.coherentsolutions.pot.insuranceservice.repository.InsurancePackageSpecification;
import com.coherentsolutions.pot.insuranceservice.repository.PlanRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class InsurancePackageManagementService {

  private final InsurancePackageRepository insurancePackageRepository;
  private final InsurancePackageMapper insurancePackageMapper;
  private final CompanyRepository companyRepository;
  private final PlanRepository planRepository;

  private void validateOnUpdate(InsurancePackage insurancePackage,
      InsurancePackageDto insurancePackageDto) {
    if (insurancePackage.getStatus() == PackageStatus.ACTIVE) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          "Cannot update active insurance package");
    }

    LocalDate today = LocalDate.now();
    if (insurancePackageDto.getStartDate() != null
        && !insurancePackage.getStartDate().isEqual(insurancePackageDto.getStartDate())
        && insurancePackageDto.getStartDate().isBefore(today)) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          "Updated start date cannot be earlier than today");
    }
  }

  private void validateAndSetPlans(InsurancePackageDto insurancePackageDto, InsurancePackage insurancePackage) {
    if (!CollectionUtils.isEmpty(insurancePackageDto.getPlanIds())) {
      List<Plan> plans = planRepository.findAllById(insurancePackageDto.getPlanIds());
      if (plans.size() != insurancePackageDto.getPlanIds().size()) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Some plan IDs are invalid");
      }
      insurancePackage.setPlans(plans);
    }
  }

  @Transactional(readOnly = true)
  public Page<InsurancePackageDto> getInsurancePackagesWithFilters(
      InsurancePackageFilter filter, Pageable pageable) {
    Page<InsurancePackage> insurancePackages = insurancePackageRepository.findAll(
        InsurancePackageSpecification.withFilters(filter), pageable);
    return insurancePackages.map(insurancePackageMapper::toInsurancePackageDto);
  }

  @Transactional(readOnly = true)
  public InsurancePackageDto getInsurancePackageById(UUID id) {
    InsurancePackage insurancePackage = insurancePackageRepository.findByIdOrThrow(id);
    return insurancePackageMapper.toInsurancePackageDto(insurancePackage);
  }

  @Transactional
  public InsurancePackageDto createInsurancePackage(
      UUID companyId,
      InsurancePackageDto insurancePackageDto) {
    InsurancePackage insurancePackage = insurancePackageMapper.toInsurancePackage(
        insurancePackageDto);

    validateAndSetPlans(insurancePackageDto, insurancePackage);

    Company company = companyRepository.findByIdOrThrow(companyId);
    insurancePackage.setCompany(company);
    insurancePackage.setStatus(PackageStatus.INITIALIZED);
    insurancePackageRepository.save(insurancePackage);

    return insurancePackageMapper.toInsurancePackageDto(insurancePackage);
  }

  @Transactional
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

  @Transactional
  public InsurancePackageDto updateInsurancePackage(UUID id,
      InsurancePackageDto insurancePackageDto) {
    InsurancePackage insurancePackage = insurancePackageRepository.findByIdOrThrow(id);

    validateOnUpdate(insurancePackage, insurancePackageDto);
    validateAndSetPlans(insurancePackageDto, insurancePackage);

    insurancePackage.setName(insurancePackageDto.getName());
    insurancePackage.setStartDate(insurancePackageDto.getStartDate());
    insurancePackage.setEndDate(insurancePackageDto.getEndDate());
    insurancePackage.setPayrollFrequency(insurancePackageDto.getPayrollFrequency());

    if (insurancePackageDto.getStatus() == PackageStatus.DEACTIVATED) {
      insurancePackage.setStatus(PackageStatus.DEACTIVATED);
    } else {
      insurancePackage.calculateStatus(true);
    }

    insurancePackageRepository.save(insurancePackage);
    return insurancePackageMapper.toInsurancePackageDto(insurancePackage);
  }


}
