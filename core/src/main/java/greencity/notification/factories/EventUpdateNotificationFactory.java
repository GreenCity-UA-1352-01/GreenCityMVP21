package greencity.notification.factories;

import greencity.annotations.NotificationHandler;
import greencity.controller.EventController;
import greencity.dto.notification.NotificationRequestDto;
import greencity.dto.user.UserVO;
import greencity.dto.event.UpdateEventDtoRequest;
import greencity.enums.NotificationObjectType;
import greencity.enums.NotificationOrigin;
import greencity.enums.NotificationType;
import greencity.notification.NotificationDateTimeFormatter;
import greencity.service.EventService;
import java.lang.reflect.Method;
import java.time.ZonedDateTime;
import java.util.Arrays;
import greencity.notification.NotificationEventFactory;
import java.util.HashSet;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@NotificationHandler
@AllArgsConstructor
public class EventUpdateNotificationFactory implements NotificationEventFactory {
    private EventService eventService;

    @Override
    public boolean supports(Method method) {
        Class<?>[] expectedParameterTypes = {
            UpdateEventDtoRequest.class,
            List.class,
            UserVO.class
        };

        return method.getDeclaringClass().equals(EventController.class)
                && method.getName().equals("update")
                && Arrays.equals(expectedParameterTypes, method.getParameterTypes());
    }

    @Override
    public NotificationRequestDto createEvent(Object[] args) {
        UpdateEventDtoRequest updatedEvent = (UpdateEventDtoRequest) args[0];
        List<Long> attenders = eventService.findAttendersIdByEventId(updatedEvent.getId());
        UserVO user = (UserVO) args[2];

        ZonedDateTime creationDate = ZonedDateTime.now();
        String formattedDate = NotificationDateTimeFormatter.format(creationDate);

        String message = "Event " + updatedEvent.getTitle() + " was updated. " + formattedDate;

        return NotificationRequestDto.builder()
                .action(message)
                .objectId(updatedEvent.getId())
                .objectName(updatedEvent.getTitle())
                .creationDate(creationDate)
                .receiverIds(new HashSet<>(attenders))
                .initiatorId(user.getId())
                .objectType(NotificationObjectType.EVENT)
                .notificationType(NotificationType.EVENT_UPDATE_NAME)
                .origin(NotificationOrigin.GREEN_CITY)
                .build();
    }
}