package greencity.annotations;

import greencity.validator.LocationOrOnlineValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = LocationOrOnlineValidator.class)
public @interface ValidLocationOrOnlineLink {
    String message() default "Please provide at least a location or online link for each day";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
