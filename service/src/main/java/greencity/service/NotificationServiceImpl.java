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

        notificationCounterRepo.findById(dto.getReceiverId()).ifPresentOrElse(notificationCounter ->
                notificationCounter.setCountOfNotifications(notificationCounter.getCountOfNotifications() + 1),
            () -> notificationCounterRepo.save(NotificationCounter.builder()
                .countOfNotifications(1)
                .user(receiver)
                .build())
        );
        return notificationResponseDtoMapper.convert(notification);
    }

    @Override
    @Transactional
    public void deleteEventLikeNotification(Long initiatorId,
                                       Long receiverId,
                                       Long id) {
        String eventObjectLink = "/events/" + id;
        if(notificationsRepo.existsLikeNotification(initiatorId, receiverId, eventObjectLink)) {
            notificationsRepo.deleteByInitiatorIdAndReceiverIdAndActionAndObjectLink(
                    initiatorId,
                    receiverId,
                    "likes",
                    eventObjectLink
            );
            return;
        }

        String commentObjectLink = "/comments/" + id;
        if(notificationsRepo.existsLikeNotification(initiatorId, receiverId, commentObjectLink)) {
            notificationsRepo.deleteByInitiatorIdAndReceiverIdAndActionAndObjectLink(
                    initiatorId,
                    receiverId,
                    "likes",
                    commentObjectLink
            );
        }
    }

    @Override
    @Transactional
    public void deleteHabitLikeNotification(Long initiatorId,
                                            Long receiverId,
                                            Long habitId) {
        String objectLink = "/habit/" + habitId;
        if(notificationsRepo.existsLikeNotification(initiatorId, receiverId, objectLink)) {
            notificationsRepo.deleteByInitiatorIdAndReceiverIdAndActionAndObjectLink(
                    initiatorId,
                    receiverId,
                    "likes",
                    objectLink
            );
        }
    }
}