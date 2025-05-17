package greencity.annotations;

import greencity.validator.UniqueEventDatesValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = UniqueEventDatesValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueEventDates {
    String message() default "You can't enter the same date for two days";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
