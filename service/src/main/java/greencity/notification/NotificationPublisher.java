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
     * @param notifications list of notifications to be published
     * @throws IllegalArgumentException if the event is null
     * @author Roman Diakov & Rostyslav Zadyraichuk
     * @see NotificationListener
     */
    public void publish(List<NotificationRequestDto> notifications) {
        if (notifications == null || notifications.isEmpty()) {
            throw new IllegalArgumentException("Notification event must not be null");
        }

        notifications.stream()
            .filter(n -> !n.getReceiverId().equals(n.getInitiatorId()))
            .forEach(publisher::publishEvent);
    }
}
