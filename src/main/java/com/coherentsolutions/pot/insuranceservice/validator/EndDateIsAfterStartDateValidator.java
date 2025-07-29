package com.coherentsolutions.pot.insuranceservice.validator;

import com.coherentsolutions.pot.insuranceservice.annotation.ValidateEndDateIsAfterStartDate;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.lang.reflect.Method;
import java.beans.PropertyDescriptor;
import java.time.LocalDate;

public class EndDateIsAfterStartDateValidator
    implements ConstraintValidator<ValidateEndDateIsAfterStartDate, Object> {

  private String startDateField;
  private String endDateField;

  @Override
  public void initialize(ValidateEndDateIsAfterStartDate constraintAnnotation) {
    this.startDateField = constraintAnnotation.startDate();
    this.endDateField = constraintAnnotation.endDate();
  }

  @Override
  public boolean isValid(Object obj, ConstraintValidatorContext context) {
    try {
      LocalDate startDate = (LocalDate) new PropertyDescriptor(startDateField, obj.getClass())
          .getReadMethod().invoke(obj);

      LocalDate endDate = (LocalDate) new PropertyDescriptor(endDateField, obj.getClass())
          .getReadMethod().invoke(obj);

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
