package com.coherentsolutions.pot.insuranceservice.controller;

import com.coherentsolutions.pot.insuranceservice.dto.enrollment.EnrollmentDto;
import com.coherentsolutions.pot.insuranceservice.service.EnrollmentManagementService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/companies/{companyId}/users/{userId}/enrollments")
public class AdminEnrollmentManagementController {

  private final EnrollmentManagementService enrollmentService;

  @PreAuthorize("@companyAdminSecurityService.canAccessCompanyResource(#companyId, 'ROLE_FUNC_COMPANY_ENROLLMENT_MANAGER')")
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public EnrollmentDto createEnrollment(@PathVariable UUID companyId, @PathVariable UUID userId, @Valid @RequestBody EnrollmentDto dto) {
    return enrollmentService.createEnrollment(dto);
  }

  @PreAuthorize(
      "@userSecurityService.canAccessUserResource(#userId, 'ROLE_FUNC_CONSUMER') "
          + "or "
          + "@companyAdminSecurityService.canAccessCompanyResource(#companyId, 'ROLE_FUNC_COMPANY_ENROLLMENT_MANAGER')"
  )
  @GetMapping
  public List<EnrollmentDto> getAllEnrollments(Authentication authentication, @PathVariable UUID companyId, @PathVariable UUID userId) {
    return enrollmentService.getAll(authentication, companyId, userId);
  }

}
