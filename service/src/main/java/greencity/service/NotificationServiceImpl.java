package greencity.service;

import greencity.dto.notification.NotificationRequestDto;
import greencity.dto.notification.NotificationResponseDto;
import greencity.dto.notification.UpdateNotificationStatusRequestDto;
import greencity.dto.user.UserVO;
import greencity.entity.Notification;
import greencity.enums.NotificationStatus;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.NotificationCounterRepo;
import greencity.repository.NotificationRepo;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Objects;

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
     * Updates the status of a specific notification for the current user.
     *
     * <p>This method first checks whether the notification with the given ID exists and belongs
     * to the user making the request. If the notification does not exist, a {@link NotFoundException}
     * is thrown. If the notification does not belong to the user, an {@link IllegalArgumentException}
     * is thrown. The status is then updated to the specified value.</p>
     *
     * @param request the request DTO containing the notification ID and new status
     * @param user the currently authenticated user
     *
     * @throws NotFoundException if no notification with the given ID is found
     * @throws IllegalArgumentException if the notification does not belong to the provided user
     * @throws IllegalArgumentException if the status string is invalid and cannot be converted to {@link NotificationStatus}
     */
    @Override
    public void updateNotificationStatus(
            UpdateNotificationStatusRequestDto request,
            UserVO user
    ) {
        Notification notification = notificationsRepo.findById(request.getId())
                .orElseThrow(() -> new NotFoundException("Notification with ID " + request.getId() + " not found."));

        if (!Objects.equals(notification.getReceiver().getId(), user.getId())) {
            throw new BadRequestException("Notification with ID " + request.getId() + " does not belong to user with ID " + user.getId() + ".");
        }

        NotificationStatus enumStatus = NotificationStatus.valueOf(request.getStatus().toUpperCase());

        notification.setStatus(enumStatus);
        notificationsRepo.save(notification);
    }
}