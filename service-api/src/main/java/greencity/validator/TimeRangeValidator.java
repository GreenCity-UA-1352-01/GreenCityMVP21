package greencity.validator;

import greencity.annotations.ValidTimeRange;
import greencity.constant.ErrorMessage;
import greencity.dto.event.EventDateLocationDto;
import greencity.exception.exceptions.InvalidEventDateException;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.ZonedDateTime;

public class TimeRangeValidator implements ConstraintValidator<ValidTimeRange, EventDateLocationDto> {
    @Override
    public void initialize(ValidTimeRange constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(EventDateLocationDto dto, ConstraintValidatorContext constraintValidatorContext) {
        if (dto == null) {
            return true;
        }

        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime start = dto.getStartDateTime();
        ZonedDateTime end = dto.getEndDateTime();

        if (start == null) {
            throw new InvalidEventDateException(ErrorMessage.EVENT_START_DATE_IS_NULL);
        }

        if (end == null) {
            throw new InvalidEventDateException(ErrorMessage.EVENT_END_DATE_IS_NULL);
        }

        if (!start.isAfter(now)) {
            throw new InvalidEventDateException(ErrorMessage.EVENT_START_MUST_BE_IN_FUTURE);
        }

        if (!end.isAfter(start)) {
            throw new InvalidEventDateException(ErrorMessage.EVENT_END_BEFORE_START);
        }

        return true;
    }
}
