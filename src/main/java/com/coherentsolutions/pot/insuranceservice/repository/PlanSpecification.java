package com.coherentsolutions.pot.insuranceservice.repository;

import com.coherentsolutions.pot.insuranceservice.dto.plan.PlanFilter;
import com.coherentsolutions.pot.insuranceservice.model.Plan;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;

public class PlanSpecification {

  /**
   * Builds a specification based on the provided filter criteria.
   */
  public static Specification<Plan> withFilter(PlanFilter filter) {
    return (root, query, cb) -> {
      List<Predicate> predicates = new ArrayList<>();

      if (filter.getTypeId() != null) {
        predicates.add(cb.equal(root.get("type").get("id"), filter.getTypeId()));
      }

      predicates.add(cb.isNull(root.get("deletedAt")));

      return predicates.isEmpty()
          ? cb.conjunction()
          : cb.and(predicates.toArray(new Predicate[0]));
    };
  }
}