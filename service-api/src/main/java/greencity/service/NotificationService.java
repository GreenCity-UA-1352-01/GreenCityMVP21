package greencity.service;

import greencity.dto.notification.NotificationRequestDto;
import greencity.dto.notification.NotificationResponseDto;
import java.util.List;

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

    List<NotificationResponseDto> getAllNotificationsForUser(Long userId);
}
