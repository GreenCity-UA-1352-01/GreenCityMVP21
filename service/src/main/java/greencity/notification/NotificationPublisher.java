package greencity.notification;

import greencity.dto.notification.NotificationRequestDto;
import jakarta.validation.Valid;
import java.util.Set;
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
     *
     * @param notification the notification to be published
     * @throws IllegalArgumentException if the event is null
     * @author Roman Diakov & Rostyslav Zadyraichuk
     * @see NotificationListener
     */
    public void publish(@Valid NotificationRequestDto notification) {
        if (notification != null) {
            notification.setReceiverIds(notification.getReceiverIds().stream()
                .filter(id -> !id.equals(notification.getInitiatorId()))
                .collect(Collectors.toSet())
            );
            publisher.publishEvent(notification);
        }
    }
}
