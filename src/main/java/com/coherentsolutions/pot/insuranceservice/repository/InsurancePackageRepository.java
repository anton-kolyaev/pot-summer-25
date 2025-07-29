package com.coherentsolutions.pot.insuranceservice.repository;

import com.coherentsolutions.pot.insuranceservice.model.InsurancePackage;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;

@Repository
public interface InsurancePackageRepository extends JpaRepository<InsurancePackage, UUID>,
    JpaSpecificationExecutor<InsurancePackage> {

  default InsurancePackage findByIdOrThrow(UUID id) {
    return findById(id).orElseThrow(
        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Insurance package not found"));
  }
}
