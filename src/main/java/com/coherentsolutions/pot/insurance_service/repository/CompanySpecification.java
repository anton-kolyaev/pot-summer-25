package com.coherentsolutions.pot.insurance_service.repository;

import com.coherentsolutions.pot.insurance_service.dto.CompanyFilter;
import com.coherentsolutions.pot.insurance_service.model.Company;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Path;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CompanySpecification {

    public static Specification<Company> withFilters(CompanyFilter filter) {
        return (root, query, criteriaBuilder) -> {
           List<Predicate> predicates = Stream.of(
              namePredicate(filter, root, criteriaBuilder),
              countryCodePredicate(filter, root, criteriaBuilder),
              statusPredicate(filter, root, criteriaBuilder),
              createdDatePredicate(filter, root, criteriaBuilder),
              updatedDatePredicate(filter, root, criteriaBuilder)
          )
          .filter(Objects::nonNull)
          .toList();

            return predicates.isEmpty() 
                ? criteriaBuilder.conjunction() 
                : criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private static Predicate namePredicate(CompanyFilter filter, Root<Company> root, CriteriaBuilder criteriaBuilder) {
        return StringUtils.hasText(filter.getName()) 
            ? criteriaBuilder.like(
                criteriaBuilder.lower(root.get("name")),
                "%" + filter.getName().toLowerCase() + "%"
            )
            : null;
    }

    private static Predicate countryCodePredicate(CompanyFilter filter, Root<Company> root, CriteriaBuilder criteriaBuilder) {
        return StringUtils.hasText(filter.getCountryCode())
            ? criteriaBuilder.equal(
                root.get("countryCode"),
                filter.getCountryCode().toUpperCase()
            )
            : null;
    }

    private static Predicate statusPredicate(CompanyFilter filter, Root<Company> root, CriteriaBuilder criteriaBuilder) {
        if (filter.getStatus() == null) {
            return null;
        }

        return criteriaBuilder.equal(root.get("status"), filter.getStatus());
    }

    private static Predicate createdDatePredicate(CompanyFilter filter, Root<Company> root, CriteriaBuilder criteriaBuilder) {
        return createDateRangePredicate(
            filter.getCreatedFrom(),
            filter.getCreatedTo(),
            root.get("createdAt"),
            criteriaBuilder
        );
    }

    private static Predicate updatedDatePredicate(CompanyFilter filter, Root<Company> root, CriteriaBuilder criteriaBuilder) {
        return createDateRangePredicate(
            filter.getUpdatedFrom(),
            filter.getUpdatedTo(),
            root.get("updatedAt"),
            criteriaBuilder
        );
    }

    private static Predicate createDateRangePredicate(
            java.time.Instant from,
            java.time.Instant to,
            Path<java.time.Instant> datePath,
            CriteriaBuilder criteriaBuilder) {
        
        List<Predicate> datePredicates = Stream.of(
            from != null ? criteriaBuilder.greaterThanOrEqualTo(datePath, from) : null,
            to != null ? criteriaBuilder.lessThanOrEqualTo(datePath, to) : null
        )
        .filter(Objects::nonNull)
        .toList();

        return datePredicates.isEmpty() 
            ? null 
            : criteriaBuilder.and(datePredicates.toArray(new Predicate[0]));
    }
} 