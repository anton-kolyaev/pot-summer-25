package com.coherentsolutions.pot.insuranceservice.controller;


import com.coherentsolutions.pot.insuranceservice.dto.insurancepackage.InsurancePackageDto;
import com.coherentsolutions.pot.insuranceservice.service.InsurancePackageManagementService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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

  @GetMapping("{id}")
  public InsurancePackageDto getInsurancePackage(@PathVariable UUID id) {
    return insurancePackageManagementService.getInsurancePackageById(id);
  }

}
