package com.coherentsolutions.pot.insuranceservice.service;

import com.coherentsolutions.pot.insuranceservice.dto.enrollment.EnrollmentDto;
import com.coherentsolutions.pot.insuranceservice.mapper.EnrollmentMapper;
import com.coherentsolutions.pot.insuranceservice.repository.EnrollmentRepository;
import com.coherentsolutions.pot.insuranceservice.repository.PlanRepository;
import com.coherentsolutions.pot.insuranceservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
}
