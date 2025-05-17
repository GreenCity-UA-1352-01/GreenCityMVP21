package greencity.notification.factories;

import greencity.annotations.NotificationHandler;
import greencity.controller.FriendController;
import greencity.dto.notification.NotificationRequestDto;
import greencity.dto.user.UserVO;
import greencity.enums.NotificationObjectType;
import greencity.enums.NotificationOrigin;
import greencity.enums.NotificationType;
import greencity.notification.NotificationDateTimeFormatter;
import greencity.notification.NotificationEventFactory;
import greencity.service.UserService;
import java.util.Set;
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
                .formatted(acceptor.getName(), NotificationDateTimeFormatter.format(creationDate));

        return NotificationRequestDto.builder()
                .action(action)
                .objectId(sender.getId())
                .objectName("Friendship")
                .creationDate(creationDate)
                .receiverIds(Set.of(acceptor.getId()))
                .initiatorId(sender.getId())
                .objectType(NotificationObjectType.USER)
                .notificationType(NotificationType.FRIENDSHIP_REQUEST_ACCEPT)
                .origin(NotificationOrigin.GREEN_CITY)
                .build();
    }
}
