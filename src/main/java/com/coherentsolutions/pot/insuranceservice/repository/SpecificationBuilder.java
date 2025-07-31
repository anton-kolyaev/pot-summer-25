package com.coherentsolutions.pot.insuranceservice.repository;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import java.util.Collection;
import java.util.function.Function;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

public class SpecificationBuilder {

  public static <EntityT, ComparedValueTypeT> Specification<EntityT> equal(ComparedValueTypeT value,
      Function<Root<EntityT>, Path<ComparedValueTypeT>> columnPath) {
    return (root, query, criteriaBuilder) ->
        value != null ? criteriaBuilder.equal(columnPath.apply(root), value) : null;
  }

  public static <EntityT> Specification<EntityT> like(
      String value,
      Function<Root<EntityT>, Path<String>> columnPath) {
    return (root, query, criteriaBuilder) ->
        StringUtils.hasText(value)
            ? criteriaBuilder.like(
            criteriaBuilder.lower(columnPath.apply(root)),
            "%" + value.toLowerCase() + "%")
            : null;
  }

  public static <EntityT, ComparableValueTypeT extends Comparable<? super ComparableValueTypeT>>
      Specification<EntityT> lessThanOrEqualTo(
      ComparableValueTypeT toValue,
      Function<Root<EntityT>, Path<ComparableValueTypeT>> columnPath) {
    return (root, query, criteriaBuilder) -> {
      if (toValue == null) {
        return null;
      }
      return criteriaBuilder.lessThanOrEqualTo(columnPath.apply(root), toValue);
    };
  }

  public static <EntityT, ComparableValueTypeT extends Comparable<? super ComparableValueTypeT>>
      Specification<EntityT> greaterThanOrEqualTo(
      ComparableValueTypeT fromValue,
      Function<Root<EntityT>, Path<ComparableValueTypeT>> columnPath) {
    return (root, query, criteriaBuilder) -> {
      if (fromValue == null) {
        return null;
      }
      return criteriaBuilder.greaterThanOrEqualTo(columnPath.apply(root), fromValue);
    };
  }

  public static <EntityT, ValueTypeT> Specification<EntityT> joinEqual(
      ValueTypeT value,
      String joinColumn,
      String attribute
  ) {
    return (root, query, criteriaBuilder) -> {
      if (value == null) {
        return null;
      }
      Join<EntityT, ?> join = root.join(joinColumn);

      return criteriaBuilder.equal(join.get(attribute), value);
    };
  }

  public static <EntityT, ValueTypeT> Specification<EntityT> joinIn(
      Collection<ValueTypeT> values,
      String joinColumn,
      String attribute
  ) {
    return (root, query, criteriaBuilder) -> {
      if (values == null || values.isEmpty()) {
        return null;
      }

      Join<EntityT, ?> join = root.join(joinColumn);
      return join.get(attribute).in(values);
    };
  }

}
