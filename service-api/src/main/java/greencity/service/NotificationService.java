package greencity.service;

import greencity.dto.notification.NotificationRequestDto;
import greencity.dto.notification.NotificationResponseDto;

public interface NotificationService {
    NotificationResponseDto createNotification(NotificationRequestDto dto);

    void deleteLikeNotification(Long initiatorId,
                            Long receiverId,
                            Long eventId);
}
