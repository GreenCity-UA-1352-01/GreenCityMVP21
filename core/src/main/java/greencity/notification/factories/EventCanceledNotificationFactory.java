package greencity.notification.factories;

import greencity.annotations.NotificationHandler;
import greencity.controller.EventController;
import greencity.dto.event.EventVO;
import greencity.dto.notification.NotificationRequestDto;
import greencity.dto.user.UserVO;
import greencity.enums.NotificationObjectType;
import greencity.enums.NotificationOrigin;
import greencity.enums.NotificationType;
import greencity.notification.CommentDateTimeFormatter;
import greencity.notification.NotificationEventFactory;
import greencity.service.EventService;
import java.util.HashSet;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import java.lang.reflect.Method;
import java.time.ZonedDateTime;
import java.util.List;

@Component
@AllArgsConstructor
@NotificationHandler
public class EventCanceledNotificationFactory implements NotificationEventFactory {
    private final EventService eventService;

    @Override
    public boolean supports(Method method) {
        Class<?>[] expectedParameterTypes = {Long.class, UserVO.class, String.class};
        try {
            Method joinPointMethod = EventController.class
                    .getDeclaredMethod("cancelEvent", expectedParameterTypes);
            return joinPointMethod.equals(method);
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public NotificationRequestDto createEvent(Object[] args) {
        Long eventId = (Long) args[0];
        UserVO initiator = (UserVO) args[1];

        ZonedDateTime creationDate = ZonedDateTime.now();
        EventVO event = eventService.findById(eventId);
        String title = event.getTitle().length() > 20 ? event.getTitle().substring(0, 17) + "..." : event.getTitle();

        String action = "Unfortunately event %s was cancelled. %s".formatted(title,
            CommentDateTimeFormatter.format(creationDate));
        List<Long> receivers = eventService.findAttendersIdByEventId(eventId);

        return NotificationRequestDto.builder()
            .action(action)
            .objectName(event.getTitle())
            .creationDate(ZonedDateTime.now())
            .receiverIds(new HashSet<>(receivers))
            .initiatorId(initiator.getId())
            .objectType(NotificationObjectType.EVENT)
            .notificationType(NotificationType.EVENT_CANCEL)
            .origin(NotificationOrigin.GREEN_CITY)
            .build();
    }
}
