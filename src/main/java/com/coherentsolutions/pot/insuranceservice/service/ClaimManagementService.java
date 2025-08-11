package com.coherentsolutions.pot.insuranceservice.service;

import com.coherentsolutions.pot.insuranceservice.dto.claim.ClaimDto;
import com.coherentsolutions.pot.insuranceservice.dto.claim.ClaimFilter;
import com.coherentsolutions.pot.insuranceservice.enums.ClaimStatus;
import com.coherentsolutions.pot.insuranceservice.mapper.ClaimMapper;
import com.coherentsolutions.pot.insuranceservice.model.Claim;
import com.coherentsolutions.pot.insuranceservice.model.Plan;
import com.coherentsolutions.pot.insuranceservice.model.User;
import com.coherentsolutions.pot.insuranceservice.repository.ClaimRepository;
import com.coherentsolutions.pot.insuranceservice.repository.ClaimSpecification;
import com.coherentsolutions.pot.insuranceservice.repository.PlanRepository;
import com.coherentsolutions.pot.insuranceservice.repository.UserRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class ClaimManagementService {

  private final ClaimRepository claimRepository;
  private final PlanRepository planRepository;
  private final UserRepository userRepository;
  private final ClaimMapper claimMapper;

  @Transactional(readOnly = true)
  public Page<ClaimDto> getClaimsWithFilters(ClaimFilter filter, Pageable pageable) {
    Page<Claim> page = claimRepository.findAll(ClaimSpecification.withFilters(filter), pageable);
    return page.map(claimMapper::toDto);
  }

  //TODO: Do not allow creating claims for soft deleted Plans
  @Transactional
  public ClaimDto createClaim(ClaimDto request) {
    UUID userId = request.getConsumer() != null ? request.getConsumer().getUserId() : null;
    if (userId == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "consumer.userId is required");
    }
    User consumer = userRepository.findByIdOrThrow(userId);
    Plan plan = planRepository.findById(request.getPlanId())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Plan not found"));
    Claim entity = claimMapper.toEntity(request);
    entity.setConsumer(consumer);
    entity.setPlan(plan);
    entity.setStatus(ClaimStatus.PENDING);
    Claim saved = claimRepository.save(entity);
    return claimMapper.toDto(saved);
  }
}
