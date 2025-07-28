package com.coherentsolutions.pot.insuranceservice.service;

import com.coherentsolutions.pot.insuranceservice.enums.PackageStatus;
import com.coherentsolutions.pot.insuranceservice.model.InsurancePackage;
import com.coherentsolutions.pot.insuranceservice.repository.InsurancePackageRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InsurancePackageStatusUpdater {

  private final InsurancePackageRepository insurancePackageRepository;

  @Scheduled(cron = "0 0 0 * * *")
  @Transactional
  public void updatePackageStatus() {
    List<InsurancePackage> packages = insurancePackageRepository.findAll();

    for (InsurancePackage insurancePackage : packages) {
      if (insurancePackage.getStatus() != PackageStatus.DEACTIVATED) {
        LocalDate now = LocalDate.now();
        if (now.isBefore(insurancePackage.getStartDate())) {
          insurancePackage.setStatus(PackageStatus.INITIALIZED);
        } else if (!now.isAfter(insurancePackage.getEndDate())) {
          insurancePackage.setStatus(PackageStatus.ACTIVE);
        } else {
          insurancePackage.setStatus(PackageStatus.EXPIRED);
        }
      }
    }

    insurancePackageRepository.saveAll(packages);
  }

}
