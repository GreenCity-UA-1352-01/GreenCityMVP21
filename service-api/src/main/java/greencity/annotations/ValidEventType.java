package greencity.annotations;

import greencity.validator.EventTypeValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EventTypeValidator.class)
public @interface ValidEventType {
    String message() default "At least one event type (Place or Online) must be selected";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
