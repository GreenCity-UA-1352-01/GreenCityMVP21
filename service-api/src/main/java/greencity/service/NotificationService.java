package greencity.service;

import greencity.dto.notification.BaseNotificationResponseDto;
import greencity.dto.notification.NotificationRequestDto;
import greencity.dto.notification.NotificationResponseDto;
import java.util.List;
import java.util.Set;

public interface NotificationService {
    /**
     * Creates a notification or notifications depending on the number of receivers.
     *
     * @param dto a request DTO which contains information about a notification
     * @return a list of created notifications
     * @author Roman Diakov
     * @author Rostyslav Zadyraichuk
     */
    List<NotificationResponseDto> createNotifications(NotificationRequestDto dto);

    /**
     * Method to retrieve all notifications for a specific user.
     *
     * @param userId   the id of the user.
     * @return a set containing the notifications.
     * @author Marian Shtangret
     */
    Set<BaseNotificationResponseDto> getAllNotificationsForUser(Long userId);
}
