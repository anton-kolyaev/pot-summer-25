package com.coherentsolutions.pot.insuranceservice.repository;

import com.coherentsolutions.pot.insuranceservice.model.Plan;
import java.time.Instant;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public interface PlanRepository extends JpaRepository<Plan, UUID>, JpaSpecificationExecutor<Plan> {

  default Plan findByIdOrThrow(UUID id) {
    return findById(id).orElseThrow(() ->
        new ResponseStatusException(HttpStatus.NOT_FOUND, "Plan not found"));
  }

  @Query(value = "SELECT deleted_at FROM plans WHERE id = :id", nativeQuery = true)
  Instant findDeletedAtById(@Param("id") UUID id);
}
