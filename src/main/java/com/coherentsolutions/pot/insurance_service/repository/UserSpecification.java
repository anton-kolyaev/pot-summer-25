package com.coherentsolutions.pot.insurance_service.repository;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import org.springframework.data.jpa.domain.Specification;
import static org.springframework.util.StringUtils.hasText;

import com.coherentsolutions.pot.insurance_service.dto.user.UserFilter;
import com.coherentsolutions.pot.insurance_service.model.User;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class UserSpecification {

    public static Specification<User> withFilters(UserFilter filter) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = Stream.of(
              namePredicate(filter, root, criteriaBuilder),
              emailPredicate(filter, root, criteriaBuilder),
              dateOfBirthPredicate(filter, root, criteriaBuilder),
              statusPredicate(filter, root, criteriaBuilder),
              ssnPredicate(filter, root, criteriaBuilder),
              functionPredicate(filter, root, criteriaBuilder))
          .filter(Objects::nonNull)
          .toList();

            return predicates.isEmpty()
                    ? criteriaBuilder.conjunction()
                    : criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private static Predicate namePredicate(UserFilter filter, Root<User> root, CriteriaBuilder criteriaBuilder) {
        if (hasText(filter.getName())) {
            String pattern = "%" + filter.getName().toLowerCase() + "%";
            Predicate firstNameLike = criteriaBuilder.like(criteriaBuilder.lower(root.get("firstName")), pattern);
            Predicate lastNameLike = criteriaBuilder.like(criteriaBuilder.lower(root.get("lastName")), pattern);
            return criteriaBuilder.or(firstNameLike, lastNameLike);
        }
        return null;
    }

    private static Predicate emailPredicate(UserFilter filter, Root<User> root, CriteriaBuilder criteriaBuilder) {
        return hasText(filter.getEmail())
                ? criteriaBuilder.like(criteriaBuilder.lower(root.get("email")),
                        "%" + filter.getEmail().toLowerCase() + "%")
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
                ? criteriaBuilder.like(criteriaBuilder.lower(root.get("ssn")),
                        "%" + filter.getSsn().toLowerCase() + "%")
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
