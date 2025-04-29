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
        if (dto == null) return true;

        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime start = dto.getStartDateTime();
        ZonedDateTime end = dto.getEndDateTime();
        boolean allDay = dto.isAllDay();

        if (start == null) {
            throw new InvalidEventDateException(ErrorMessage.EVENT_START_DATE_IS_NULL);
        }

        if (!allDay && end == null) {
            throw new InvalidEventDateException(ErrorMessage.EVENT_END_DATE_IS_NULL);
        }

        if (allDay) {
            if (start.toLocalDate().isBefore(now.toLocalDate())) {
                throw new InvalidEventDateException(ErrorMessage.EVENT_ALL_DAY_START_IN_PAST);
            }
        } else {
            if (start.toLocalDate().isEqual(now.toLocalDate()) && start.isBefore(now)) {
                throw new InvalidEventDateException(ErrorMessage.EVENT_START_MUST_BE_IN_FUTURE);
            }

            if (end.isBefore(start)) {
                throw new InvalidEventDateException(ErrorMessage.EVENT_END_BEFORE_START);
            }
        }

        return true;
    }
}
