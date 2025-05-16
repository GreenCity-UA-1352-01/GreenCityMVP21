package greencity.service;

import greencity.dto.notification.BaseNotificationResponseDto;
import greencity.dto.notification.NotificationRequestDto;
import greencity.dto.notification.NotificationResponseDto;
import java.util.List;
import java.util.Set;
import greencity.dto.user.UserVO;
import greencity.dto.PageableDto;
import greencity.dto.notification.NotificationRequestDto;
import greencity.dto.notification.NotificationResponseDto;
import greencity.enums.NotificationOrigin;
import org.springframework.data.domain.Pageable;

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
     * Method to retrieve all notifications for a specific user.
     *
     * @param userId   the id of the user.
     * @return a set containing the notifications.
     * @author Marian Shtangret
     * @author Rostyslav Zadyraichuk
     */
    Set<BaseNotificationResponseDto> getAllNotificationsForUser(Long userId);

    void deleteLikeNewsNotificationIfExists(Long initiatorId, Long receiverId, String objectLink);
}
