package com.coherentsolutions.pot.insurance_service.repository;

import com.coherentsolutions.pot.insurance_service.dto.CompanyFilter;
import com.coherentsolutions.pot.insurance_service.model.Company;
import com.coherentsolutions.pot.insurance_service.model.enums.CompanyStatus;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import jakarta.persistence.criteria.Predicate;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CompanySpecification {

    public static Specification<Company> withFilters(CompanyFilter filter) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = Arrays.asList(
                namePredicate(filter, root, criteriaBuilder),
                countryCodePredicate(filter, root, criteriaBuilder),
                statusPredicate(filter, root, criteriaBuilder),
                createdDatePredicate(filter, root, criteriaBuilder),
                updatedDatePredicate(filter, root, criteriaBuilder)
            ).stream()
                .filter(predicate -> predicate != null)
                .collect(Collectors.toList());

            return predicates.isEmpty() 
                ? criteriaBuilder.conjunction() 
                : criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private static Predicate namePredicate(CompanyFilter filter, jakarta.persistence.criteria.Root<Company> root, jakarta.persistence.criteria.CriteriaBuilder criteriaBuilder) {
        return StringUtils.hasText(filter.getName()) 
            ? criteriaBuilder.like(
                criteriaBuilder.lower(root.get("name")),
                "%" + filter.getName().toLowerCase() + "%"
            )
            : null;
    }

    private static Predicate countryCodePredicate(CompanyFilter filter, jakarta.persistence.criteria.Root<Company> root, jakarta.persistence.criteria.CriteriaBuilder criteriaBuilder) {
        return StringUtils.hasText(filter.getCountryCode())
            ? criteriaBuilder.equal(
                root.get("countryCode"),
                filter.getCountryCode().toUpperCase()
            )
            : null;
    }

    private static Predicate statusPredicate(CompanyFilter filter, jakarta.persistence.criteria.Root<Company> root, jakarta.persistence.criteria.CriteriaBuilder criteriaBuilder) {
        return StringUtils.hasText(filter.getStatus())
            ? createStatusPredicate(filter.getStatus(), root, criteriaBuilder)
            : null;
    }

    private static Predicate createStatusPredicate(String status, jakarta.persistence.criteria.Root<Company> root, jakarta.persistence.criteria.CriteriaBuilder criteriaBuilder) {
        try {
            CompanyStatus companyStatus = CompanyStatus.valueOf(status.toUpperCase());
            return criteriaBuilder.equal(root.get("status"), companyStatus);
        } catch (IllegalArgumentException e) {
            return criteriaBuilder.disjunction(); // Return no results for invalid status
        }
    }

    private static Predicate createdDatePredicate(CompanyFilter filter, jakarta.persistence.criteria.Root<Company> root, jakarta.persistence.criteria.CriteriaBuilder criteriaBuilder) {
        return createDateRangePredicate(
            filter.getCreatedFrom(),
            filter.getCreatedTo(),
            root.get("createdAt"),
            criteriaBuilder
        );
    }

    private static Predicate updatedDatePredicate(CompanyFilter filter, jakarta.persistence.criteria.Root<Company> root, jakarta.persistence.criteria.CriteriaBuilder criteriaBuilder) {
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
            jakarta.persistence.criteria.Path<java.time.Instant> datePath,
            jakarta.persistence.criteria.CriteriaBuilder criteriaBuilder) {
        
        List<Predicate> datePredicates = Arrays.asList(
            from != null ? criteriaBuilder.greaterThanOrEqualTo(datePath, from) : null,
            to != null ? criteriaBuilder.lessThanOrEqualTo(datePath, to) : null
        ).stream()
            .filter(predicate -> predicate != null)
            .collect(Collectors.toList());

        return datePredicates.isEmpty() 
            ? null 
            : criteriaBuilder.and(datePredicates.toArray(new Predicate[0]));
    }
} 