package com.coherentsolutions.pot.insuranceservice.repository;

import static com.coherentsolutions.pot.insuranceservice.repository.SpecificationBuilder.equal;

import com.coherentsolutions.pot.insuranceservice.dto.plan.PlanFilter;
import com.coherentsolutions.pot.insuranceservice.model.Plan;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.springframework.data.jpa.domain.Specification;

public class PlanSpecification {

  /**
   * Builds a specification based on the provided filter criteria.
   */
  public static Specification<Plan> withFilter(PlanFilter filter) {
    List<Specification<Plan>> specs = new ArrayList<>();

    specs.add(equal(filter.getTypeId(), r -> r.join("type").get("id")));

    return specs.stream()
        .filter(Objects::nonNull)
        .reduce(Specification::and)
        .orElse((root, query, cb) -> cb.conjunction());
  }
}
