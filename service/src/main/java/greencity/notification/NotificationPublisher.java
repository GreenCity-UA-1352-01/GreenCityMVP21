package greencity.notification;

import greencity.dto.notification.NotificationRequestDto;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class NotificationPublisher {
    private final ApplicationEventPublisher publisher;

    /**
     * Publishes a notification event, that can be handled by a listener.
     *
     * @param event the notification event to publish, must not be null
     * @throws IllegalArgumentException if the event is null
     * @see NotificationListener
     * @author Roman Diakov & Rostyslav Zadyraichuk
     */
    public void publish(NotificationRequestDto event) {
        if (event == null) {
            throw new IllegalArgumentException("Notification event must not be null");
        }

        if (!event.getReceiverId().equals(event.getInitiatorId())) {
            publisher.publishEvent(event);
        }
    }
}
