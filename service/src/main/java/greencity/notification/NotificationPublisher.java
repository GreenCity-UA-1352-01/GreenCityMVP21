package greencity.notification;

import greencity.dto.notification.NotificationRequestDto;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class NotificationPublisher {
    private final ApplicationEventPublisher publisher;

    /**
     * Publishes a notification event, that can be handled by a listener.
     *
     * @param event the notification event to publish, must not be null
     * @throws IllegalArgumentException if the event is null
     * @author Roman Diakov & Rostyslav Zadyraichuk
     * @see NotificationListener
     */
    public void publish(List<NotificationRequestDto> event) {
        if (event == null || event.isEmpty()) {
            throw new IllegalArgumentException("Notification event must not be null");
        }
        for (NotificationRequestDto notificationRequestDto : event) {
            publisher.publishEvent(notificationRequestDto);
        }
    }
}
