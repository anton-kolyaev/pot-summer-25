package com.coherentsolutions.pot.insuranceservice.unit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.coherentsolutions.pot.insuranceservice.dto.enrollment.EnrollmentDto;
import com.coherentsolutions.pot.insuranceservice.mapper.EnrollmentMapper;
import com.coherentsolutions.pot.insuranceservice.model.Enrollment;
import com.coherentsolutions.pot.insuranceservice.model.Plan;
import com.coherentsolutions.pot.insuranceservice.model.User;
import com.coherentsolutions.pot.insuranceservice.repository.EnrollmentRepository;
import com.coherentsolutions.pot.insuranceservice.repository.PlanRepository;
import com.coherentsolutions.pot.insuranceservice.repository.UserRepository;
import com.coherentsolutions.pot.insuranceservice.service.EnrollmentManagementService;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
@DisplayName("Enrollment Management Service Tests")
public class EnrollmentManagementServiceTest {


  @Mock
  private EnrollmentRepository enrollmentRepository;
  @Mock
  private UserRepository userRepository;
  @Mock
  private PlanRepository planRepository;
  @Mock
  private EnrollmentMapper enrollmentMapper;

  @InjectMocks
  private EnrollmentManagementService enrollmentService;

  private UUID userId;
  private UUID planId;

  private User user;
  private Plan plan;

  private EnrollmentDto requestDto;
  private Enrollment mappedEntity;
  private Enrollment savedEntity;
  private EnrollmentDto responseDto;

  @BeforeEach
  void setUp() {
    userId = UUID.randomUUID();
    planId = UUID.randomUUID();

    user = new User();
    user.setId(userId);

    plan = new Plan();
    plan.setId(planId);
    plan.setContribution(new BigDecimal("2000.00"));

    requestDto =
        EnrollmentDto.builder()
            .userId(userId)
            .planId(planId)
            .electionAmount(new BigDecimal("100.00"))
            .build();

    mappedEntity = new Enrollment();

    savedEntity = new Enrollment();
    savedEntity.setId(UUID.randomUUID());
    savedEntity.setUser(user);
    savedEntity.setPlan(plan);
    savedEntity.setElectionAmount(new BigDecimal("100.00"));
    savedEntity.setPlanContribution(new BigDecimal("2000.00"));

    responseDto =
        EnrollmentDto.builder()
            .id(savedEntity.getId())
            .userId(userId)
            .planId(planId)
            .electionAmount(new BigDecimal("100.00"))
            .planContribution(new BigDecimal("2000.00"))
            .build();
  }

  private Enrollment buildEnrollment(
      UUID id, UUID uId, UUID pId, BigDecimal electionAmount, BigDecimal contribution) {
    Enrollment e = new Enrollment();
    e.setId(id);
    User u = new User();
    u.setId(uId);
    e.setUser(u);
    Plan p = new Plan();
    p.setId(pId);
    p.setContribution(contribution);
    e.setPlan(p);
    e.setElectionAmount(electionAmount);
    e.setPlanContribution(contribution);
    return e;
  }

  private EnrollmentDto buildEnrollmentDto(
      UUID id, UUID uId, UUID pId, BigDecimal electionAmount, BigDecimal contribution) {
    return EnrollmentDto.builder()
        .id(id)
        .userId(uId)
        .planId(pId)
        .electionAmount(electionAmount)
        .planContribution(contribution)
        .build();
  }

