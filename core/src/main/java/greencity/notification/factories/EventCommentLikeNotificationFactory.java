package greencity.notification.factories;

import greencity.annotations.NotificationHandler;
import greencity.controller.EventCommentController;
import greencity.dto.event.EventCommentVO;
import greencity.dto.notification.NotificationRequestDto;
import greencity.dto.user.UserVO;
import greencity.enums.NotificationObjectType;
import greencity.enums.NotificationOrigin;
import greencity.enums.NotificationType;
import greencity.notification.CommentDateTimeFormatter;
import greencity.notification.NotificationEventFactory;
import greencity.service.EventCommentService;
import java.util.Set;
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

        ZonedDateTime creationDate = ZonedDateTime.now();
        String action = "%s liked your comment. %s".formatted(user.getName(),
            CommentDateTimeFormatter.format(creationDate));

        return NotificationRequestDto.builder()
                .action(action)
                .objectName("Comment " + comment.getId())
                .creationDate(ZonedDateTime.now())
                .receiverIds(Set.of(comment.getUser().getId()))
                .initiatorId(user.getId())
                .objectType(NotificationObjectType.EVENT)
                .notificationType(NotificationType.COMMENT_LIKE)
                .origin(NotificationOrigin.GREEN_CITY)
                .build();
    }
}
