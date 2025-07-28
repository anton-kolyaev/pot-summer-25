package com.coherentsolutions.pot.insuranceservice.repository;

import com.coherentsolutions.pot.insuranceservice.dto.insurancepackage.InsurancePackageFilter;
import com.coherentsolutions.pot.insuranceservice.model.InsurancePackage;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

public class InsurancePackageSpecification {

  public static Specification<InsurancePackage> withFilters(InsurancePackageFilter filter) {
    return (root, query, criteriaBuilder) -> {
      List<Predicate> predicates = Stream.of(
              companyIdPredicate(filter, root, criteriaBuilder),
              namePredicate(filter, root, criteriaBuilder),
              startDatePredicate(filter, root, criteriaBuilder),
              endDatePredicate(filter, root, criteriaBuilder),
              payrollFrequencyPredicate(filter, root, criteriaBuilder),
              statusPredicate(filter, root, criteriaBuilder))
          .filter(Objects::nonNull)
          .toList();

      return predicates.isEmpty()
          ? criteriaBuilder.conjunction()
          : criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    };
  }

  private static Predicate companyIdPredicate(InsurancePackageFilter filter, Root<?> root,
      CriteriaBuilder cb) {
    return filter.getCompanyId() != null
        ? cb.equal(
        root.join("company").get("id"),
        filter.getCompanyId()
    )
        : null;
  }

  private static Predicate namePredicate(InsurancePackageFilter filter, Root<?> root,
      CriteriaBuilder cb) {
    return StringUtils.hasText(filter.getName())
        ? cb.like(
        cb.lower(root.get("name")),
        "%" + filter.getName().toLowerCase() + "%"
    )
        : null;
  }

  private static Predicate startDatePredicate(InsurancePackageFilter filter, Root<?> root,
      CriteriaBuilder cb) {
    return filter.getStartDate() != null
        ? cb.equal(root.get("startDate"), filter.getStartDate())
        : null;
  }

  private static Predicate endDatePredicate(InsurancePackageFilter filter, Root<?> root,
      CriteriaBuilder cb) {
    return filter.getEndDate() != null
        ? cb.equal(root.get("endDate"), filter.getEndDate())
        : null;
  }

  private static Predicate payrollFrequencyPredicate(InsurancePackageFilter filter, Root<?> root,
      CriteriaBuilder cb) {
    return filter.getPayrollFrequency() != null
        ? cb.equal(root.get("payrollFrequency"), filter.getPayrollFrequency())
        : null;
  }

  private static Predicate statusPredicate(InsurancePackageFilter filter, Root<?> root,
      CriteriaBuilder cb) {
    return filter.getStatus() != null
        ? cb.equal(root.get("status"), filter.getStatus())
        : null;
  }


}
