package greencity.notification.factories;

import greencity.annotations.NotificationHandler;
import greencity.controller.EventCommentController;
import greencity.dto.event.EventVO;
import greencity.dto.eventcomment.AddEventCommentDtoRequest;
import greencity.dto.notification.NotificationRequestDto;
import greencity.dto.user.UserVO;
import greencity.enums.NotificationObjectType;
import greencity.enums.NotificationOrigin;
import greencity.enums.NotificationType;
import greencity.notification.NotificationDateTimeFormatter;
import greencity.notification.NotificationEventFactory;
import greencity.service.EventService;
import java.lang.reflect.Method;
import java.time.ZonedDateTime;
import java.util.Set;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@NotificationHandler
public class EventCommentedNotificationFactory implements NotificationEventFactory {
    private final EventService eventService;

    @Override
    public boolean supports(Method method) {
        Class<?>[] expectedParameterTypes = {Long.class, AddEventCommentDtoRequest.class, UserVO.class};
        try {
            Method joinPointMethod = EventCommentController.class
                .getDeclaredMethod("save", expectedParameterTypes);
            return joinPointMethod.equals(method);
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public NotificationRequestDto createEvent(Object[] args) {
        Long eventId = (Long) args[0];
        UserVO initiator = (UserVO) args[2];

        ZonedDateTime creationDate = ZonedDateTime.now();
        EventVO event = eventService.findById(eventId);
        String title = event.getTitle().length() > 20
            ? event.getTitle().substring(0, 17) + "..."
            : event.getTitle();
        String action = "%s commented on your event %s. %s".formatted(initiator.getName(), title,
            NotificationDateTimeFormatter.format(creationDate));

        return NotificationRequestDto.builder()
            .action(action)
            .objectId(event.getId())
            .objectName(event.getTitle())
            .creationDate(creationDate)
            .receiverIds(Set.of(event.getInitiator().getId()))
            .initiatorId(initiator.getId())
            .objectType(NotificationObjectType.EVENT)
            .notificationType(NotificationType.EVENT_COMMENT)
            .origin(NotificationOrigin.GREEN_CITY)
            .build();
    }
}
