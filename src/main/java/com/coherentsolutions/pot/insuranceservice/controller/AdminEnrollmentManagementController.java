package com.coherentsolutions.pot.insuranceservice.controller;

import com.coherentsolutions.pot.insuranceservice.dto.enrollment.EnrollmentDto;
import com.coherentsolutions.pot.insuranceservice.security.service.UserSecurityService;
import com.coherentsolutions.pot.insuranceservice.service.EnrollmentManagementService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/user/{userId}/enrollments")
public class AdminEnrollmentManagementController {

  private final EnrollmentManagementService enrollmentService;
  private final UserSecurityService userSecurityService;

  @PreAuthorize("@userSecurityService.canAccessUserResource(#userId, 'ROLE_FUNC_CONSUMER')")
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public EnrollmentDto createEnrollment(@PathVariable UUID userId, @Valid @RequestBody EnrollmentDto dto) {
    return enrollmentService.createEnrollment(dto);
  }

  @PreAuthorize("@userSecurityService.canAccessUserResource(#userId, 'ROLE_FUNC_CONSUMER')")
  @GetMapping
  public List<EnrollmentDto> getAllEnrollments(@PathVariable UUID userId) {
    return enrollmentService.getAll();
  }

}
