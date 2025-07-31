package com.coherentsolutions.pot.insuranceservice.service;


import com.coherentsolutions.pot.insuranceservice.model.InsurancePackage;
import com.coherentsolutions.pot.insuranceservice.repository.InsurancePackageRepository;
import jakarta.transaction.Transactional;
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
      insurancePackage.calculateStatus(false);
    }

    insurancePackageRepository.saveAll(packages);
  }


}
