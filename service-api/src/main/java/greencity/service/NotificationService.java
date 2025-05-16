package greencity.service;


import greencity.dto.user.UserVO;
import greencity.dto.PageableDto;
import greencity.dto.notification.NotificationRequestDto;
import greencity.dto.notification.NotificationResponseDto;
import greencity.enums.NotificationOrigin;
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
  
    void updateNotificationStatus(UpdateNotificationStatusRequestDto updateNotificationStatusRequestDto, UserVO user);

    void deleteEventLikeNotification(Long initiatorId,
                                     Long receiverId,
                                     Long eventId);

    void deleteHabitLikeNotification(Long initiatorId,
                                     Long receiverId,
                                     Long habitId);

    void deleteCommentLikeNotification(Long initiatorId,
                            Long receiverId,
                            Long eventId);
  
    /**
     * Retrieves all notifications for a specific user.
     *
     * @param userId   the user's id to retrieve notifications for.
     * @param origin   the origin of the notifications to filter by.
     * @param pageable the pagination information.
     * @return a page of notification response DTOs.
     * @author Marian Shtangret
     * @author Rostyslav Zadyraichuk
     */
    PageableDto<NotificationResponseDto> getAllNotificationsForUser(Long userId,
                                                                    NotificationOrigin origin,
                                                                    Pageable pageable);

    void deleteLikeNewsNotificationIfExists(Long initiatorId, Long receiverId, String objectLink);
}
