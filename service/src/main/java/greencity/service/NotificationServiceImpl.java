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
import greencity.dto.PageableDto;
import greencity.dto.notification.NotificationRequestDto;
import greencity.dto.notification.NotificationResponseDto;
import greencity.entity.Notification;
import greencity.entity.NotificationCounter;
import greencity.entity.User;
import greencity.enums.NotificationOrigin;
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
     *
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

    @Override
    @Transactional
    public void deleteEventLikeNotification(Long initiatorId,
                                            Long receiverId,
                                            Long id) {
        String eventObjectLink = "/events/" + id;
        if (notificationsRepo.existsLikeNotification(initiatorId, receiverId, eventObjectLink)) {
            notificationsRepo.deleteByInitiatorIdAndReceiverIdAndActionAndObjectLink(
                    initiatorId,
                    receiverId,
                    "likes",
                    eventObjectLink
            );
            decrementCounter(receiverId);
        }
    }



    @Override
    @Transactional
    public void deleteCommentLikeNotification(Long initiatorId,
                                              Long receiverId,
                                              Long id) {
        String commentObjectLink = "/comments/" + id;
        if (notificationsRepo.existsLikeNotification(initiatorId, receiverId, commentObjectLink)) {
            notificationsRepo.deleteByInitiatorIdAndReceiverIdAndActionAndObjectLink(
                    initiatorId,
                    receiverId,
                    "likes",
                    commentObjectLink
            );
            decrementCounter(receiverId);
        }
    }


    @Override
    @Transactional
    public void deleteHabitLikeNotification(Long initiatorId,
                                            Long receiverId,
                                            Long habitId) {
        String objectLink = "/habit/" + habitId;
        if (notificationsRepo.existsLikeNotification(initiatorId, receiverId, objectLink)) {
            notificationsRepo.deleteByInitiatorIdAndReceiverIdAndActionAndObjectLink(
                initiatorId,
                receiverId,
                "likes",
                objectLink
            );
            decrementCounter(receiverId);
        }
    }

    /**
     * Returns all notifications for specified user id and notification origin.
     * Origin can be null, then notifications from all origins will be returned.
     *
     * @param userId   user id
     * @param origin   notification origin
     * @param pageable page request
     * @return notification response dto with pagination
     * @author Marian Shtangret
     * @author Rostyslav Zadyraichuk
     */
    @Override
    public PageableDto<NotificationResponseDto> getAllNotificationsForUser(Long userId,
                                                                           NotificationOrigin origin,
                                                                           Pageable pageable) {
        Page<Notification> notifications = origin == null
            ? notificationsRepo.findNotificationsForUser(userId, pageable)
            : notificationsRepo.findNotificationsForUser(userId, origin, pageable);
        return new PageableDto<>(notifications.stream()
                .map(notificationResponseDtoMapper::convert)
                .toList(),
            notifications.getTotalElements(),
            notifications.getNumber(),
            notifications.getTotalPages()
        );
    }

    @Transactional
    public void deleteLikeNewsNotificationIfExists(Long initiatorId, Long receiverId, String objectLink) {
        boolean exists = notificationsRepo.existsLikeNotification(initiatorId, receiverId, objectLink);
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