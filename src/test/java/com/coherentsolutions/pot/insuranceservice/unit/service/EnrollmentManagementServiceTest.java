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

    requestDto = EnrollmentDto.builder()
        .userId(userId)
        .planId(planId)
        .electionAmount(new BigDecimal("100.00"))
        .build();

    mappedEntity = new Enrollment(); // fields are set in service (user, plan, contribution)
    savedEntity = new Enrollment();
    savedEntity.setId(UUID.randomUUID());
    savedEntity.setUser(user);
    savedEntity.setPlan(plan);
    savedEntity.setElectionAmount(new BigDecimal("100.00"));
    savedEntity.setPlanContribution(new BigDecimal("2000.00"));

    responseDto = EnrollmentDto.builder()
        .id(savedEntity.getId())
        .userId(userId)
        .planId(planId)
        .electionAmount(new BigDecimal("100.00"))
        .planContribution(new BigDecimal("2000.00"))
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

    ResponseStatusException ex = assertThrows(ResponseStatusException.class,
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

    ResponseStatusException ex = assertThrows(ResponseStatusException.class,
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

    ResponseStatusException ex = assertThrows(ResponseStatusException.class,
        () -> enrollmentService.createEnrollment(requestDto));

    assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    verify(planRepository).findById(planId);
    verify(enrollmentRepository, never()).existsByUserIdAndPlanIdAndDeletedAtIsNull(any(), any());
    verifyNoInteractions(enrollmentMapper);
  }
}
