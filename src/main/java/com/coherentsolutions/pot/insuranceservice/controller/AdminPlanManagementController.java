package com.coherentsolutions.pot.insuranceservice.controller;


import com.coherentsolutions.pot.insuranceservice.dto.plan.PlanDto;
import com.coherentsolutions.pot.insuranceservice.dto.plan.PlanFilter;
import com.coherentsolutions.pot.insuranceservice.dto.plan.PlanTypeDto;
import com.coherentsolutions.pot.insuranceservice.service.PlanManagementService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/plans")
public class AdminPlanManagementController {

  private final PlanManagementService planManagementService;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public PlanDto createPlan(@Valid @RequestBody PlanDto planDto) {
    return planManagementService.createPlan(planDto);
  }

  @PutMapping("/{id}")
  public PlanDto updatePlan(@PathVariable UUID id, @Valid @RequestBody PlanDto planDto) {
    return planManagementService.updatePlan(id, planDto);
  }

  @GetMapping
  public List<PlanDto> getPlans(PlanFilter filter) {
    return planManagementService.getPlansWithFilter(filter);
  }

  @GetMapping("/plan-types")
  public List<PlanTypeDto> getPlanTypes() {
    return planManagementService.getAllPlanTypes();
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deletePlan(@PathVariable UUID id) {
    planManagementService.softDeletePlan(id);
  }

}
