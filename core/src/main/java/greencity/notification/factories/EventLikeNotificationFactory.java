package greencity.notification.factories;

import greencity.annotations.NotificationHandler;
import greencity.controller.EventController;
import greencity.dto.event.EventVO;
import greencity.dto.notification.NotificationRequestDto;
import greencity.dto.user.UserVO;
import greencity.enums.NotificationObjectType;
import greencity.enums.NotificationOrigin;
import greencity.enums.NotificationType;
import greencity.notification.NotificationDateTimeFormatter;
import greencity.notification.NotificationEventFactory;
import java.lang.reflect.Method;
import java.time.ZonedDateTime;
import java.util.Arrays;
import greencity.service.EventService;
import java.util.Set;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@NotificationHandler
public class EventLikeNotificationFactory implements NotificationEventFactory {
    private final EventService eventService;

    @Override
    public boolean supports(Method method) {
        Class<?>[] expectedParameterTypes = {Long.class, UserVO.class};
        return method.getDeclaringClass().equals(EventController.class)
                && method.getName().equals("likeEvent")
                && Arrays.equals(expectedParameterTypes, method.getParameterTypes());
    }

    @Override
    public NotificationRequestDto createEvent(Object[] args) {
        Long id = (Long) args[0];
        UserVO user = (UserVO) args[1];
        EventVO event = eventService.findById(id);

        ZonedDateTime creationDate = ZonedDateTime.now();
        String title = event.getTitle().length() > 20
            ? event.getTitle().substring(0, 17) + "..."
            : event.getTitle();
        String action = "%s liked your event %s. %s".formatted(user.getName(), title,
            NotificationDateTimeFormatter.format(creationDate));

        return NotificationRequestDto.builder()
                .action(action)
                .objectName(event.getTitle())
                .creationDate(ZonedDateTime.now())
                .receiverIds(Set.of(event.getInitiator().getId()))
                .initiatorId(user.getId())
                .objectType(NotificationObjectType.EVENT)
                .notificationType(NotificationType.EVENT_LIKE)
                .origin(NotificationOrigin.GREEN_CITY)
                .build();
    }
}