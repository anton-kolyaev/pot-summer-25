package com.coherentsolutions.pot.insuranceservice.service;

import com.coherentsolutions.pot.insuranceservice.dto.claim.ClaimDto;
import com.coherentsolutions.pot.insuranceservice.dto.claim.ClaimFilter;
import com.coherentsolutions.pot.insuranceservice.mapper.ClaimMapper;
import com.coherentsolutions.pot.insuranceservice.model.Claim;
import com.coherentsolutions.pot.insuranceservice.repository.ClaimRepository;
import com.coherentsolutions.pot.insuranceservice.repository.ClaimSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ClaimManagementService {

  private final ClaimRepository claimRepository;
  private final ClaimMapper claimMapper;

  @Transactional(readOnly = true)
  public Page<ClaimDto> getClaimsWithFilters(ClaimFilter filter, Pageable pageable) {
    Page<Claim> page = claimRepository.findAll(ClaimSpecification.withFilters(filter), pageable);
    return page.map(claimMapper::toDto);
  }
}
