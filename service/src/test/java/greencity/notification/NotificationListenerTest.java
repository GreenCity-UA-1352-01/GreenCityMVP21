package greencity.notification;

import greencity.ModelUtils;
import greencity.dto.notification.NotificationRequestDto;
import greencity.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationListenerTest {

    @Mock
    private NotificationService service;

    @InjectMocks
    private NotificationListener notificationListener;

    @Test
    void testHandleNotification() {
        NotificationRequestDto event = ModelUtils.getNotificationRequestDto();

        notificationListener.handleNotification(event);

        verify(service, times(1)).createNotifications(event);
    }

}