package com.coherentsolutions.pot.insuranceservice.repository;

import static com.coherentsolutions.pot.insuranceservice.repository.SpecificationBuilder.equal;
import static com.coherentsolutions.pot.insuranceservice.repository.SpecificationBuilder.greaterThanOrEqualTo;
import static com.coherentsolutions.pot.insuranceservice.repository.SpecificationBuilder.lessThanOrEqualTo;
import static com.coherentsolutions.pot.insuranceservice.repository.SpecificationBuilder.like;

import com.coherentsolutions.pot.insuranceservice.dto.insurancepackage.InsurancePackageFilter;
import com.coherentsolutions.pot.insuranceservice.model.InsurancePackage;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.springframework.data.jpa.domain.Specification;

public class InsurancePackageSpecification {

  public static Specification<InsurancePackage> withFilters(InsurancePackageFilter filter) {
    List<Specification<InsurancePackage>> specs = new ArrayList<>();

    specs.add(equal(filter.getCompanyId(), r -> r.join("company").get("id")));
    specs.add(like(filter.getName(), r -> r.get("name")));
    specs.add(greaterThanOrEqualTo(filter.getStartDate(), r -> r.get("startDate")));
    specs.add(lessThanOrEqualTo(filter.getEndDate(), r -> r.get("endDate")));
    specs.add(equal(filter.getPayrollFrequency(), r -> r.get("payrollFrequency")));
    specs.add(equal(filter.getStatus(), r -> r.get("status")));

    return specs.stream()
        .filter(Objects::nonNull)
        .reduce(Specification::and)
        .orElse((root, query, cb) -> cb.conjunction());
  }


}
