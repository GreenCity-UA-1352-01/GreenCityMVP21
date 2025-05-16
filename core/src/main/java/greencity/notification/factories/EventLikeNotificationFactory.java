package greencity.notification.factories;

import greencity.annotations.NotificationHandler;
import greencity.controller.EventController;
import greencity.dto.event.EventVO;
import greencity.dto.notification.NotificationRequestDto;
import greencity.dto.user.UserVO;
import greencity.enums.NotificationOrigin;
import greencity.enums.NotificationStatus;
import greencity.notification.NotificationEventFactory;

import java.lang.reflect.Method;
import java.time.ZonedDateTime;
import java.util.Arrays;

import greencity.service.EventService;
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


        return NotificationRequestDto.builder()
                .action("likes")
                .objectName(event.getTitle())
                .objectLink("/events/" + event.getId())
                .creationDate(ZonedDateTime.now())
                .status(NotificationStatus.UNREAD)
                .receiverId(event.getInitiator().getId())
                .initiatorId(user.getId())
                .origin(NotificationOrigin.GREEN_CITY)
                .build();
    }
}