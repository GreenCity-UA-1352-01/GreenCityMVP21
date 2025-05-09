package greencity.service;

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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepo notificationsRepo;
    private final NotificationCounterRepo notificationCounterRepo;
    private final NotificationMapper notificationMapper;
    private final NotificationResponseDtoMapper notificationResponseDtoMapper;

    @Override
    @Transactional
    public NotificationResponseDto createNotification(NotificationRequestDto dto) {
        Notification notification = notificationMapper.convert(dto);
        notification = notificationsRepo.save(notification);
        final User receiver = notification.getReceiver();

        notificationCounterRepo.findById(dto.getReceiverId()).ifPresentOrElse(notificationCounter ->
                notificationCounter.setCountOfNotifications(notificationCounter.getCountOfNotifications() + 1),
            () -> notificationCounterRepo.save(NotificationCounter.builder()
                .countOfNotifications(1)
                .user(receiver)
                .build())
        );
        return notificationResponseDtoMapper.convert(notification);
    }

    @Transactional
    public void deleteLikeNewsNotificationIfExists(Long initiatorId, Long receiverId, String objectLink) {
        boolean exists = notificationsRepo.existsByUsersAndLink(initiatorId, receiverId, objectLink);
        if (exists) {
            notificationsRepo.deleteByUsersAndLink(
                    initiatorId, receiverId, objectLink
            );
            decrementCounter(receiverId);
        }
    }
    private void decrementCounter(Long receiverId) {
        NotificationCounter counter = notificationCounterRepo.findById(receiverId)
                .orElse(null);
        if (counter != null && counter.getCountOfNotifications() > 0) {
            counter.setCountOfNotifications(counter.getCountOfNotifications() - 1);
            notificationCounterRepo.save(counter);
        }
    }
}