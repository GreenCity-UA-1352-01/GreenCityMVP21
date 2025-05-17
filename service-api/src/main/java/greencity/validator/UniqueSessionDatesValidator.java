package greencity.validator;

import greencity.annotations.UniqueSessionDates;
import greencity.dto.eventdatetime.EventDateTimeLocationRequestDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UniqueSessionDatesValidator
        implements ConstraintValidator<UniqueSessionDates, List<EventDateTimeLocationRequestDto>> {
    @Override
    public boolean isValid(List<EventDateTimeLocationRequestDto> dtoList, ConstraintValidatorContext context) {
        if (dtoList == null) {
            return true;
        }

        Set<LocalDate> uniqueDates = new HashSet<>();

        for (EventDateTimeLocationRequestDto dto : dtoList) {
            if (dto.getStartDateTime() == null) {
                continue; // skip nulls, let @NotNull handle them
            }

            LocalDate date = dto.getStartDateTime().toLocalDate();

            if (!uniqueDates.add(date)) {
                return false; // duplicate date found
            }
        }

        return true;
    }
}
