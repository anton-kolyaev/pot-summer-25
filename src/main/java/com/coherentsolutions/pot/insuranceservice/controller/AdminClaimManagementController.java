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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/companies/{companyId}/claims")
public class AdminClaimManagementController {

  private final ClaimManagementService claimManagementService;

  @PreAuthorize("@companyAdminSecurityService.canAccessCompanyResource(#companyId, 'ROLE_FUNC_COMPANY_CLAIM_MANAGER')")
  @GetMapping
  @Operation(
      summary = "Get company claims",
      description = """
          Returns claims for the specified company. Access: Application Admin or Company Claim Manager of that company.
          
          Filters
          - `claimId`
          - `status`
          - `planName` (case-insensitive contains)
          - `amountMin` / `amountMax` (inclusive)
          - `serviceDateFrom` / `serviceDateTo` (YYYY-MM-DD, inclusive)
          - `userId`, `enrollmentId`
          """
  )
  public Page<ClaimDto> getClaims(@PathVariable UUID companyId,
      @ParameterObject ClaimFilter filter,
      @ParameterObject Pageable pageable) {
    filter.setCompanyId(companyId);
    return claimManagementService.getClaimsWithFilters(filter, pageable);
  }

  @PreAuthorize("@companyAdminSecurityService.canAccessCompanyResource(#companyId, 'ROLE_FUNC_COMPANY_CLAIM_MANAGER')")
  @PostMapping("/{id}/approval")
  @Operation(
      summary = "Approve company claim",
      description = """
          Approves a claim that belongs to this company. Access: Application Admin or Company Claim Manager of this company.
          
          Required body fields
          - `approvedAmount`
          
          Defaults
          - `status` → `APPROVED`
          - `processedDate` → now
          """
  )
  public ClaimDto approveClaim(@PathVariable UUID companyId,
      @PathVariable UUID id,
      @Valid @RequestBody ClaimApprovalRequest request) {
    ensureClaimInCompanyOr404(companyId, id);
    return claimManagementService.approveClaim(id, request);
  }

  @PreAuthorize("@companyAdminSecurityService.canAccessCompanyResource(#companyId, 'ROLE_FUNC_COMPANY_CLAIM_MANAGER')")
  @PostMapping("/{id}/denial")
  @Operation(
      summary = "Deny company claim",
      description = """
          Denies a claim that belongs to this company. Access: Application Admin or Company Claim Manager of this company.
          
          Required body fields
          - `reason`
          
          Defaults
          - `status` → `DENIED`
          - `processedDate` → now
          """
  )
  public ClaimDto denyClaim(@PathVariable UUID companyId,
      @PathVariable UUID id,
      @Valid @RequestBody ClaimDenialRequest request) {
    ensureClaimInCompanyOr404(companyId, id);
    return claimManagementService.denyClaim(id, request);
  }

  @PreAuthorize("@companyAdminSecurityService.canAccessCompanyResource(#companyId, 'ROLE_FUNC_COMPANY_CLAIM_MANAGER')")
  @GetMapping("/{id}")
  @Operation(
      summary = "Get company claim by id",
      description = "Returns a single claim if it belongs to this company. Access: Application Admin or Company Claim Manager of this company."
  )
  public ClaimDto getClaimById(@PathVariable UUID companyId, @PathVariable UUID id) {
    ensureClaimInCompanyOr404(companyId, id);
    return claimManagementService.getClaimById(id);
  }

  private void ensureClaimInCompanyOr404(UUID companyId, UUID claimId) {
    var ownership = claimManagementService.getClaimOwnership(claimId);
    if (ownership == null || ownership.companyId() == null || !ownership.companyId()
        .equals(companyId)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Claim not found");
    }
  }
}
