package greencity.notification.factories;

import greencity.annotations.NotificationHandler;
import greencity.controller.EventController;
import greencity.dto.notification.NotificationRequestDto;
import greencity.dto.user.UserVO;
import greencity.dto.event.UpdateEventDtoRequest;

import java.lang.reflect.Method;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import greencity.enums.NotificationStatus;
import greencity.notification.NotificationEventFactory;
import org.springframework.stereotype.Component;

@Component
@NotificationHandler
public class EventUpdateNotificationFactory implements NotificationEventFactory {

    @Override
    public boolean supports(Method method) {
        Class<?>[] expectedParameterTypes = {
                greencity.dto.event.UpdateEventDtoRequest.class,
                java.util.List.class,
                greencity.dto.user.UserVO.class
        };

        return method.getDeclaringClass().equals(EventController.class)
                && method.getName().equals("update")
                && Arrays.equals(expectedParameterTypes, method.getParameterTypes());
    }

    @Override
    public NotificationRequestDto createEvent(Object[] args) {
        UpdateEventDtoRequest updatedEvent = (UpdateEventDtoRequest) args[0];
        UserVO user = (UserVO) args[2];

        String formattedDate = ZonedDateTime.now()
                .format(DateTimeFormatter.ofPattern("MMM dd, yyyy hh:mm a"));

        String message = "Event " + updatedEvent.getTitle() + " was updated. " + formattedDate;

        return NotificationRequestDto.builder()
                .action("event-update")
                .objectName(message)
                .creationDate(ZonedDateTime.now())
                .status(NotificationStatus.UNREAD)
                .receiverId(user.getId())
                .initiatorId(user.getId())
                .build();
    }
}