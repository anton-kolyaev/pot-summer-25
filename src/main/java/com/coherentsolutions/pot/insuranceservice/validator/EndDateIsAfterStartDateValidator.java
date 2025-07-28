package com.coherentsolutions.pot.insuranceservice.validator;

import com.coherentsolutions.pot.insuranceservice.annotation.ValidateEndDateIsAfterStartDate;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.lang.reflect.Method;
import java.time.LocalDate;

public class EndDateIsAfterStartDateValidator
    implements ConstraintValidator<ValidateEndDateIsAfterStartDate, Object> {

  @Override
  public boolean isValid(Object obj, ConstraintValidatorContext context) {
    try {
      Method getStartDate = obj.getClass().getMethod("getStartDate");
      Method getEndDate = obj.getClass().getMethod("getEndDate");

      Object startDateObj = getStartDate.invoke(obj);
      Object endDateObj = getEndDate.invoke(obj);

      LocalDate startDate = (LocalDate) startDateObj;
      LocalDate endDate = (LocalDate) endDateObj;

      if (startDate == null || endDate == null || !endDate.isAfter(startDate)) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
            .addPropertyNode("endDate")
            .addConstraintViolation();
        return false;
      }

      return true;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

  }

}
