package com.coherentsolutions.pot.insuranceservice.repository;

import static com.coherentsolutions.pot.insuranceservice.repository.SpecificationBuilder.equal;
import static com.coherentsolutions.pot.insuranceservice.repository.SpecificationBuilder.greaterThanOrEqualTo;
import static com.coherentsolutions.pot.insuranceservice.repository.SpecificationBuilder.lessThanOrEqualTo;
import static com.coherentsolutions.pot.insuranceservice.repository.SpecificationBuilder.like;

import com.coherentsolutions.pot.insuranceservice.dto.company.CompanyFilter;
import com.coherentsolutions.pot.insuranceservice.model.Company;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

/**
 * Provides JPA Specifications to filter {@link Company} entities based on various criteria
 * encapsulated in {@link CompanyFilter}.
 */
public class CompanySpecification {

  /**
   * Creates a Specification for filtering {@link Company} entities based on the given filter
   * criteria.
   */
  public static Specification<Company> withFilters(CompanyFilter filter) {
    List<Specification<Company>> specs = new ArrayList<>();

    specs.add(like(filter.getName(), r -> r.get("name")));
    specs.add(equal(
        StringUtils.hasText(filter.getCountryCode()) ? filter.getCountryCode().toUpperCase() : null,
        r -> r.get("countryCode")));
    specs.add(equal(filter.getStatus(), r -> r.get("status")));
    specs.add(greaterThanOrEqualTo(filter.getCreatedFrom(), r -> r.get("createdAt")));
    specs.add(lessThanOrEqualTo(filter.getCreatedTo(), r -> r.get("createdAt")));
    specs.add(greaterThanOrEqualTo(filter.getUpdatedFrom(), r -> r.get("updatedAt")));
    specs.add(lessThanOrEqualTo(filter.getUpdatedTo(), r -> r.get("updatedAt")));

    return specs.stream()
        .filter(Objects::nonNull)
        .reduce(Specification::and)
        .orElse((root, query, cb) -> cb.conjunction());
  }
}

