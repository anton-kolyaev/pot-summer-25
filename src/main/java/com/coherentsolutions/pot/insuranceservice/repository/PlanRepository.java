package com.coherentsolutions.pot.insuranceservice.repository;

import com.coherentsolutions.pot.insuranceservice.model.Plan;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public interface PlanRepository extends JpaRepository<Plan, UUID>, JpaSpecificationExecutor<Plan> {

  default Plan findByIdOrThrow(UUID id) {
    return findById(id).orElseThrow(() ->
        new ResponseStatusException(HttpStatus.NOT_FOUND, "Plan not found"));
  }

  Optional<Plan> findByIdAndDeletedAtIsNull(UUID id);

  default Plan findActiveByIdOrThrow(UUID id) {
    return findByIdAndDeletedAtIsNull(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Plan not found"));
  }
}
