package greencity.notification.factories;

import greencity.annotations.NotificationHandler;
import greencity.controller.EventCommentController;
import greencity.dto.event.EventCommentVO;
import greencity.dto.notification.NotificationRequestDto;
import greencity.dto.user.UserVO;
import greencity.enums.NotificationOrigin;
import greencity.enums.NotificationStatus;
import greencity.notification.NotificationEventFactory;
import greencity.service.EventCommentService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.ZonedDateTime;
import java.util.Arrays;

@Component
@AllArgsConstructor
@NotificationHandler
public class EventCommentLikeNotificationFactory implements NotificationEventFactory {
    private final EventCommentService eventCommentService;

    @Override
    public boolean supports(Method method) {
        Class<?>[] expectedParameterTypes = {Long.class, UserVO.class};
        return method.getDeclaringClass().equals(EventCommentController.class)
                && method.getName().equals("likeEventComment")
                && Arrays.equals(expectedParameterTypes, method.getParameterTypes());
    }

    @Override
    public NotificationRequestDto createEvent(Object[] args) {
        EventCommentVO comment = eventCommentService.findById((Long) args[0]);
        UserVO user = (UserVO) args[1];

        return NotificationRequestDto.builder()
                .action("likes")
                .objectName("Comment")
                .objectLink("/comments/" + comment.getId())
                .creationDate(ZonedDateTime.now())
                .status(NotificationStatus.UNREAD)
                .receiverId(comment.getUser().getId())
                .initiatorId(user.getId())
                .origin(NotificationOrigin.GREEN_CITY)
                .build();
    }
}
