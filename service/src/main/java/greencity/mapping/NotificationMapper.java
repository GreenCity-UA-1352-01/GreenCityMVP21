package greencity.mapping;

import greencity.constant.ErrorMessage;
import greencity.dto.notification.NotificationRequestDto;
import greencity.entity.Notification;
import greencity.entity.NotificationReceiver;
import greencity.entity.User;
import greencity.enums.NotificationStatus;
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.UserRepo;
import java.util.List;
import lombok.AllArgsConstructor;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class NotificationMapper extends AbstractConverter<NotificationRequestDto, Notification> {
    private final UserRepo userRepo;

    @Override
    public Notification convert(NotificationRequestDto dto) {
        User initiator = userRepo.findById(dto.getInitiatorId())
            .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_ID + dto.getInitiatorId()));
        List<User> receivers = userRepo.findAllById(dto.getReceiverIds());

        Notification notification = Notification.builder()
            .action(dto.getAction())
            .objectId(dto.getObjectId())
            .objectName(dto.getObjectName())
            .objectType(dto.getObjectType())
            .creationDate(dto.getCreationDate())
            .notificationType(dto.getNotificationType())
            .initiator(initiator)
            .origin(dto.getOrigin())
            .commentId(dto.getCommentId())
            .build();
        List<NotificationReceiver> notificationReceivers = receivers.stream()
            .map(receiver -> NotificationReceiver.builder()
                .receiver(receiver)
                .notification(notification)
                .status(NotificationStatus.UNREAD)
                .build())
            .toList();
        notification.setNotificationReceivers(notificationReceivers);

        return notification;
    }
}
