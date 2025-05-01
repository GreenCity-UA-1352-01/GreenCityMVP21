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

    @EventListener
    public void handleNotification(NotificationRequestDto notificationRequestDto) {
        notificationService.createNotification(notificationRequestDto);
    }
}
