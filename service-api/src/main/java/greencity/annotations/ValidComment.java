package greencity.annotations;

import greencity.validator.CommentValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = CommentValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidComment {
    String message() default "Invalid comment: must not be empty, contain emojis, or links.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
