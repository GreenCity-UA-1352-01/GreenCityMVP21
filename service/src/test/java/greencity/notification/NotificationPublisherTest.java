package greencity.notification;

import greencity.ModelUtils;
import greencity.dto.notification.NotificationRequestDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationPublisherTest {

    @Mock
    private ApplicationEventPublisher publisher;

    @InjectMocks
    private NotificationPublisher notificationPublisher;

    @Test
    void testPublish() {
        NotificationRequestDto event = ModelUtils.getNotificationRequestDto();

        notificationPublisher.publish(event);

        verify(publisher, times(1)).publishEvent(event);
    }

    @Test
    void testPublish_whenNull_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> notificationPublisher.publish(null));

        verifyNoInteractions(publisher);
    }
}