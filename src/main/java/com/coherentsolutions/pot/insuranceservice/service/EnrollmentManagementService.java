package com.coherentsolutions.pot.insuranceservice.service;

import com.coherentsolutions.pot.insuranceservice.dto.enrollment.EnrollmentDto;
import com.coherentsolutions.pot.insuranceservice.mapper.EnrollmentMapper;
import com.coherentsolutions.pot.insuranceservice.repository.EnrollmentRepository;
import com.coherentsolutions.pot.insuranceservice.repository.PlanRepository;
import com.coherentsolutions.pot.insuranceservice.repository.UserRepository;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class EnrollmentManagementService {

  private final EnrollmentRepository enrollmentRepository;
  private final UserRepository userRepository;
  private final PlanRepository planRepository;
  private final EnrollmentMapper enrollmentMapper;

  @Transactional
  public EnrollmentDto createEnrollment(EnrollmentDto dto) {
    var user = userRepository.findByIdOrThrow(dto.getUserId());
    var plan = planRepository.findByIdOrThrow(dto.getPlanId());

    if (enrollmentRepository
        .existsByUserIdAndPlanIdAndDeletedAtIsNull(dto.getUserId(), dto.getPlanId())) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          "Active enrollment already exists for this user and plan");
    }

    var entity = enrollmentMapper.toEntity(dto);
    entity.setUser(user);
    entity.setPlan(plan);
    entity.setPlanContribution(plan.getContribution());

    var saved = enrollmentRepository.save(entity);
    return enrollmentMapper.toDto(saved);
  }

  @Transactional(readOnly = true)
  public List<EnrollmentDto> getAll(Authentication authentication, UUID companyId, UUID userId) {

    // For app_admin returns all not deleted enrollments
    if (authentication.getAuthorities().stream()
        .anyMatch(a -> a.getAuthority().equals("ROLE_APPLICATION_ADMIN"))) {
      return enrollmentRepository.findAllByDeletedAtIsNull()
          .stream()
          .map(enrollmentMapper::toDto)
          .toList();
    }

    // For company_enrollment_manager returns only related to his company enrollments
    if (authentication.getAuthorities().stream()
        .anyMatch(a -> a.getAuthority().equals("ROLE_FUNC_COMPANY_ENROLLMENT_MANAGER"))) {
      return enrollmentRepository.findByUserCompanyIdAndUserId(companyId, userId)
          .stream()
          .map(enrollmentMapper::toDto)
          .toList();
    }

    // For consumer returns all consumer's enrollments
    if (authentication.getAuthorities().stream()
        .anyMatch(a -> a.getAuthority().equals("ROLE_FUNC_CONSUMER"))) {
      return enrollmentRepository.findByUserId(userId)
          .stream()
          .map(enrollmentMapper::toDto)
          .toList();
    }

    return Collections.emptyList();

  }
}
