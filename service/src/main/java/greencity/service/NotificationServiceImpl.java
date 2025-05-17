package greencity.service;

import greencity.constant.ErrorMessage;
import greencity.dto.notification.BaseNotificationResponseDto;
import greencity.dto.notification.NotificationRequestDto;
import greencity.dto.notification.NotificationResponseDto;
import greencity.dto.notification.UpdateNotificationStatusRequestDto;
import greencity.dto.user.UserVO;
import greencity.entity.Notification;
import greencity.enums.NotificationObjectType;
import greencity.enums.NotificationStatus;
import greencity.exception.exceptions.InvalidStatusException;
import greencity.exception.exceptions.NotFoundException;
import greencity.entity.NotificationCounter;
import greencity.entity.User;
import org.modelmapper.ModelMapper;
import greencity.mapping.NotificationMapper;
import greencity.mapping.NotificationResponseDtoMapper;
import greencity.repository.NotificationCounterRepo;
import greencity.repository.NotificationReceiverRepo;
import greencity.repository.NotificationRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import greencity.entity.NotificationCounter;
import greencity.entity.NotificationReceiver;
import greencity.enums.NotificationType;
import greencity.enums.NotificationOrigin;
import greencity.mapping.NotificationMapper;
import greencity.mapping.NotificationResponseDtoMapper;
import greencity.mapping.NotificationsGroupedResponseDtoMapper;
import java.util.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepo notificationsRepo;
    private final NotificationCounterRepo notificationCounterRepo;
    private final NotificationReceiverRepo notificationReceiverRepo;
    private final NotificationMapper notificationMapper;
    private final NotificationResponseDtoMapper notificationResponseDtoMapper;
    private final NotificationsGroupedResponseDtoMapper notificationsGroupedResponseDtoMapper;

    /**
     * {@inheritDoc}
     * If notification counter for receiver is not present in the database, creates new one with count of
     * notifications set to 1.
     * If notification counter for receiver is present in the database, increments count of notifications by 1.
     *
     * @param dto notification data transfer object
     * @return list of created notifications
     * @author Roman Diakov
     * @author Rostyslav Zadyraichuk
     */
    @Override
    @Transactional
    public List<NotificationResponseDto> createNotifications(NotificationRequestDto dto) {
        Notification notification = notificationMapper.convert(dto);
        notification = notificationsRepo.save(notification);
        List<NotificationReceiver> receivers = notification.getNotificationReceivers();
//
//        receivers.forEach(nr -> {
//            notificationCounterRepo.findById(nr.getId()).ifPresentOrElse(notificationCounter ->
//                    notificationCounter.setCountOfNotifications(notificationCounter.getCountOfNotifications() + 1),
//                () -> {
//                    NotificationCounter newNotificationCounter = NotificationCounter.builder()
//                        .countOfNotifications(1)
//                        .user(nr.getReceiver())
//                        .build();
//                    notificationCounterRepo.save(newNotificationCounter);
//                });
//        });

        receivers.forEach(receiver -> {
            notificationCounterRepo.findById(receiver.getId()).ifPresentOrElse(notificationCounter ->
                    notificationCounter.setCountOfNotifications(notificationCounter.getCountOfNotifications() + 1),
                () -> notificationCounterRepo.save(NotificationCounter.builder()
                    .countOfNotifications(1)
                    .user(receiver.getReceiver())
                    .build())
            );
        });

        return notificationResponseDtoMapper.convert(notification);
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
     * @param user    the currently authenticated user
     * @throws NotFoundException      if the notification receiver with the given ID does not exist
     * @throws InvalidStatusException if the status cannot be converted to a {@link NotificationStatus}
     */
    @Override
    public void updateNotificationStatus(UpdateNotificationStatusRequestDto request,
                                         UserVO user) {
//        Notification notification = notificationsRepo.findById(request.getId())
//            .orElseThrow(() -> new NotFoundException("Notification with ID " + request.getId() + " not found."));
//        List<NotificationReceiver> receivers = notification.getNotificationReceivers();
//
//        if (!Objects.equals(notification.getReceiver().getId(), user.getId())) {
//            throw new BadRequestException(
//                "Notification with ID " + request.getId() + " does not belong to user with ID " + user.getId() + ".");
//        }
//
//        NotificationStatus enumStatus = NotificationStatus.valueOf(request.getStatus().toUpperCase());
//
//        notification.setStatus(enumStatus);
//        notificationsRepo.save(notification);
        NotificationReceiver notificationReceiver = notificationReceiverRepo
            .findNotificationReceiver(request.getId(), user.getId())
            .orElseThrow(() -> new NotFoundException(ErrorMessage.NOTIFICATION_RECEIVER_NOT_FOUND + request.getId()));

        notificationReceiver.setStatus(convertStatus(request.getStatus()));
        notificationReceiverRepo.save(notificationReceiver);
    }

    private NotificationStatus convertStatus(String status) {
        try {
            return NotificationStatus.valueOf(status);
        } catch (IllegalArgumentException e) {
            throw new InvalidStatusException(ErrorMessage.INVALID_ORIGIN + status);
        }
    }

    @Override
    @Transactional
    public void deleteEventLikeNotification(Long initiatorId,
                                            Long receiverId,
                                            Long id) {
        if (notificationsRepo.existsLikeNotification(initiatorId, receiverId, NotificationType.EVENT_LIKE,
            NotificationObjectType.EVENT, id)) {
            notificationsRepo.deleteLikeNotification(
                initiatorId,
                receiverId,
                NotificationType.EVENT_LIKE,
                NotificationObjectType.EVENT,
                id
            );
            decrementCounter(receiverId);
        }
    }

    /**
     * Retrieves all notifications for a specific user. If user has no notifications, returns an empty set.
     *
     * <p>
     * The notifications are grouped by notification type and object id and sorted by creation date,
     * type, object id, and status.
     *
     * @param userId the id of the user.
     * @return a set containing the notifications.
     * @author Marian Shtangret
     * @author Rostyslav Zadyraichuk
     */
    @Override
    public Set<BaseNotificationResponseDto> getAllNotificationsForUser(Long userId, NotificationOrigin origin) {
        List<Notification> notifications = notificationsRepo.findNotificationsForUser(userId, origin);
        if (notifications.isEmpty()) {
            return Collections.emptySet();
        }

        Map<NotificationGroupKey, List<NotificationResponseDto>> groupedNotifications = new HashMap<>();
        var result = new TreeSet<>(Comparator
            .comparing(BaseNotificationResponseDto::getCreationDate, Comparator.reverseOrder())
            .thenComparing(BaseNotificationResponseDto::getNotificationType)
            .thenComparing(BaseNotificationResponseDto::getObjectId)
            .thenComparing(BaseNotificationResponseDto::getStatus)
        );

        for (Notification notification : notifications) {
            List<NotificationResponseDto> dtos = notificationResponseDtoMapper.convert(notification);
            NotificationGroupKey key = new NotificationGroupKey(
                notification.getNotificationType(),
                notification.getObjectId()
            );
            if (key.getType().isGroupable()) {
                groupedNotifications
                    .computeIfAbsent(key, k -> new ArrayList<>())
                    .addAll(dtos);
            } else {
                result.addAll(dtos);
            }
        }

        for (var entry : groupedNotifications.entrySet()) {
            result.add(notificationsGroupedResponseDtoMapper.convert(entry.getValue()));
        }

        return result;
    }

    @Transactional
    public void deleteCommentLikeNotification(Long initiatorId,
                                              Long receiverId,
                                              NotificationObjectType objectType,
                                              Long objectId) {
        if (notificationsRepo.existsLikeNotification(initiatorId, receiverId, NotificationType.COMMENT_LIKE,
                objectType, objectId)) {
            notificationsRepo.deleteLikeNotification(
                initiatorId,
                receiverId,
                NotificationType.COMMENT_LIKE,
                objectType,
                objectId
            );
            decrementCounter(receiverId);
        }
    }


    @Override
    @Transactional
    public void deleteHabitLikeNotification(Long initiatorId,
                                            Long receiverId,
                                            Long habitId) {
        if (notificationsRepo.existsLikeNotification(initiatorId, receiverId, NotificationType.HABIT_LIKE,
            NotificationObjectType.HABIT, habitId)) {
            notificationsRepo.deleteLikeNotification(
                initiatorId,
                receiverId,
                NotificationType.HABIT_LIKE,
                NotificationObjectType.HABIT,
                habitId
            );
            decrementCounter(receiverId);
        }
    }

    @Transactional
    public void deleteLikeNewsNotificationIfExists(Long initiatorId, Long receiverId, Long newsId) {
        boolean exists = notificationsRepo.existsLikeNotification(initiatorId, receiverId,
            NotificationType.ECO_NEWS_LIKE, NotificationObjectType.ECO_NEWS, newsId);
        if (exists) {
            notificationsRepo.existsLikeNotification(
                initiatorId, receiverId, NotificationType.ECO_NEWS_LIKE, NotificationObjectType.ECO_NEWS, newsId
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

    @AllArgsConstructor
    @Getter
    @EqualsAndHashCode
    private static class NotificationGroupKey {
        private final NotificationType type;
        private final Long objectId;
    }
}