package com.coherentsolutions.pot.insurance_service.repository;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.jpa.domain.Specification;

import com.coherentsolutions.pot.insurance_service.dto.user.UserFilter;
import com.coherentsolutions.pot.insurance_service.model.User;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import static org.springframework.util.StringUtils.hasText;

public class UserSpecification {
    
    public static Specification<User> withFilters(UserFilter filter) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = Arrays.asList(
                namePredicate(filter, root, criteriaBuilder),
                emailPredicate(filter, root, criteriaBuilder),
                dateOfBirthPredicate(filter, root, criteriaBuilder),
                statusPredicate(filter, root, criteriaBuilder),
                ssnPredicate(filter, root, criteriaBuilder),
                functionPredicate(filter, root, criteriaBuilder)
            ).stream()
            .filter(predicate -> predicate != null)
            .collect(Collectors.toList());

            return predicates.isEmpty() 
                ? criteriaBuilder.conjunction() 
                : criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private static Predicate namePredicate(UserFilter filter, Root<User> root, CriteriaBuilder criteriaBuilder) {
        return hasText(filter.getName())
            ? criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + filter.getName().toLowerCase() + "%")
            : null;
    }

    private static Predicate emailPredicate(UserFilter filter, Root<User> root, CriteriaBuilder criteriaBuilder) {
        return hasText(filter.getEmail())
            ? criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), "%" + filter.getEmail().toLowerCase() + "%")
            : null;
    }

    private static Predicate dateOfBirthPredicate(UserFilter filter, Root<User> root, CriteriaBuilder criteriaBuilder) {
        return filter.getDateOfBirth() != null
            ? criteriaBuilder.equal(root.get("dateOfBirth"), filter.getDateOfBirth())
            : null;
    }

    private static Predicate statusPredicate(UserFilter filter, Root<User> root, CriteriaBuilder criteriaBuilder) {
        return filter.getStatus() != null
            ? criteriaBuilder.equal(root.get("status"), filter.getStatus())
            : null;
    }

    private static Predicate ssnPredicate(UserFilter filter, Root<User> root, CriteriaBuilder criteriaBuilder) {
        return hasText(filter.getSsn())
            ? criteriaBuilder.like(criteriaBuilder.lower(root.get("ssn")), "%" + filter.getSsn().toLowerCase() + "%")
            : null;
    }

    private static Predicate functionPredicate(UserFilter filter, Root<User> root, CriteriaBuilder criteriaBuilder) {
    if (filter.getFunctions() != null && !filter.getFunctions().isEmpty()) {
        var join = root.join("functions"); 
        return join.get("function").in(filter.getFunctions());
    }
    return null;
    }
}
