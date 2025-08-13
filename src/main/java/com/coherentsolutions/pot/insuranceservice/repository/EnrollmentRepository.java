package com.coherentsolutions.pot.insuranceservice.repository;

import com.coherentsolutions.pot.insuranceservice.model.Enrollment;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, UUID> {

  default Enrollment findByIdOrThrow(UUID id) {
    return findById(id).orElseThrow(() ->
        new ResponseStatusException(HttpStatus.NOT_FOUND, "Enrollment not found"));
  }

  boolean existsByUserIdAndPlanIdAndDeletedAtIsNull(UUID userId, UUID planId);

  List<Enrollment> findAllByDeletedAtIsNull();
}