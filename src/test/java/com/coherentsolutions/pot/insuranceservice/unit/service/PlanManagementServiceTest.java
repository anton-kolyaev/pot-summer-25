package com.coherentsolutions.pot.insuranceservice.unit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.coherentsolutions.pot.insuranceservice.dto.plan.PlanDto;
import com.coherentsolutions.pot.insuranceservice.dto.plan.PlanFilter;
import com.coherentsolutions.pot.insuranceservice.dto.plan.PlanTypeDto;
import com.coherentsolutions.pot.insuranceservice.mapper.PlanMapper;
import com.coherentsolutions.pot.insuranceservice.model.Plan;
import com.coherentsolutions.pot.insuranceservice.model.PlanType;
import com.coherentsolutions.pot.insuranceservice.repository.PlanRepository;
import com.coherentsolutions.pot.insuranceservice.repository.PlanTypeRepository;
import com.coherentsolutions.pot.insuranceservice.service.PlanManagementService;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;
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
    verify(planMapper, never()).toDto(any(Plan.class));
  }

  @Test
  @DisplayName("Should return all plans when no filter is applied")
  void shouldReturnAllPlansWithoutFilter() {
    List<Plan> plans = List.of(plan);
    List<PlanDto> expectedDtos = List.of(planDto);

    when(planRepository.findAll(ArgumentMatchers.<Specification<Plan>>any())).thenReturn(plans);
    when(planMapper.toDto(plan)).thenReturn(planDto);

    List<PlanDto> result = planManagementService.getPlansWithFilter(new PlanFilter());

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(planDto, result.get(0));

    verify(planRepository).findAll(ArgumentMatchers.<Specification<Plan>>any());
    verify(planMapper).toDto(plan);
  }

  @Test
  @DisplayName("Should return filtered plans by type ID")
  void shouldReturnFilteredPlansByType() {
    PlanFilter filter = new PlanFilter();
    filter.setTypeId(3);

    List<Plan> filteredPlans = List.of(plan);
    List<PlanDto> expectedDtos = List.of(planDto);

    when(planRepository.findAll(ArgumentMatchers.<Specification<Plan>>any())).thenReturn(
        filteredPlans);
    when(planMapper.toDto(plan)).thenReturn(planDto);

    List<PlanDto> result = planManagementService.getPlansWithFilter(filter);

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(3, result.get(0).getType());

    verify(planRepository).findAll(ArgumentMatchers.<Specification<Plan>>any());
    verify(planMapper).toDto(plan);
  }

  @Test
  @DisplayName("Should return all plan types")
  void shouldReturnAllPlanTypes() {
    PlanType dental = buildPlanType(1, "DENTAL");
    dental.setName("Dental Plan");

    PlanType medical = buildPlanType(2, "MEDICAL");
    medical.setName("Medical Plan");

    PlanTypeDto dentalDto = new PlanTypeDto(1, "DENTAL", "Dental Plan");
    PlanTypeDto medicalDto = new PlanTypeDto(2, "MEDICAL", "Medical Plan");

    when(planTypeRepository.findAll()).thenReturn(List.of(dental, medical));
    when(planMapper.toDto(dental)).thenReturn(dentalDto);
    when(planMapper.toDto(medical)).thenReturn(medicalDto);

    List<PlanTypeDto> result = planManagementService.getAllPlanTypes();

    assertNotNull(result);
    assertEquals(2, result.size());
    assertEquals("DENTAL", result.get(0).getCode());
    assertEquals("MEDICAL", result.get(1).getCode());

    verify(planTypeRepository).findAll();
    verify(planMapper).toDto(dental);
    verify(planMapper).toDto(medical);
  }

  @Test
  @DisplayName("Should return empty list when no plan types exist")
  void shouldReturnEmptyListWhenNoPlanTypes() {
    when(planTypeRepository.findAll()).thenReturn(List.of());

    List<PlanTypeDto> result = planManagementService.getAllPlanTypes();

    assertNotNull(result);
    assertEquals(0, result.size());

    verify(planTypeRepository).findAll();
    verifyNoInteractions(planMapper);
  }

  private PlanType buildPlanType(int id, String code) {
    PlanType planType = new PlanType();
    planType.setId(id);
    planType.setCode(code);
    return planType;
  }
}
