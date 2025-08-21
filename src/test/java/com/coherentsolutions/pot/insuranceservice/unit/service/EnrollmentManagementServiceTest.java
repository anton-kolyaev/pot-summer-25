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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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
      UUID id, UUID userIdParam, UUID planIdParam,
      BigDecimal electionAmount, BigDecimal contribution) {

    Enrollment enrollment = new Enrollment();
    enrollment.setId(id);

    User enrollmentUser = new User();
    enrollmentUser.setId(userIdParam);
    enrollment.setUser(enrollmentUser);

    Plan enrollmentPlan = new Plan();
    enrollmentPlan.setId(planIdParam);
    enrollmentPlan.setContribution(contribution);
    enrollment.setPlan(enrollmentPlan);

    enrollment.setElectionAmount(electionAmount);
    enrollment.setPlanContribution(contribution);
    return enrollment;
  }

  private EnrollmentDto buildEnrollmentDto(
      UUID id, UUID userIdParam, UUID planIdParam,
      BigDecimal electionAmount, BigDecimal contribution) {

    return EnrollmentDto.builder()
        .id(id)
        .userId(userIdParam)
        .planId(planIdParam)
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

    ResponseStatusException exception =
        assertThrows(ResponseStatusException.class,
            () -> enrollmentService.createEnrollment(requestDto));

    assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    assertEquals("Active enrollment already exists for this user and plan", exception.getReason());

    verify(enrollmentRepository).existsByUserIdAndPlanIdAndDeletedAtIsNull(userId, planId);
    verifyNoInteractions(enrollmentMapper);
    verify(enrollmentRepository, never()).save(any());
  }

  @Test
  @DisplayName("Should throw NOT_FOUND when user does not exist")
  void shouldThrowWhenUserNotFound() {
    doCallRealMethod().when(userRepository).findByIdOrThrow(userId);
    when(userRepository.findById(userId)).thenReturn(Optional.empty());

    ResponseStatusException exception =
        assertThrows(ResponseStatusException.class,
            () -> enrollmentService.createEnrollment(requestDto));

    assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
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

    ResponseStatusException exception =
        assertThrows(ResponseStatusException.class,
            () -> enrollmentService.createEnrollment(requestDto));

    assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    verify(planRepository).findById(planId);
    verify(enrollmentRepository, never()).existsByUserIdAndPlanIdAndDeletedAtIsNull(any(), any());
    verifyNoInteractions(enrollmentMapper);
  }

  @Test
  @DisplayName("Should return mapped DTOs for active enrollments")
  void listAll_shouldReturnDtos() {
    UUID enrollmentId1 = UUID.randomUUID();
    UUID enrollmentId2 = UUID.randomUUID();
    Authentication authentication =
        new UsernamePasswordAuthenticationToken(
            "testUser", // principal (can also be a UserDetails object)
            null,
            List.of(new SimpleGrantedAuthority("ROLE_APPLICATION_ADMIN"))
        );
    UUID companyId = UUID.randomUUID();

    Enrollment enrollment1 =
        buildEnrollment(
            enrollmentId1, userId, planId, new BigDecimal("100.00"), new BigDecimal("2000.00"));
    Enrollment enrollment2 =
        buildEnrollment(
            enrollmentId2, userId, planId, new BigDecimal("50.00"), new BigDecimal("2000.00"));

    EnrollmentDto enrollmentDto1 =
        buildEnrollmentDto(
            enrollmentId1, userId, planId, new BigDecimal("100.00"), new BigDecimal("2000.00"));
    EnrollmentDto enrollmentDto2 =
        buildEnrollmentDto(
            enrollmentId2, userId, planId, new BigDecimal("50.00"), new BigDecimal("2000.00"));

    when(enrollmentRepository.findAllByDeletedAtIsNull()).thenReturn(
        List.of(enrollment1, enrollment2));
    when(enrollmentMapper.toDto(enrollment1)).thenReturn(enrollmentDto1);
    when(enrollmentMapper.toDto(enrollment2)).thenReturn(enrollmentDto2);

    List<EnrollmentDto> result = enrollmentService.getAll(authentication, companyId, userId);

    assertNotNull(result);
    assertEquals(2, result.size());
    assertEquals(enrollmentDto1, result.get(0));
    assertEquals(enrollmentDto2, result.get(1));

    verify(enrollmentRepository).findAllByDeletedAtIsNull();
    verify(enrollmentMapper).toDto(enrollment1);
    verify(enrollmentMapper).toDto(enrollment2);
    verifyNoInteractions(userRepository, planRepository);
  }

  @Test
  @DisplayName("Should return empty list when no active enrollments")
  void listAll_shouldReturnEmptyList_whenNoActiveEnrollments() {
    when(enrollmentRepository.findAllByDeletedAtIsNull()).thenReturn(List.of());
    Authentication authentication =
        new UsernamePasswordAuthenticationToken(
            "testUser", // principal (can also be a UserDetails object)
            null,
            List.of(new SimpleGrantedAuthority("ROLE_APPLICATION_ADMIN"))
        );
    UUID companyId = UUID.randomUUID();

    List<EnrollmentDto> result = enrollmentService.getAll(authentication, companyId, userId);

    assertNotNull(result);
    assertTrue(result.isEmpty());

    verify(enrollmentRepository).findAllByDeletedAtIsNull();
    verifyNoInteractions(enrollmentMapper, userRepository, planRepository);
  }

  @Test
  @DisplayName("Should call mapper for each entity")
  void listAll_shouldCallMapperForEachEntity() {
    Enrollment enrollment1 =
        buildEnrollment(
            UUID.randomUUID(), userId, planId, new BigDecimal("10.00"), new BigDecimal("111.11"));
    Enrollment enrollment2 =
        buildEnrollment(
            UUID.randomUUID(), userId, planId, new BigDecimal("20.00"), new BigDecimal("222.22"));
    Enrollment enrollment3 =
        buildEnrollment(
            UUID.randomUUID(), userId, planId, new BigDecimal("30.00"), new BigDecimal("333.33"));

    when(enrollmentRepository.findAllByDeletedAtIsNull()).thenReturn(
        List.of(enrollment1, enrollment2, enrollment3));
    when(enrollmentMapper.toDto(enrollment1)).thenReturn(
        buildEnrollmentDto(enrollment1.getId(), userId, planId, enrollment1.getElectionAmount(),
            enrollment1.getPlanContribution()));
    when(enrollmentMapper.toDto(enrollment2)).thenReturn(
        buildEnrollmentDto(enrollment2.getId(), userId, planId, enrollment2.getElectionAmount(),
            enrollment2.getPlanContribution()));
    when(enrollmentMapper.toDto(enrollment3)).thenReturn(
        buildEnrollmentDto(enrollment3.getId(), userId, planId, enrollment3.getElectionAmount(),
            enrollment3.getPlanContribution()));

    Authentication authentication =
        new UsernamePasswordAuthenticationToken(
            "testUser", // principal (can also be a UserDetails object)
            null,
            List.of(new SimpleGrantedAuthority("ROLE_APPLICATION_ADMIN"))
        );
    UUID companyId = UUID.randomUUID();

    enrollmentService.getAll(authentication, companyId, userId);

    verify(enrollmentMapper, times(1)).toDto(enrollment1);
    verify(enrollmentMapper, times(1)).toDto(enrollment2);
    verify(enrollmentMapper, times(1)).toDto(enrollment3);
  }

  @Test
  @DisplayName("Should propagate exception thrown by mapper")
  void listAll_shouldPropagateMapperException() {
    Enrollment enrollment1 =
        buildEnrollment(
            UUID.randomUUID(), userId, planId, new BigDecimal("10.00"), new BigDecimal("111.11"));

    when(enrollmentRepository.findAllByDeletedAtIsNull()).thenReturn(List.of(enrollment1));
    when(enrollmentMapper.toDto(enrollment1)).thenThrow(new IllegalStateException("mapping boom"));

    Authentication authentication =
        new UsernamePasswordAuthenticationToken(
            "testUser", // principal (can also be a UserDetails object)
            null,
            List.of(new SimpleGrantedAuthority("ROLE_APPLICATION_ADMIN"))
        );
    UUID companyId = UUID.randomUUID();

    IllegalStateException exception =
        assertThrows(IllegalStateException.class, () -> enrollmentService.getAll(
            authentication, companyId, userId));

    assertEquals("mapping boom", exception.getMessage());
  }
}
