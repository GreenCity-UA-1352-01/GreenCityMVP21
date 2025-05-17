package greencity.validator;

import greencity.annotations.ValidLocationOrOnlineLink;
import greencity.dto.event.EventDateLocationDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.util.StringUtils;

public class LocationOrOnlineValidator implements ConstraintValidator<ValidLocationOrOnlineLink, EventDateLocationDto> {
    @Override
    public boolean isValid(EventDateLocationDto dto, ConstraintValidatorContext context) {
        return StringUtils.hasText(dto.getLocation()) || StringUtils.hasText(dto.getOnlineLink());
    }
}
