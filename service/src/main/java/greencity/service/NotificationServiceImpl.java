package greencity.service;

import greencity.dto.PageableDto;
import greencity.dto.notification.NotificationRequestDto;
import greencity.dto.notification.NotificationResponseDto;
import greencity.entity.Notification;
import greencity.entity.NotificationCounter;
import greencity.entity.User;
import greencity.mapping.NotificationMapper;
import greencity.mapping.NotificationResponseDtoMapper;
import greencity.repository.NotificationCounterRepo;
import greencity.repository.NotificationRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepo notificationsRepo;
    private final NotificationCounterRepo notificationCounterRepo;
    private final NotificationMapper notificationMapper;
    private final NotificationResponseDtoMapper notificationResponseDtoMapper;

    /**
     * {@inheritDoc}
     * If notification counter for receiver is not present in the database, creates new one with count of
     * notifications set to 1.
     * If notification counter for receiver is present in the database, increments count of notifications by 1.
     * @param dto notification data transfer object
     * @return created notification data transfer object
     * @author Roman Diakov
     * @author Rostyslav Zadyraichuk
     */
    @Override
    @Transactional
    public NotificationResponseDto createNotification(NotificationRequestDto dto) {
        Notification notification = notificationMapper.convert(dto);
        notification = notificationsRepo.save(notification);
        final User receiver = notification.getReceiver();

        notificationCounterRepo.findById(dto.getReceiverId()).ifPresentOrElse(
            notificationCounter ->
                notificationCounter.setCountOfNotifications(notificationCounter.getCountOfNotifications() + 1),
            () -> {
                NotificationCounter newNotificationCounter = NotificationCounter.builder()
                    .countOfNotifications(1)
                    .user(receiver)
                    .build();
                notificationCounterRepo.save(newNotificationCounter);
            }
        );
        return notificationResponseDtoMapper.convert(notification);
    }

    @Override
    public PageableDto<NotificationResponseDto> getAllNotificationsForUser(Long userId, Pageable pageable) {
        Page<Notification> notifications = notificationsRepo.findNotificationsForUser(userId, pageable);
        return new PageableDto<>(notifications.stream()
                .map(notificationResponseDtoMapper::convert)
                .toList(),
            notifications.getTotalElements(),
            notifications.getNumber(),
            notifications.getTotalPages());
    }
}