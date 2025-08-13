package com.coherentsolutions.pot.insuranceservice.controller;

import com.coherentsolutions.pot.insuranceservice.dto.enrollment.EnrollmentDto;
import com.coherentsolutions.pot.insuranceservice.service.EnrollmentManagementService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/enrollments")
public class AdminEnrollmentManagementController {

  private final EnrollmentManagementService enrollmentService;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public EnrollmentDto createEnrollment(@Valid @RequestBody EnrollmentDto dto) {
    return enrollmentService.createEnrollment(dto);
  }

  @GetMapping
  public List<EnrollmentDto> getAllEnrollments() {
    return enrollmentService.getAll();
  }

}
