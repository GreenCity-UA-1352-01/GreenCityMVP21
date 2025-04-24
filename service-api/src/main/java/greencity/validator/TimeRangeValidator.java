package greencity.validator;

import greencity.annotations.ValidTimeRange;
import greencity.dto.event.EventDateLocationDto;
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
        if (dto == null) return true;

        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime start = dto.getStartDateTime();
        ZonedDateTime end = dto.getEndDateTime();
        boolean allDay = dto.isAllDay();

        if (allDay) {
            if (start.toLocalDate().isBefore(now.toLocalDate())) {
                return false;
            }
            return true;
        }

        if (start.toLocalDate().isEqual(now.toLocalDate()) && start.isBefore(now)) {
            return false;
        }

        if (end != null && end.isBefore(start)) {
            return false;
        }

        return true;
    }
}
