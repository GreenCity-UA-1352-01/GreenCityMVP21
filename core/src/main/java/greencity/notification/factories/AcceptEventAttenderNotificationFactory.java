package greencity.notification.factories;

import greencity.annotations.NotificationHandler;
import greencity.controller.EventController;
import greencity.dto.event.EventVO;
import greencity.dto.notification.NotificationRequestDto;
import greencity.dto.user.UserVO;
import greencity.enums.NotificationOrigin;
import greencity.enums.NotificationStatus;
import greencity.notification.CommentDateTimeFormatter;
import greencity.notification.NotificationEventFactory;
import greencity.service.EventService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.ZonedDateTime;
@Component
@AllArgsConstructor
@NotificationHandler
public class AcceptEventAttenderNotificationFactory implements NotificationEventFactory {
    private final EventService eventService;
    @Override
    public boolean supports(Method method) {
        Class<?>[] expectedParameterTypes = {Long.class, Long.class, UserVO.class};
        try {
            Method joinPointMethod = EventController.class
                    .getDeclaredMethod("acceptAttenderToEvent", expectedParameterTypes);
            return joinPointMethod.equals(method);
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public NotificationRequestDto createEvent(Object[] args) {
        Long eventId = (Long) args[0];
        Long userId = (Long) args[1];
        UserVO initiator = (UserVO) args[2];

        ZonedDateTime creationDate = ZonedDateTime.now();
        EventVO event = eventService.findById(eventId);
        String title = event.getTitle().length() > 20 ? event.getTitle().substring(0, 17) + "..." : event.getTitle();

        String action = "Initiator accepted you to %s event. %s".formatted(title, CommentDateTimeFormatter.format(creationDate));
        return NotificationRequestDto.builder()
                .action(action)
                .objectName("Event")
                .objectLink("/events/" + event.getId())
                .creationDate(ZonedDateTime.now())
                .status(NotificationStatus.UNREAD)
                .receiverId(userId)
                .initiatorId(initiator.getId())
                .origin(NotificationOrigin.GREEN_CITY)
                .build();
    }
}
