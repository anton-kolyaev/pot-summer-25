package com.coherentsolutions.pot.insuranceservice.repository;

import com.coherentsolutions.pot.insuranceservice.model.PlanType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public interface PlanTypeRepository extends JpaRepository<PlanType, Integer> {

  Optional<PlanType> findByCode(String code);

  default PlanType findByIdOrThrow(Integer id) {
    return findById(id).orElseThrow(() ->
        new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid plan type"));
  }
}
