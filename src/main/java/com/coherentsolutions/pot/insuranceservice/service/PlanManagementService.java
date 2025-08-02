package com.coherentsolutions.pot.insuranceservice.service;

import com.coherentsolutions.pot.insuranceservice.dto.plan.PlanDto;
import com.coherentsolutions.pot.insuranceservice.dto.plan.PlanFilter;
import com.coherentsolutions.pot.insuranceservice.dto.plan.PlanTypeDto;
import com.coherentsolutions.pot.insuranceservice.mapper.PlanMapper;
import com.coherentsolutions.pot.insuranceservice.model.Plan;
import com.coherentsolutions.pot.insuranceservice.model.PlanType;
import com.coherentsolutions.pot.insuranceservice.repository.PlanRepository;
import com.coherentsolutions.pot.insuranceservice.repository.PlanSpecification;
import com.coherentsolutions.pot.insuranceservice.repository.PlanTypeRepository;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class PlanManagementService {

  private final PlanRepository planRepository;
  private final PlanMapper planMapper;
  private final PlanTypeRepository planTypeRepository;

  @Transactional
  public PlanDto createPlan(PlanDto planDto) {
    Plan plan = planMapper.toEntity(planDto);

    PlanType planType = planTypeRepository.findByIdOrThrow(planDto.getType());

    plan.setType(planType);

    Plan saved = planRepository.save(plan);
    return planMapper.toDto(saved);
  }

  @Transactional
  public PlanDto updatePlan(UUID id, PlanDto planDto) {
    Plan existing = planRepository.findByIdOrThrow(id);

    validateOnUpdate(existing, planDto);

    existing.setName(planDto.getName());
    existing.setContribution(planDto.getContribution());

    Plan updated = planRepository.save(existing);
    return planMapper.toDto(updated);
  }

  private void validateOnUpdate(Plan existing, PlanDto planDto) {
    if (!existing.getType().getId().equals(planDto.getType())) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          "Changing plan type is not allowed");
    }
  }


  @Transactional(readOnly = true)
  public List<PlanDto> getPlansWithFilter(PlanFilter filter) {
    List<Plan> plans = planRepository.findAll(PlanSpecification.withFilter(filter));
    return plans.stream().map(planMapper::toDto).toList();
  }

  @Transactional(readOnly = true)
  public List<PlanTypeDto> getAllPlanTypes() {
    return planTypeRepository.findAll()
        .stream()
        .map(planMapper::toDto)
        .toList();
  }

  @Transactional
  public void softDeletePlan(UUID id) {
    Plan plan = planRepository.findByIdOrThrow(id);
    planRepository.delete(plan);
  }
}
