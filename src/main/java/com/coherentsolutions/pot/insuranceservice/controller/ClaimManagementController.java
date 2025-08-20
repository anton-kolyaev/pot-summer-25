package com.coherentsolutions.pot.insuranceservice.controller;

import com.coherentsolutions.pot.insuranceservice.dto.claim.ClaimApprovalRequest;
import com.coherentsolutions.pot.insuranceservice.dto.claim.ClaimDenialRequest;
import com.coherentsolutions.pot.insuranceservice.dto.claim.ClaimDto;
import com.coherentsolutions.pot.insuranceservice.dto.claim.ClaimFilter;
import com.coherentsolutions.pot.insuranceservice.service.ClaimManagementService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
@RequestMapping("/v1/claims")
public class ClaimManagementController {

  private final ClaimManagementService claimManagementService;

  @GetMapping
  @Operation(
      summary = "Get claims",
      description = """
          Retrieve claims with optional filters.
          
          **Filters**
          - `claimId`
          - `status`
          - `planName` - case-insensitive
          - `amountMin` / `amountMax` - inclusive
          - `serviceDateFrom` / `serviceDateTo` - inclusive (YYYY-MM-DD)
          - `userId`, `companyId`
          - `enrollmentId`
          """
  )
  public Page<ClaimDto> getClaims(@ParameterObject ClaimFilter filter,
      @ParameterObject Pageable pageable) {
    return claimManagementService.getClaimsWithFilters(filter, pageable);
  }

  @PostMapping
  @Operation(
      summary = "Create claim",
      description = """
          Creates a new claim.
          
          **Required body fields**
          - `consumer.userId`
          - `enrollmentId`
          - `serviceDate` (YYYY-MM-DD)
          - `amount`
          
          **Defaults**
          - `status` → `PENDING` (if omitted)
          - `claimNumber` → generated (mirrors claim id)
          """
  )
  @ResponseStatus(HttpStatus.CREATED)
  public ClaimDto createClaim(@Valid @RequestBody ClaimDto request) {
    return claimManagementService.createClaim(request);
  }

  @PostMapping("/{id}/approval")
  @Operation(
      summary = "Approve claim",
      description = """
          Approves a claim with the provided approved amount and optional notes.
          
          **Required body fields**
          - `approvedAmount`
          
          **Defaults**
          - `status` → `APPROVED`
          - `processedDate` → now
          """
  )
  public ClaimDto approveClaim(
      @PathVariable UUID id,
      @Valid @RequestBody ClaimApprovalRequest request) {
    return claimManagementService.approveClaim(id, request);
  }

  @PostMapping("/{id}/denial")
  @Operation(
      summary = "Deny claim",
      description = """
          Denies a claim with a required reason and optional notes.
          
          **Required body fields**
          - `reason`
          
          **Defaults**
          - `status` → `DENIED`
          - `processedDate` → now
          """
  )
  public ClaimDto denyClaim(
      @PathVariable UUID id,
      @Valid @RequestBody ClaimDenialRequest request) {
    return claimManagementService.denyClaim(id, request);
  }
}
