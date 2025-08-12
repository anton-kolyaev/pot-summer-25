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
import org.springframework.security.access.prepost.PreAuthorize;
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
@RequestMapping("/v1/companies/{companyId}/plans")
public class AdminPlanManagementController {

  private final PlanManagementService planManagementService;

  @PreAuthorize("@companyAdminSecurityService.canAccessCompanyPlans(#companyId)")
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public PlanDto createPlan(@PathVariable UUID companyId, @Valid @RequestBody PlanDto planDto) {
    return planManagementService.createPlan(planDto);
  }

  @PreAuthorize("@companyAdminSecurityService.canAccessCompanyPlans(#companyId)")
  @PutMapping("/{id}")
  public PlanDto updatePlan(@PathVariable UUID companyId, @PathVariable UUID id, @Valid @RequestBody PlanDto planDto) {
    return planManagementService.updatePlan(id, planDto);
  }

  @PreAuthorize("@companyAdminSecurityService.canAccessCompanyPlans(#companyId)")
  @GetMapping
  public List<PlanDto> getPlans(@PathVariable UUID companyId, PlanFilter filter) {
    return planManagementService.getPlansWithFilter(filter);
  }

  @PreAuthorize("@companyAdminSecurityService.canAccessCompanyPlans(#companyId)")
  @GetMapping("/plan-types")
  public List<PlanTypeDto> getPlanTypes(@PathVariable UUID companyId) {
    return planManagementService.getAllPlanTypes();
  }

  @PreAuthorize("@companyAdminSecurityService.canAccessCompanyPlans(#companyId)")
  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void softDeletePlan(@PathVariable UUID companyId, @PathVariable UUID id) {
    planManagementService.softDeletePlan(id);
  }

}
