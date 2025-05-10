package greencity.mapping;

import greencity.dto.notification.NotificationResponseDto;
import greencity.entity.Notification;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
public class NotificationResponseDtoMapper extends AbstractConverter<Notification, NotificationResponseDto> {
    @Override
    public NotificationResponseDto convert(Notification notification) {
        return NotificationResponseDto.builder()
            .id(notification.getId())
            .action(notification.getAction())
            .actionSource(notification.getActionSource())
            .objectId(notification.getObjectId())
            .objectName(notification.getObjectName())
            .objectLink(notification.getObjectLink())
            .creationDate(notification.getCreationDate())
            .status(notification.getStatus())
            .receiverId(notification.getReceiver().getId())
            .initiatorId(notification.getInitiator().getId())
            .build();
    }
}