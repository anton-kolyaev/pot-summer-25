package com.coherentsolutions.pot.insuranceservice.service;

import com.coherentsolutions.pot.insuranceservice.dto.plan.PlanDto;
import com.coherentsolutions.pot.insuranceservice.mapper.PlanMapper;
import com.coherentsolutions.pot.insuranceservice.model.Plan;
import com.coherentsolutions.pot.insuranceservice.model.PlanType;
import com.coherentsolutions.pot.insuranceservice.repository.PlanRepository;
import com.coherentsolutions.pot.insuranceservice.repository.PlanTypeRepository;
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
    Plan existing = planRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Plan not found"));

    PlanType planType = planTypeRepository.findByIdOrThrow(planDto.getType());

    existing.setName(planDto.getName());
    existing.setContribution(planDto.getContribution());
    existing.setType(planType);

    Plan updated = planRepository.save(existing);
    return planMapper.toDto(updated);
  }

}
