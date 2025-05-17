package greencity.annotations;

import greencity.validator.UniqueSessionDatesValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = UniqueSessionDatesValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueSessionDates {
    String message() default "Sessions must be on unique days";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
