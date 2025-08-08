package com.coherentsolutions.pot.insuranceservice.repository;

import static com.coherentsolutions.pot.insuranceservice.repository.SpecificationBuilder.equal;
import static com.coherentsolutions.pot.insuranceservice.repository.SpecificationBuilder.greaterThanOrEqualTo;
import static com.coherentsolutions.pot.insuranceservice.repository.SpecificationBuilder.lessThanOrEqualTo;

import com.coherentsolutions.pot.insuranceservice.dto.claim.ClaimFilter;
import com.coherentsolutions.pot.insuranceservice.model.Claim;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.springframework.data.jpa.domain.Specification;

public class ClaimSpecification {

  public static Specification<Claim> withFilters(ClaimFilter filter) {
    List<Specification<Claim>> specs = new ArrayList<>();
    
    specs.add(equal(filter.getClaimNumber(), r -> r.get("claimNumber")));
    specs.add(equal(filter.getNumber(), r -> r.get("number")));
    specs.add(equal(filter.getStatus(), r -> r.get("status")));
    specs.add(greaterThanOrEqualTo(filter.getServiceDateFrom(), r -> r.get("serviceDate")));
    specs.add(lessThanOrEqualTo(filter.getServiceDateTo(), r -> r.get("serviceDate")));
    specs.add(equal(filter.getUserId(), r -> r.join("consumer").get("id")));
    specs.add(equal(filter.getCompanyId(), r -> r.join("consumer").join("company").get("id")));
    
    return specs.stream()
        .filter(Objects::nonNull)
        .reduce(Specification::and)
        .orElse((root, query, cb) -> cb.conjunction());
  }
}
