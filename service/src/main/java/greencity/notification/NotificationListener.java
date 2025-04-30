package greencity.notification;

import greencity.dto.notification.NotificationRequestDto;
import greencity.service.NotificationService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class NotificationListener {
    private final NotificationService notificationService;

    public NotificationListener(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @EventListener
    public void handleNotification(NotificationRequestDto notificationRequestDto) {
        notificationService.createNotification(notificationRequestDto);
    }
}
