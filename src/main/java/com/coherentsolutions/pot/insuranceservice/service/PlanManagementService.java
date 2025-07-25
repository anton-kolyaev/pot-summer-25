package com.coherentsolutions.pot.insuranceservice.service;

import com.coherentsolutions.pot.insuranceservice.dto.plan.PlanDto;
import com.coherentsolutions.pot.insuranceservice.mapper.PlanMapper;
import com.coherentsolutions.pot.insuranceservice.model.Plan;
import com.coherentsolutions.pot.insuranceservice.model.PlanType;
import com.coherentsolutions.pot.insuranceservice.repository.PlanRepository;
import com.coherentsolutions.pot.insuranceservice.repository.PlanTypeRepository;
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
    Plan plan = new Plan();

    plan.setName(planDto.getName());
    plan.setContribution(planDto.getContribution());

    PlanType planType = planTypeRepository.findByCode(planDto.getType())
        .orElseThrow(
            () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid plan type"));

    plan.setType(planType);

    Plan saved = planRepository.save(plan);
    return planMapper.toDto(saved);
  }
}
