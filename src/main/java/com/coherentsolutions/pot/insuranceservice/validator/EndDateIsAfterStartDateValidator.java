package com.coherentsolutions.pot.insuranceservice.validator;

import com.coherentsolutions.pot.insuranceservice.annotation.ValidateEndDateIsAfterStartDate;
import com.coherentsolutions.pot.insuranceservice.dto.insurancepackage.InsurancePackageDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class EndDateIsAfterStartDateValidator
    implements ConstraintValidator<ValidateEndDateIsAfterStartDate, InsurancePackageDto> {

  public boolean isValid(InsurancePackageDto insurancePackageDto,
      ConstraintValidatorContext context) {
    return insurancePackageDto.getEndDate().isAfter(LocalDate.now());
  }

}