  @Test
  @DisplayName("Should create enrollment successfully (sets planContribution from Plan)")
  void shouldCreateEnrollmentSuccessfully() {
    doCallRealMethod().when(userRepository).findByIdOrThrow(userId);
    doCallRealMethod().when(planRepository).findByIdOrThrow(planId);

    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    when(planRepository.findById(planId)).thenReturn(Optional.of(plan));
    when(enrollmentRepository.existsByUserIdAndPlanIdAndDeletedAtIsNull(userId, planId))
        .thenReturn(false);
    when(enrollmentMapper.toEntity(requestDto)).thenReturn(mappedEntity);
    when(enrollmentRepository.save(any(Enrollment.class))).thenReturn(savedEntity);
    when(enrollmentMapper.toDto(savedEntity)).thenReturn(responseDto);

    EnrollmentDto result = enrollmentService.createEnrollment(requestDto);

    assertNotNull(result);
    assertEquals(userId, result.getUserId());
    assertEquals(planId, result.getPlanId());
    assertEquals(new BigDecimal("100.00"), result.getElectionAmount());
    assertEquals(new BigDecimal("2000.00"), result.getPlanContribution());

    ArgumentCaptor<Enrollment> captor = ArgumentCaptor.forClass(Enrollment.class);
    verify(enrollmentRepository).save(captor.capture());
    Enrollment toPersist = captor.getValue();
    assertEquals(user, toPersist.getUser());
    assertEquals(plan, toPersist.getPlan());
    assertEquals(new BigDecimal("2000.00"), toPersist.getPlanContribution());
  }

  @Test
  @DisplayName("Should throw BAD_REQUEST when active enrollment already exists")
  void shouldThrowWhenDuplicateActiveEnrollment() {
    doCallRealMethod().when(userRepository).findByIdOrThrow(userId);
    doCallRealMethod().when(planRepository).findByIdOrThrow(planId);

    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    when(planRepository.findById(planId)).thenReturn(Optional.of(plan));
    when(enrollmentRepository.existsByUserIdAndPlanIdAndDeletedAtIsNull(userId, planId))
        .thenReturn(true);

    ResponseStatusException ex =
        assertThrows(ResponseStatusException.class,
            () -> enrollmentService.createEnrollment(requestDto));

    assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    assertEquals("Active enrollment already exists for this user and plan", ex.getReason());

    verify(enrollmentRepository).existsByUserIdAndPlanIdAndDeletedAtIsNull(userId, planId);
    verifyNoInteractions(enrollmentMapper);
    verify(enrollmentRepository, never()).save(any());
  }

  @Test
  @DisplayName("Should throw NOT_FOUND when user does not exist")
  void shouldThrowWhenUserNotFound() {
    doCallRealMethod().when(userRepository).findByIdOrThrow(userId);
    when(userRepository.findById(userId)).thenReturn(Optional.empty());

    ResponseStatusException ex =
        assertThrows(ResponseStatusException.class,
            () -> enrollmentService.createEnrollment(requestDto));

    assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    verify(userRepository).findById(userId);
    verifyNoInteractions(planRepository, enrollmentRepository, enrollmentMapper);
  }

  @Test
  @DisplayName("Should throw NOT_FOUND when plan does not exist")
  void shouldThrowWhenPlanNotFound() {
    doCallRealMethod().when(userRepository).findByIdOrThrow(userId);
    doCallRealMethod().when(planRepository).findByIdOrThrow(planId);

    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    when(planRepository.findById(planId)).thenReturn(Optional.empty());

    ResponseStatusException ex =
        assertThrows(ResponseStatusException.class,
            () -> enrollmentService.createEnrollment(requestDto));

    assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    verify(planRepository).findById(planId);
    verify(enrollmentRepository, never()).existsByUserIdAndPlanIdAndDeletedAtIsNull(any(), any());
    verifyNoInteractions(enrollmentMapper);
  }

