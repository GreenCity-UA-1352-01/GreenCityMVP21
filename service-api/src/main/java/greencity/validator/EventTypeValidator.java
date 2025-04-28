//package greencity.validator;
//
//import greencity.annotations.ValidEventType;
//import greencity.dto.event.CreateEventDto;
//import jakarta.validation.ConstraintValidator;
//import jakarta.validation.ConstraintValidatorContext;
//
//public class EventTypeValidator implements ConstraintValidator<ValidEventType, CreateEventDto> {
//    @Override
//    public void initialize(ValidEventType constraintAnnotation) {
//
//    }
//
//    @Override
//    public boolean isValid(CreateEventDto dto, ConstraintValidatorContext context) {
//        return dto != null && (Boolean.TRUE.equals(dto.getPlace()) || Boolean.TRUE.equals(dto.getOnline()));
//    }
//}
