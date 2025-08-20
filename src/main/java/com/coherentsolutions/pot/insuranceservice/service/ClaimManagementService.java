package com.coherentsolutions.pot.insuranceservice.service;

import com.coherentsolutions.pot.insuranceservice.dto.claim.ClaimApprovalRequest;
import com.coherentsolutions.pot.insuranceservice.dto.claim.ClaimDenialRequest;
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
import io.swagger.v3.oas.annotations.Operation;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
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
        .orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Enrollment not found"));

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
    entity.setApprovedAmount(null);
    entity.setProcessedDate(null);
    entity.setDeniedReason(null);
    entity.setNotes(null);

    Claim saved = claimRepository.save(entity);
    return claimMapper.toDto(saved);
  }

  @Transactional
  public ClaimDto approveClaim(UUID claimId, ClaimApprovalRequest request) {
    Claim claim = claimRepository.findById(claimId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Claim not found"));

    if (claim.getStatus() != ClaimStatus.PENDING) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          "Only PENDING claims can be approved");
    }
    if (request.getApprovedAmount() == null || request.getApprovedAmount().signum() <= 0) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Approved amount must be positive");
    }
    if (claim.getAmount() != null && request.getApprovedAmount().compareTo(claim.getAmount()) > 0) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          "Approved amount cannot exceed claim amount");
    }

    claim.setStatus(ClaimStatus.APPROVED);
    claim.setApprovedAmount(request.getApprovedAmount());
    claim.setDeniedReason(null);
    claim.setNotes(request.getNotes());
    claim.setProcessedDate(LocalDateTime.now());
    Claim saved = claimRepository.save(claim);
    System.out.println(">>> processedDate after save = " + saved.getProcessedDate());
    return claimMapper.toDto(saved);
  }

  @Transactional
  public ClaimDto denyClaim(UUID claimId, ClaimDenialRequest request) {
    Claim claim = claimRepository.findById(claimId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Claim not found"));

    if (claim.getStatus() != ClaimStatus.PENDING) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          "Only PENDING claims can be denied");
    }
    if (request.getReason() == null || request.getReason().isBlank()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "reason is required");
    }

    claim.setStatus(ClaimStatus.DENIED);
    claim.setDeniedReason(request.getReason().trim());
    claim.setApprovedAmount(null);
    claim.setProcessedDate(LocalDateTime.now());
    claim.setNotes(request.getNotes());

    Claim saved = claimRepository.save(claim);
    return claimMapper.toDto(saved);
  }
  @GetMapping("/{id}")
  @Operation(
      summary = "Get claim by id",
      description = """
        Retrieve details of a specific claim by its unique identifier.
        """
  )
  @Transactional(readOnly = true)
  public ClaimDto getClaimById(UUID id) {
    Claim claim = claimRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Claim not found"));
    return claimMapper.toDto(claim);
  }

}
