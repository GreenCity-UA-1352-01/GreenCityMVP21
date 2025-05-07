package greencity.service;

import greencity.dto.notification.NotificationRequestDto;
import greencity.dto.notification.NotificationResponseDto;
import greencity.repository.NotificationCounterRepo;
import greencity.repository.NotificationRepo;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepo notificationsRepo;
    private final NotificationCounterRepo notificationCounterRepo;
    private final ModelMapper modelMapper;

    @Override
    public NotificationResponseDto createNotification(NotificationRequestDto dto) {
        return null;
    }
}