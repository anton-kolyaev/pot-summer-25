package com.coherentsolutions.pot.insuranceservice.controller;

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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/users/{userId}/claims")
public class UserClaimManagementController {

  private final ClaimManagementService claimManagementService;

  @PreAuthorize("@claimSecurity.canListUserClaims(#userId)")
  @GetMapping
  @Operation(
      summary = "Get user's claims",
      description = """
          Returns claims for the specified user.
          
          Access
          - Consumer: only for themselves
          """
  )
  public Page<ClaimDto> getUserClaims(@PathVariable UUID userId,
      @ParameterObject Pageable pageable) {
    ClaimFilter filter = new ClaimFilter();
    filter.setUserId(userId);
    return claimManagementService.getClaimsWithFilters(filter, pageable);
  }

  @PreAuthorize("@claimSecurity.canCreateForUser(#userId, #request)")
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(
      summary = "Create claim for user",
      description = """
          Creates a new claim for the specified user.
          
          Access
          - Consumer: only for themselves
          
          Required body fields
          - `consumer.userId` (must equal path {userId})
          - `enrollmentId`
          - `serviceDate` (YYYY-MM-DD)
          - `amount`
          
          Defaults
          - `status` → `PENDING`
          - `claimNumber` → generated (mirrors claim id)
          """
  )
  public ClaimDto createUserClaim(@PathVariable UUID userId,
      @Valid @RequestBody ClaimDto request) {
    UUID bodyUserId = request.getConsumer() != null ? request.getConsumer().getUserId() : null;
    if (!userId.equals(bodyUserId)) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          "consumer.userId must match path parameter {userId}");
    }
    return claimManagementService.createClaim(request);
  }
}
