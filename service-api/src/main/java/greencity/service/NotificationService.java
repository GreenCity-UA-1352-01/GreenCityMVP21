package greencity.service;

import greencity.dto.notification.NotificationRequestDto;
import greencity.dto.notification.NotificationResponseDto;
import greencity.dto.notification.UpdateNotificationStatusRequestDto;
import greencity.dto.user.UserVO;

public interface NotificationService {
    NotificationResponseDto createNotification(NotificationRequestDto dto);

    void updateNotificationStatus(UpdateNotificationStatusRequestDto updateNotificationStatusRequestDto, UserVO user);
}
