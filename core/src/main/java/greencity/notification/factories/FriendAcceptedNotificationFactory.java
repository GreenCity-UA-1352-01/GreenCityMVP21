package greencity.notification.factories;

import greencity.annotations.NotificationHandler;
import greencity.controller.FriendController;
import greencity.dto.notification.NotificationRequestDto;
import greencity.dto.user.UserVO;
import greencity.enums.NotificationStatus;
import greencity.notification.CommentDateTimeFormatter;
import greencity.notification.NotificationEventFactory;
import greencity.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.ZonedDateTime;
import java.util.Arrays;

@Component
@AllArgsConstructor
@NotificationHandler
public class FriendAcceptedNotificationFactory implements NotificationEventFactory {
    private final UserService userService;

    @Override
    public boolean supports(Method method) {
        Class<?>[] expectedParameterTypes = {Long.class, UserVO.class};
        return method.getDeclaringClass().equals(FriendController.class)
                && method.getName().equals("acceptFriendRequest")
                && Arrays.equals(expectedParameterTypes, method.getParameterTypes());
    }

    @Override
    public NotificationRequestDto createEvent(Object[] args) {
        Long friendId = (Long) args[0];
        UserVO acceptor = (UserVO) args[1];

        UserVO sender = userService.findById(friendId);

        ZonedDateTime creationDate = ZonedDateTime.now();

        String action = "%s accepted your friend request. %s"
                .formatted(acceptor.getName(), CommentDateTimeFormatter.format(creationDate));

        return NotificationRequestDto.builder()
                .action(action)
                .objectName("Friendship")
                .objectLink("/friends/friend/" + acceptor.getId())
                .creationDate(creationDate)
                .status(NotificationStatus.UNREAD)
                .receiverId(sender.getId())
                .initiatorId(acceptor.getId())
                .build();
    }
}
