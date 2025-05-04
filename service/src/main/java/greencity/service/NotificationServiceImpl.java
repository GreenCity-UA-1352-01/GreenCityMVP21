package greencity.service;

import greencity.dto.notification.NotificationRequestDto;
import greencity.dto.notification.NotificationResponseDto;
import greencity.dto.notification.UpdateNotificationStatusRequestDto;
import greencity.enums.NotificationStatus;
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.NotificationCounterRepo;
import greencity.repository.NotificationRepo;
import jakarta.transaction.Transactional;
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

    /**
     * Updates the status of a notification by its ID.
     *
     * @param id     the ID of the notification to update
     * @param status the new status to set (must match one of the values in {@link NotificationStatus})
     * @throws NotFoundException if no notification with the given ID was found
     * @throws IllegalArgumentException if the provided status is not a valid {@link NotificationStatus} value
     */
    @Override
    @Transactional
    public void updateNotificationStatus(Long id, String status) {
        NotificationStatus enumStatus = NotificationStatus.valueOf(status.toUpperCase());
        int updated = notificationsRepo.updateStatusById(id, enumStatus);

        if (updated == 0) {
            throw new NotFoundException("Notification with ID " + id + " not found.");
        }
    }
}