  @Test
  @DisplayName("Should return mapped DTOs for active enrollments")
  void listAll_shouldReturnDtos() {
    UUID e1Id = UUID.randomUUID();
    UUID e2Id = UUID.randomUUID();

    Enrollment e1 =
        buildEnrollment(
            e1Id, userId, planId, new BigDecimal("100.00"), new BigDecimal("2000.00"));
    Enrollment e2 =
        buildEnrollment(
            e2Id, userId, planId, new BigDecimal("50.00"), new BigDecimal("2000.00"));

    EnrollmentDto d1 =
        buildEnrollmentDto(
            e1Id, userId, planId, new BigDecimal("100.00"), new BigDecimal("2000.00"));
    EnrollmentDto d2 =
        buildEnrollmentDto(
            e2Id, userId, planId, new BigDecimal("50.00"), new BigDecimal("2000.00"));

    when(enrollmentRepository.findAllByDeletedAtIsNull()).thenReturn(List.of(e1, e2));
    when(enrollmentMapper.toDto(e1)).thenReturn(d1);
    when(enrollmentMapper.toDto(e2)).thenReturn(d2);

    List<EnrollmentDto> result = enrollmentService.getAll();

    assertNotNull(result);
    assertEquals(2, result.size());
    assertEquals(d1, result.get(0));
    assertEquals(d2, result.get(1));

    verify(enrollmentRepository).findAllByDeletedAtIsNull();
    verify(enrollmentMapper).toDto(e1);
    verify(enrollmentMapper).toDto(e2);
    verifyNoInteractions(userRepository, planRepository);
  }

  @Test
  @DisplayName("Should return empty list when no active enrollments")
  void listAll_shouldReturnEmptyList_whenNoActiveEnrollments() {
    when(enrollmentRepository.findAllByDeletedAtIsNull()).thenReturn(List.of());

    List<EnrollmentDto> result = enrollmentService.getAll();

    assertNotNull(result);
    assertTrue(result.isEmpty());

    verify(enrollmentRepository).findAllByDeletedAtIsNull();
    verifyNoInteractions(enrollmentMapper, userRepository, planRepository);
  }

  @Test
  @DisplayName("Should call mapper for each entity")
  void listAll_shouldCallMapperForEachEntity() {
    Enrollment e1 =
        buildEnrollment(
            UUID.randomUUID(), userId, planId, new BigDecimal("10.00"), new BigDecimal("111.11"));
    Enrollment e2 =
        buildEnrollment(
            UUID.randomUUID(), userId, planId, new BigDecimal("20.00"), new BigDecimal("222.22"));
    Enrollment e3 =
        buildEnrollment(
            UUID.randomUUID(), userId, planId, new BigDecimal("30.00"), new BigDecimal("333.33"));

    when(enrollmentRepository.findAllByDeletedAtIsNull()).thenReturn(List.of(e1, e2, e3));
    when(enrollmentMapper.toDto(e1)).thenReturn(
        buildEnrollmentDto(e1.getId(), userId, planId, e1.getElectionAmount(),
            e1.getPlanContribution()));
    when(enrollmentMapper.toDto(e2)).thenReturn(
        buildEnrollmentDto(e2.getId(), userId, planId, e2.getElectionAmount(),
            e2.getPlanContribution()));
    when(enrollmentMapper.toDto(e3)).thenReturn(
        buildEnrollmentDto(e3.getId(), userId, planId, e3.getElectionAmount(),
            e3.getPlanContribution()));

    enrollmentService.getAll();

    verify(enrollmentMapper, times(1)).toDto(e1);
    verify(enrollmentMapper, times(1)).toDto(e2);
    verify(enrollmentMapper, times(1)).toDto(e3);
  }

  @Test
  @DisplayName("Should propagate exception thrown by mapper")
  void listAll_shouldPropagateMapperException() {
    Enrollment e1 =
        buildEnrollment(
            UUID.randomUUID(), userId, planId, new BigDecimal("10.00"), new BigDecimal("111.11"));

    when(enrollmentRepository.findAllByDeletedAtIsNull()).thenReturn(List.of(e1));
    when(enrollmentMapper.toDto(e1)).thenThrow(new IllegalStateException("mapping boom"));

    IllegalStateException ex =
        assertThrows(IllegalStateException.class, () -> enrollmentService.getAll());

    assertEquals("mapping boom", ex.getMessage());
  }
}
