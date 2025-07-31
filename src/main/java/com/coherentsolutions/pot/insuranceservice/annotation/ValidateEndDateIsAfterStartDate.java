package com.coherentsolutions.pot.insuranceservice.annotation;

import com.auth0.jwt.interfaces.Payload;
import com.coherentsolutions.pot.insuranceservice.validator.EndDateIsAfterStartDateValidator;
import jakarta.validation.Constraint;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = EndDateIsAfterStartDateValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidateEndDateIsAfterStartDate {

  String message() default "End date must be after start date";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  String startDate();

  String endDate();
}
