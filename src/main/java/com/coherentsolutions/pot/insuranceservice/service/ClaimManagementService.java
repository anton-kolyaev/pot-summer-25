package com.coherentsolutions.pot.insuranceservice.service;

import com.coherentsolutions.pot.insuranceservice.dto.claim.ClaimDto;
import com.coherentsolutions.pot.insuranceservice.dto.claim.ClaimFilter;
import com.coherentsolutions.pot.insuranceservice.enums.ClaimStatus;
import com.coherentsolutions.pot.insuranceservice.mapper.ClaimMapper;
import com.coherentsolutions.pot.insuranceservice.model.Claim;
import com.coherentsolutions.pot.insuranceservice.model.Enrollment;
import com.coherentsolutions.pot.insuranceservice.model.User;
import com.coherentsolutions.pot.insuranceservice.repository.ClaimRepository;
import com.coherentsolutions.pot.insuranceservice.repository.ClaimSpecification;
import com.coherentsolutions.pot.insuranceservice.repository.EnrollmentRepository;
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
  private final EnrollmentRepository enrollmentRepository;
  private final UserRepository userRepository;
  private final ClaimMapper claimMapper;

  @Transactional(readOnly = true)
  public Page<ClaimDto> getClaimsWithFilters(ClaimFilter filter, Pageable pageable) {
    Page<Claim> page = claimRepository.findAll(ClaimSpecification.withFilters(filter), pageable);
    return page.map(claimMapper::toDto);
  }

  @Transactional
  public ClaimDto createClaim(ClaimDto request) {
    UUID enrollmentId = request.getEnrollmentId();
    if (enrollmentId == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "enrollmentId is required");
    }
    Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Enrollment not found"));
    
    if (request.getConsumer() == null || request.getConsumer().getUserId() == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "consumer.userId is required");
    }
    UUID userId = request.getConsumer().getUserId();
    User consumer = userRepository.findByIdOrThrow(userId);
    
    if (!consumer.getId().equals(enrollment.getUser().getId())) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "consumer.userId does not match enrollment.user");
    }
    Claim entity = claimMapper.toEntity(request);
    entity.setEnrollment(enrollment);
    entity.setConsumer(consumer);
    entity.setStatus(ClaimStatus.PENDING);

    Claim saved = claimRepository.save(entity);
    return claimMapper.toDto(saved);
  }
}
