package com.coherentsolutions.pot.insuranceservice.repository;

import com.coherentsolutions.pot.insuranceservice.model.PlanType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlanTypeRepository extends JpaRepository<PlanType, Integer> {

  Optional<PlanType> findByCode(String code);
}
