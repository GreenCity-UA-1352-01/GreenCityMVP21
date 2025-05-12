package greencity.service;

import greencity.dto.notification.NotificationRequestDto;
import greencity.dto.notification.NotificationResponseDto;

public interface NotificationService {
    NotificationResponseDto createNotification(NotificationRequestDto dto);

    void deleteEventLikeNotification(Long initiatorId,
                                     Long receiverId,
                                     Long eventId);

    void deleteHabitLikeNotification(Long initiatorId,
                                     Long receiverId,
                                     Long habitId);
}
