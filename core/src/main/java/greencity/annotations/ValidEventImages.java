package greencity.annotations;

import greencity.validator.EventImagesValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EventImagesValidator.class)
public @interface ValidEventImages {
    String message() default "Invalid event images (must be JPG/PNG and max 10MB each, up to 5 files)";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
