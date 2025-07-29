package com.coherentsolutions.pot.insuranceservice.unit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doCallRealMethod;
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
    doCallRealMethod().when(planTypeRepository).findByIdOrThrow(3);
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
    doCallRealMethod().when(planTypeRepository).findByIdOrThrow(3);
    when(planTypeRepository.findById(3)).thenReturn(Optional.empty());

    ResponseStatusException exception = assertThrows(ResponseStatusException.class,
        () -> planManagementService.createPlan(planDto));

    assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    assertEquals("Invalid plan type", exception.getReason());

    verify(planTypeRepository).findById(3);
    verify(planRepository, never()).save(any());
    verify(planMapper, never()).toDto(any());
  }

  @Test
  @DisplayName("Should update plan successfully")
  void shouldUpdatePlanSuccessfully() {
    UUID planId = UUID.randomUUID();

    doCallRealMethod().when(planTypeRepository).findByIdOrThrow(3);
    when(planTypeRepository.findById(3)).thenReturn(Optional.of(planType));
    when(planRepository.findById(planId)).thenReturn(Optional.of(plan));
    when(planRepository.save(plan)).thenReturn(plan);
    when(planMapper.toDto(plan)).thenReturn(planDto);

    PlanDto result = planManagementService.updatePlan(planId, planDto);

    assertNotNull(result);
    assertEquals(planDto.getName(), result.getName());
    assertEquals(planDto.getType(), result.getType());
    assertEquals(planDto.getContribution(), result.getContribution());

    verify(planRepository).findById(planId);
    verify(planTypeRepository).findById(3);
    verify(planRepository).save(plan);
    verify(planMapper).toDto(plan);
  }

  @Test
  @DisplayName("Should throw NOT_FOUND when plan is not found")
  void shouldThrowWhenPlanNotFoundForUpdate() {
    UUID planId = UUID.randomUUID();

    when(planRepository.findById(planId)).thenReturn(Optional.empty());

    ResponseStatusException exception = assertThrows(ResponseStatusException.class,
        () -> planManagementService.updatePlan(planId, planDto));

    assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    assertEquals("Plan not found", exception.getReason());

    verify(planRepository).findById(planId);
    verify(planRepository, never()).save(any());
  }

  @Test
  @DisplayName("Should throw BAD_REQUEST when updating with invalid plan type")
  void shouldThrowWhenInvalidTypeDuringUpdate() {
    UUID planId = UUID.randomUUID();

    when(planRepository.findById(planId)).thenReturn(Optional.of(plan));
    doCallRealMethod().when(planTypeRepository).findByIdOrThrow(3);
    when(planTypeRepository.findById(3)).thenReturn(Optional.empty());

    ResponseStatusException exception = assertThrows(ResponseStatusException.class,
        () -> planManagementService.updatePlan(planId, planDto));

    assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    assertEquals("Invalid plan type", exception.getReason());

    verify(planRepository).findById(planId);
    verify(planTypeRepository).findById(3);
    verify(planRepository, never()).save(any());
  }
}
