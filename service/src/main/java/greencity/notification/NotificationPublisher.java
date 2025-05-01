package greencity.notification;

import greencity.dto.notification.NotificationRequestDto;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class NotificationPublisher {
    private final ApplicationEventPublisher publisher;

    public void publish(NotificationRequestDto event) {
        if (event == null) {
            throw new IllegalArgumentException("Notification event must not be null");
        }
        publisher.publishEvent(event);
    }
}
