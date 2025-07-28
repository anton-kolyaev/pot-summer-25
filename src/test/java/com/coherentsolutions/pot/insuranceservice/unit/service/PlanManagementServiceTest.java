package com.coherentsolutions.pot.insuranceservice.unit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.coherentsolutions.pot.insuranceservice.dto.plan.PlanDto;
import com.coherentsolutions.pot.insuranceservice.mapper.PlanMapper;
import com.coherentsolutions.pot.insuranceservice.model.Plan;
import com.coherentsolutions.pot.insuranceservice.model.PlanType;
import com.coherentsolutions.pot.insuranceservice.repository.PlanRepository;
import com.coherentsolutions.pot.insuranceservice.repository.PlanTypeRepository;
import com.coherentsolutions.pot.insuranceservice.service.PlanManagementService;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
@DisplayName("Plan Management Service Tests")
public class PlanManagementServiceTest {

  @Mock
  private PlanRepository planRepository;

  @Mock
  private PlanTypeRepository planTypeRepository;

  @Mock
  private PlanMapper planMapper;

  @InjectMocks
  private PlanManagementService planManagementService;

  private PlanDto planDto;
  private PlanType planType;
  private Plan plan;

  @BeforeEach
  void setUp() {
    planDto = PlanDto.builder()
        .name("Vision Plan")
        .type(3)
        .contribution(new BigDecimal("123.45"))
        .build();

    planType = new PlanType();
    planType.setId(3);
    planType.setCode("VISION");

    plan = new Plan();
    plan.setId(UUID.randomUUID());
    plan.setName("Vision Plan");
    plan.setType(planType);
    plan.setContribution(new BigDecimal("123.45"));
  }

  @Test
  @DisplayName("Should create a plan when plan type exists")
  void shouldCreatePlanSuccessfully() {
    when(planTypeRepository.findById(3)).thenReturn(Optional.of(planType));
    when(planMapper.toEntity(planDto)).thenReturn(plan);
    when(planRepository.save(Mockito.any(Plan.class))).thenReturn(plan);
    when(planMapper.toDto(Mockito.any(Plan.class))).thenReturn(planDto);

    PlanDto result = planManagementService.createPlan(planDto);

    assertNotNull(result);
    assertEquals(3, result.getType());
    assertEquals(new BigDecimal("123.45"), result.getContribution());

    verify(planTypeRepository).findById(3);
    verify(planRepository).save(Mockito.any(Plan.class));
    verify(planMapper).toDto(Mockito.any(Plan.class));
  }

  @Test
  @DisplayName("Should throw BAD_REQUEST when plan type does not exist")
  void shouldThrowWhenPlanTypeNotFound() {
    when(planTypeRepository.findById(3)).thenReturn(Optional.empty());

    ResponseStatusException exception = assertThrows(ResponseStatusException.class,
        () -> planManagementService.createPlan(planDto));

    assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    assertEquals("Invalid plan type", exception.getReason());

    verify(planTypeRepository).findById(3);
    verify(planRepository, never()).save(any());
    verify(planMapper, never()).toDto(any());
  }
}
