package com.coherentsolutions.pot.insuranceservice.unit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doCallRealMethod;
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
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.hibernate.Filter;
import org.hibernate.Session;
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

  @Mock
  private EntityManager entityManager;

  @Mock
  private Session session;

  @Mock
  private Filter filter;

  @InjectMocks
  private PlanManagementService planManagementService;

  private PlanDto planDto;
  private PlanType planType;
  private Plan plan;

  @BeforeEach
  void setUp() {
    planDto = buildPlanDto("Vision Plan", 3, new BigDecimal("123.45"));
    planType = buildPlanType(3, "VISION");
    plan = buildPlan(UUID.randomUUID(), "Vision Plan", planType, new BigDecimal("123.45"));
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
    verify(planMapper, never()).toDto(any(Plan.class));
  }

  @Test
  @DisplayName("Should update plan successfully when type remains the same")
  void shouldUpdatePlanSuccessfully() {
    UUID planId = UUID.randomUUID();

    PlanType sameType = buildPlanType(3, "VISION");
    Plan existingPlan = buildPlan(planId, "Vision Plan", sameType, new BigDecimal("123.45"));
    PlanDto updatedDto = buildPlanDto("Updated Vision Plan", 3, new BigDecimal("456.78"));

    when(planRepository.findByIdOrThrow(planId)).thenReturn(existingPlan);
    when(planRepository.save(existingPlan)).thenReturn(existingPlan);
    when(planMapper.toDto(existingPlan)).thenReturn(updatedDto);

    PlanDto result = planManagementService.updatePlan(planId, updatedDto);

    assertNotNull(result);
    assertEquals("Updated Vision Plan", result.getName());
    assertEquals(new BigDecimal("456.78"), result.getContribution());
    assertEquals(3, result.getType());

    verify(planRepository).findByIdOrThrow(planId);
    verify(planRepository).save(existingPlan);
    verify(planMapper).toDto(existingPlan);
  }

  @Test
  @DisplayName("Should throw NOT_FOUND when plan is not found")
  void shouldThrowWhenPlanNotFoundForUpdate() {
    UUID planId = UUID.randomUUID();

    when(planRepository.findByIdOrThrow(planId))
        .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Plan not found"));

    ResponseStatusException exception = assertThrows(ResponseStatusException.class,
        () -> planManagementService.updatePlan(planId, planDto));

    assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    assertEquals("Plan not found", exception.getReason());

    verify(planRepository).findByIdOrThrow(planId);
    verify(planRepository, never()).save(any());
  }

  @Test
  @DisplayName("Should throw BAD_REQUEST when updating with invalid plan type")
  void shouldThrowWhenInvalidTypeDuringUpdate() {
    UUID planId = UUID.randomUUID();

    PlanType existingType = buildPlanType(1, "VISION");
    Plan existingPlan = buildPlan(planId, "Vision Plan", existingType, new BigDecimal("123.45"));

    PlanDto updatedDto = buildPlanDto("Updated Plan", 3, new BigDecimal("999.99")); // new type

    when(planRepository.findByIdOrThrow(planId)).thenReturn(existingPlan);

    ResponseStatusException exception = assertThrows(ResponseStatusException.class,
        () -> planManagementService.updatePlan(planId, updatedDto));

    assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    assertEquals("Changing plan type is not allowed", exception.getReason());

    verify(planRepository).findByIdOrThrow(planId);
    verify(planTypeRepository, never()).findById(any());
    verify(planRepository, never()).save(any());
  }

  @Test
  @DisplayName("Should return all plans when no filter is applied")
  void shouldReturnAllPlansWithoutFilter() {

    List<Plan> plans = List.of(plan);

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

    PlanFilter filterDto = new PlanFilter();
    filterDto.setTypeId(3);

    List<Plan> filteredPlans = List.of(plan);

    when(planRepository.findAll(ArgumentMatchers.<Specification<Plan>>any())).thenReturn(filteredPlans);
    when(planMapper.toDto(plan)).thenReturn(planDto);

    List<PlanDto> result = planManagementService.getPlansWithFilter(filterDto);

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


  @Test
  @DisplayName("Should throw BAD_REQUEST when trying to change the plan type")
  void shouldThrowWhenPlanTypeIsChanged() {
    UUID planId = UUID.randomUUID();

    PlanType originalType = buildPlanType(3, "VISION");
    Plan existingPlan = buildPlan(planId, "Vision Plan", originalType, new BigDecimal("123.45"));

    PlanDto updatedDto = buildPlanDto("New Plan Name", 2, new BigDecimal("456.78"));

    when(planRepository.findByIdOrThrow(planId)).thenReturn(existingPlan);

    ResponseStatusException exception = assertThrows(ResponseStatusException.class,
        () -> planManagementService.updatePlan(planId, updatedDto));

    assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    assertEquals("Changing plan type is not allowed", exception.getReason());

    verify(planRepository).findByIdOrThrow(planId);
    verify(planTypeRepository, never()).findById(any());
    verify(planRepository, never()).save(any());
  }

  @Test
  @DisplayName("Should soft delete plan successfully")
  void shouldSoftDeletePlanSuccessfully() {
    UUID planId = UUID.randomUUID();
    Plan existingPlan = buildPlan(planId, "Test Plan", planType, new BigDecimal("123.45"));

    when(planRepository.findByIdOrThrow(planId)).thenReturn(existingPlan);

    planManagementService.softDeletePlan(planId);

    verify(planRepository).findByIdOrThrow(planId);
    verify(planRepository).delete(existingPlan);
  }

  @Test
  @DisplayName("Should throw NOT_FOUND when soft deleting non-existent plan")
  void shouldThrowNotFoundWhenSoftDeletingNonExistentPlan() {
    UUID planId = UUID.randomUUID();

    when(planRepository.findByIdOrThrow(planId))
        .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Plan not found"));

    ResponseStatusException exception = assertThrows(ResponseStatusException.class,
        () -> planManagementService.softDeletePlan(planId));

    assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    assertEquals("Plan not found", exception.getReason());

    verify(planRepository).findByIdOrThrow(planId);
    verify(planRepository, never()).save(any());
  }

  private PlanDto buildPlanDto(String name, int typeId, BigDecimal contribution) {
    return PlanDto.builder()
        .name(name)
        .type(typeId)
        .contribution(contribution)
        .build();
  }

  private Plan buildPlan(UUID id, String name, PlanType type, BigDecimal contribution) {
    Plan plan = new Plan();
    plan.setId(id);
    plan.setName(name);
    plan.setType(type);
    plan.setContribution(contribution);
    return plan;
  }

  private PlanType buildPlanType(int id, String code) {
    PlanType planType = new PlanType();
    planType.setId(id);
    planType.setCode(code);
    return planType;
  }
}
