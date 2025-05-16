package greencity.notification;

import greencity.ModelUtils;
import greencity.dto.notification.NotificationRequestDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationPublisherTest {

    @Mock
    private ApplicationEventPublisher publisher;

    @InjectMocks
    private NotificationPublisher notificationPublisher;

    @Test
    void testPublishWithList() {
        NotificationRequestDto event = ModelUtils.getNotificationRequestDto();
        List<NotificationRequestDto> eventList = List.of(event);

        notificationPublisher.publish(eventList);

        verify(publisher, times(1)).publishEvent(event);
    }

    @Test
    void testPublish_whenNull_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> notificationPublisher.publish(null));

        verifyNoInteractions(publisher);
    }
}