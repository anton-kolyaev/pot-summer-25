package com.coherentsolutions.pot.insuranceservice.controller;

import com.coherentsolutions.pot.insuranceservice.dto.claim.ClaimDto;
import com.coherentsolutions.pot.insuranceservice.dto.claim.ClaimFilter;
import com.coherentsolutions.pot.insuranceservice.service.ClaimManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/claims")
public class ClaimManagementController {

  private final ClaimManagementService claimManagementService;

  @GetMapping
  public Page<ClaimDto> getClaims(ClaimFilter filter, Pageable pageable) {
    return claimManagementService.getClaimsWithFilters(filter, pageable);
  }
}
