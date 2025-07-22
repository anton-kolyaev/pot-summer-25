package com.coherentsolutions.pot.insuranceservice.repository;

import com.coherentsolutions.pot.insuranceservice.model.Company;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;

/**
 * Repository interface for accessing and managing {@link Company} entities. Extends
 * {@link JpaRepository} for basic CRUD operations and {@link JpaSpecificationExecutor} for
 * filtering capabilities.
 */
@Repository
public interface CompanyRepository extends JpaRepository<Company, UUID>,
    JpaSpecificationExecutor<Company> {

  /**
   * Finds a {@link Company} by its ID or throws a {@link ResponseStatusException} with 404 NOT
   * FOUND status if the company is not present.
   */
  default Company findByIdOrThrow(UUID id) {
    return findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Company not found"));
  }

}

