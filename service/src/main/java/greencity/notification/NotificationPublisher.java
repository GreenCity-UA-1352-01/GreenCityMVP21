package greencity.notification;

import greencity.dto.notification.NotificationRequestDto;
import jakarta.validation.Valid;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Component
@AllArgsConstructor
@Validated
public class NotificationPublisher {
    private final ApplicationEventPublisher publisher;

    /**
     * Publishes a notification event, that can be handled by a listener.
     * If notification is null, does nothing.
     *
     * @param notification the notification to be published
     * @author Roman Diakov & Rostyslav Zadyraichuk
     * @see NotificationListener
     */
    public void publish(@Valid NotificationRequestDto notification) {
        if (notification != null) {
            if (notification.getReceiverIds().contains(notification.getInitiatorId())) {
                notification.setReceiverIds(notification.getReceiverIds().stream()
                    .filter(id -> !id.equals(notification.getInitiatorId()))
                    .collect(Collectors.toSet())
                );
            }
            publisher.publishEvent(notification);
        }
    }
}
