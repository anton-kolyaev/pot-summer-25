package com.coherentsolutions.pot.insuranceservice.controller;


import com.coherentsolutions.pot.insuranceservice.dto.plan.PlanDto;
import com.coherentsolutions.pot.insuranceservice.service.PlanManagementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/plans")
public class PlanManagementController {

  private final PlanManagementService planManagementService;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public PlanDto createPlan(@Valid @RequestBody PlanDto planDto) {
    return planManagementService.createPlan(planDto);
  }

}
