package greencity.service;

import greencity.dto.notification.NotificationRequestDto;
import greencity.dto.notification.NotificationResponseDto;
import java.util.List;

public interface NotificationService {
    NotificationResponseDto createNotification(NotificationRequestDto dto);

    List<NotificationResponseDto> getAllNotificationsForUser(Long userId);
}
