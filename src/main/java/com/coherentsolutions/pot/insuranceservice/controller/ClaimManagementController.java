package com.coherentsolutions.pot.insuranceservice.controller;

import com.coherentsolutions.pot.insuranceservice.dto.claim.ClaimDto;
import com.coherentsolutions.pot.insuranceservice.dto.claim.ClaimFilter;
import com.coherentsolutions.pot.insuranceservice.enums.ClaimStatus;
import com.coherentsolutions.pot.insuranceservice.service.ClaimManagementService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/claims")
public class ClaimManagementController {

  private final ClaimManagementService claimManagementService;

  @GetMapping
  @Operation(
      summary = "Get claims",
      description = "Filters: claimId, status, planName (contains, case-insensitive), " +
          "amountMin/amountMax (inclusive), serviceDateFrom/serviceDateTo, userId, companyId. " +
          "Supports page/size/sort."
  )
  public Page<ClaimDto> getClaims(ClaimFilter filter, Pageable pageable) {
    return claimManagementService.getClaimsWithFilters(filter, pageable);
  }

  @PostMapping
  @Operation(
      summary = "Create claim",
      description = "Creates a new claim for a consumer and plan. "
          + "Requires consumer.userId and planId, plus serviceDate and amount. "
          + "If status is not provided it defaults to PENDING. "
          + "The server generates claimNumber (mirrors the claim id). "
          + "Ignores consumer name/phone from the request and returns values from the linked user. "
          + "Returns the created claim."
  )
  @ResponseStatus(HttpStatus.CREATED)
  public ClaimDto createClaim(@Valid @RequestBody ClaimDto request) {
    return claimManagementService.createClaim(request);
  }
}
