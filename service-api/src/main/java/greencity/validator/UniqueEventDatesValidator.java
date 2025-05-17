package greencity.validator;

import greencity.annotations.UniqueEventDates;
import greencity.constant.ErrorMessage;
import greencity.dto.event.CreateEventDto;
import greencity.dto.event.EventDateLocationDto;
import greencity.exception.exceptions.InvalidEventDateException;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class UniqueEventDatesValidator implements ConstraintValidator<UniqueEventDates, CreateEventDto> {
    @Override
    public boolean isValid(CreateEventDto dto, ConstraintValidatorContext context) {
        if (dto.getDates() == null) {
            return true;
        }

        Set<LocalDate> uniqueDates = new HashSet<>();
        for (EventDateLocationDto dateDto : dto.getDates()) {
            if (dateDto != null && dateDto.getStartDateTime() != null) {
                LocalDate date = dateDto.getStartDateTime().toLocalDate();
                if (!uniqueDates.add(date)) {
                    throw new InvalidEventDateException(ErrorMessage.THE_SAME_DATES);
                }
            }
        }
        return true;
    }
}