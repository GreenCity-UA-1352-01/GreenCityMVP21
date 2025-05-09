package greencity.service;

import greencity.dto.PageableDto;
import greencity.dto.notification.NotificationRequestDto;
import greencity.dto.notification.NotificationResponseDto;
import org.springframework.data.domain.Pageable;

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
     * Method to retrieve all notifications for a specific user with pagination.
     *
     * @param userId   the id of the user.
     * @param pageable the pagination information.
     * @return a pageable dto containing the notifications.
     * @author Marian Shtangret
     */
    PageableDto<NotificationResponseDto> getAllNotificationsForUser(Long userId, Pageable pageable);
}
