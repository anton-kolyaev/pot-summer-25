package com.coherentsolutions.pot.insuranceservice.repository;

import com.coherentsolutions.pot.insuranceservice.dto.plan.PlanFilter;
import com.coherentsolutions.pot.insuranceservice.model.Plan;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import org.springframework.data.jpa.domain.Specification;

public class PlanSpecification {

  /**
   * Builds a specification based on the provided filter criteria.
   */
  public static Specification<Plan> withFilter(PlanFilter filter) {
    return (root, query, cb) -> {
      List<Predicate> predicates = Stream.of(
              typeIdPredicate(filter, root, cb)
          )
          .filter(Objects::nonNull)
          .toList();

      return predicates.isEmpty()
          ? cb.conjunction()
          : cb.and(predicates.toArray(new Predicate[0]));
    };
  }

  private static Predicate typeIdPredicate(PlanFilter filter, Root<Plan> root, CriteriaBuilder cb) {
    return filter.getTypeId() != null
        ? cb.equal(root.get("type").get("id"), filter.getTypeId())
        : null;
  }
}
