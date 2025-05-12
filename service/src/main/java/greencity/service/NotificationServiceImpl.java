package greencity.service;

import greencity.dto.notification.BaseNotificationResponseDto;
import greencity.dto.notification.NotificationForGroupingDto;
import greencity.dto.notification.NotificationRequestDto;
import greencity.dto.notification.NotificationResponseDto;
import greencity.entity.Notification;
import greencity.entity.NotificationCounter;
import greencity.entity.User;
import greencity.enums.NotificationStatus;
import greencity.enums.NotificationType;
import greencity.mapping.NotificationForGroupingDtoMapper;
import greencity.mapping.NotificationMapper;
import greencity.mapping.NotificationResponseDtoMapper;
import greencity.mapping.NotificationsGroupedResponseDtoMapper;
import greencity.repository.NotificationCounterRepo;
import greencity.repository.NotificationRepo;
import java.util.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
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
    private final NotificationForGroupingDtoMapper notificationForGroupingDtoMapper;
    private final NotificationsGroupedResponseDtoMapper notificationsGroupedResponseDtoMapper;

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
    public Set<BaseNotificationResponseDto> getAllNotificationsForUser(Long userId) {
        List<Notification> notifications = notificationsRepo.findNotificationsForUser(userId);
        Map<NotificationGroupKey, List<NotificationForGroupingDto>> groupedNotifications = new HashMap<>();
//        Map<NotificationType, Map<Long, Map<NotificationStatus, List<NotificationResponseDto>>>> groupedNotifications = new HashMap<>();
        var result = new TreeSet<>(Comparator
            .comparing(BaseNotificationResponseDto::getCreationDate)
            .thenComparing(BaseNotificationResponseDto::getNotificationType)
            .thenComparing(BaseNotificationResponseDto::getObjectId)
            .thenComparing(BaseNotificationResponseDto::getStatus)
            .reversed());

        for (Notification notification : notifications) {
            NotificationForGroupingDto dto = notificationForGroupingDtoMapper.convert(notification);
            NotificationGroupKey key = new NotificationGroupKey(
                notification.getNotificationType(),
                notification.getObjectId(),
                notification.getStatus()
            );
            if (key.getType().isGroupable()) {
                groupedNotifications
                    .computeIfAbsent(key, k -> new ArrayList<>())
//                    .computeIfAbsent(dto.getType(), t -> new HashMap<>())
//                    .computeIfAbsent(dto.getObjectId(), id -> new HashMap<>())
//                    .computeIfAbsent(dto.getStatus(), s -> new ArrayList<>())
                    .add(dto);
            } else {
                result.add(dto);
            }
        }

        for (var entry : groupedNotifications.entrySet()) {
            result.add(notificationsGroupedResponseDtoMapper.convert(entry.getValue()));
        }

        return result;
    }

    @AllArgsConstructor
    @Getter
    @EqualsAndHashCode
    private static class NotificationGroupKey {
        private final NotificationType type;
        private final Long objectId;
        private final NotificationStatus status;
    }
}