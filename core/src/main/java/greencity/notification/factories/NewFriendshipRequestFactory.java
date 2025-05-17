package greencity.notification.factories;

import greencity.annotations.NotificationHandler;
import greencity.controller.FriendController;
import greencity.dto.notification.NotificationRequestDto;
import greencity.dto.user.UserVO;
import greencity.enums.NotificationStatus;
import greencity.notification.NotificationDateTimeFormatter;
import greencity.notification.NotificationEventFactory;
import greencity.service.UserService;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.lang.reflect.Method;
import java.time.ZonedDateTime;
import java.util.Arrays;

@Component
@NotificationHandler
@RequiredArgsConstructor
public class NewFriendshipRequestFactory implements NotificationEventFactory {
    private final UserService userService;

    @Override
    public boolean supports(Method method) {
        Class<?>[] expectedParameterTypes = {Long.class, UserVO.class};
        return method.getDeclaringClass().equals(FriendController.class)
                && method.getName().equals("addFriend")
                && Arrays.equals(expectedParameterTypes, method.getParameterTypes());
    }

    @Override
    public NotificationRequestDto createEvent(Object[] args) {
        Long friendId = (Long) args[0];

        UserVO sender = (UserVO) args[1];
        UserVO acceptor =  userService.findById(friendId);

        String action = "%s sent you a friend request. %s"
                .formatted(sender.getName(), NotificationDateTimeFormatter.format(ZonedDateTime.now()));

        return NotificationRequestDto.builder()
                .action(action)
                .objectName("Friendship")
                .creationDate(ZonedDateTime.now())
                .receiverIds(Set.of(acceptor.getId()))
                .initiatorId(sender.getId())
                .build();
    }
}
