package greencity.service;

import greencity.dto.notification.BaseNotificationResponseDto;
import greencity.dto.notification.NotificationRequestDto;
import greencity.dto.notification.NotificationResponseDto;
import java.util.Set;

public interface NotificationService {
    /**
     * Creates a notification.
     *
     * @param dto a request DTO which contains information about a notification
     * @return a response DTO which contains information about a created notification
     * @author Roman Diakov
     * @author Rostyslav Zadyraichuk
     */
    NotificationResponseDto createNotification(NotificationRequestDto dto);

    /**
     * Method to retrieve all notifications for a specific user.
     *
     * @param userId   the id of the user.
     * @return a set containing the notifications.
     * @author Marian Shtangret
     */
    Set<BaseNotificationResponseDto> getAllNotificationsForUser(Long userId);
}
