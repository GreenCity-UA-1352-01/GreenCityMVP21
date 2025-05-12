package greencity.mapping;

import greencity.dto.notification.NotificationForGroupingDto;
import greencity.entity.Notification;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
public class NotificationForGroupingDtoMapper extends AbstractConverter<Notification, NotificationForGroupingDto> {
    @Override
    public NotificationForGroupingDto convert(Notification notification) {
        NotificationForGroupingDto dto = NotificationForGroupingDto.builder()
            .id(notification.getId())
            .action(notification.getAction())
            .objectId(notification.getObjectId())
            .objectName(notification.getObjectName())
            .objectType(notification.getObjectType())
            .creationDate(notification.getCreationDate())
            .status(notification.getStatus())
            .receiverId(notification.getReceiver().getId())
            .initiatorId(notification.getInitiator().getId())
            .initiatorName(notification.getInitiator().getName())
            .notificationType(notification.getNotificationType())
            .build();
        dto.setObjectLink(dto.getObjectType().getLinkBuilder().apply(dto));
        return dto;
    }
}