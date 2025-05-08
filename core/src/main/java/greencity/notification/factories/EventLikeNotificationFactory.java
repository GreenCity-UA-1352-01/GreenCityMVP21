package greencity.notification.factories;


import greencity.dto.event.EventVO;
import greencity.dto.notification.NotificationRequestDto;
import greencity.dto.user.UserVO;
import greencity.enums.NotificationOrigin;
import greencity.enums.NotificationStatus;
import greencity.notification.NotificationEventFactory;
import greencity.service.EventService;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.ZonedDateTime;

@Component
public class EventLikeNotificationFactory implements NotificationEventFactory {
    private final EventService eventService;

    public EventLikeNotificationFactory(EventService eventService) {
        this.eventService = eventService;
    }

    @Override
    public NotificationRequestDto createEvent(Object[] args) {
        Long eventId = (Long) args[0];
        UserVO user = (UserVO) args[1];

        EventVO eventVO = eventService.findById(eventId);


        return NotificationRequestDto.builder()
                .action("likes")
                .objectName(eventVO.getTitle())
                .objectLink("/events/" + eventVO.getId())
                .creationDate(ZonedDateTime.now())
                .status(NotificationStatus.UNREAD)
                .receiverId(eventVO.getInitiator().getId())
                .initiatorId(user.getId())
                .origin(NotificationOrigin.GREEN_CITY)
                .build();
    }

    @Override
    public boolean supports(Method method) {
        return false;
    }
}
