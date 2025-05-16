package greencity.notifications;

import greencity.controller.FriendController;
import greencity.dto.notification.NotificationRequestDto;
import greencity.dto.user.UserVO;
import greencity.enums.NotificationStatus;
import greencity.notification.factories.FriendAcceptedNotificationFactory;
import greencity.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FriendAcceptedNotificationFactoryTest {
    @Mock
    private UserService userService;

    @InjectMocks
    private FriendAcceptedNotificationFactory factory;

    @Test
    void testSupports_shouldReturnTrueForCorrectMethod() throws NoSuchMethodException {
        Method method = FriendController.class.getMethod("acceptFriendRequest", Long.class, UserVO.class);

        boolean result = factory.supports(method);
        assertTrue(result);
    }

    @Test
    void testSupports_shouldReturnFalseForWrongMethod() throws NoSuchMethodException {
        Method method = String.class.getMethod("substring", int.class);

        boolean result = factory.supports(method);
        assertFalse(result);
    }

    @Test
    void testCreateEvent_shouldReturnNotificationRequestDto() {
        Long friendId = 1L;
        Long acceptorId = 2L;

        UserVO sender = UserVO.builder()
                .id(friendId)
                .name("Anastasia")
                .build();

        UserVO acceptor = UserVO.builder()
                .id(acceptorId)
                .name("Anna")
                .build();

        when(userService.findById(friendId)).thenReturn(sender);

        NotificationRequestDto result = factory.createEvent(new Object[]{friendId, acceptor});

        assertNotNull(result);
        assertEquals("Friendship", result.getObjectName());
        assertEquals("/friends/friend/" + acceptorId, result.getObjectLink());
        assertEquals(NotificationStatus.UNREAD, result.getStatus());
        assertEquals(friendId, result.getReceiverId());
        assertEquals(acceptorId, result.getInitiatorId());
        assertTrue(result.getAction().contains("Anna accepted your friend request"));
    }
}
