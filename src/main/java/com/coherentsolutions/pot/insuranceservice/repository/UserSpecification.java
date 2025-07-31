package com.coherentsolutions.pot.insuranceservice.repository;

import static com.coherentsolutions.pot.insuranceservice.repository.SpecificationBuilder.equal;
import static com.coherentsolutions.pot.insuranceservice.repository.SpecificationBuilder.joinEqual;
import static com.coherentsolutions.pot.insuranceservice.repository.SpecificationBuilder.joinIn;
import static com.coherentsolutions.pot.insuranceservice.repository.SpecificationBuilder.like;

import com.coherentsolutions.pot.insuranceservice.dto.user.UserFilter;
import com.coherentsolutions.pot.insuranceservice.model.User;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

/**
 * Provides JPA Specifications to filter {@link User} entities based on various criteria
 * encapsulated in {@link UserFilter}.
 */
public class UserSpecification {

  /**
   * Creates a Specification for filtering {@link User} entities according to the criteria defined
   * in the given {@link UserFilter}.
   */
  public static Specification<User> withFilters(UserFilter filter) {
    List<Specification<User>> specs = new ArrayList<>();

    specs.add(joinEqual(filter.getCompanyId(), "company", "id"));
    specs.add(nameLike(filter.getName()));
    specs.add(like(filter.getEmail(), r -> r.get("email")));
    specs.add(equal(filter.getDateOfBirth(), r -> r.get("dateOfBirth")));
    specs.add(equal(filter.getStatus(), r -> r.get("status")));
    specs.add(like(filter.getSsn(), r -> r.get("ssn")));
    specs.add(joinIn(filter.getFunctions(), "functions", "function"));

    return specs.stream()
        .filter(Objects::nonNull)
        .reduce(Specification::and)
        .orElse((root, query, cb) -> cb.conjunction());
  }

  private static Specification<User> nameLike(String name) {
    if (!StringUtils.hasText(name)) {
      return null;
    }
    return (root, query, cb) -> {
      String pattern = "%" + name.toLowerCase() + "%";
      return cb.or(
          cb.like(cb.lower(root.get("firstName")), pattern),
          cb.like(cb.lower(root.get("lastName")), pattern)
      );
    };
  }
}
