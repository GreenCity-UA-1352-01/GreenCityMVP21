package greencity.notification;

import greencity.dto.notification.NotificationRequestDto;
import greencity.service.NotificationService;
import lombok.AllArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class NotificationListener {
    private final NotificationService notificationService;

    /**
     * Handles published notifications and saves them in the database.
     *
     * @param notificationRequestDto notification DTO for creation
     * @see NotificationPublisher
     * @author Roman Diakov & Rostyslav Zadyraichuk
     */
    @EventListener
    public void handleNotification(NotificationRequestDto notificationRequestDto) {
        notificationService.createNotifications(notificationRequestDto);
    }
}
