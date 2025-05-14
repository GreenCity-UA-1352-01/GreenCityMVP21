package greencity.mapping;

import greencity.dto.notification.NotificationResponseDto;
import greencity.entity.Notification;
import java.util.ArrayList;
import java.util.List;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
public class NotificationResponseDtoMapper extends AbstractConverter<Notification, List<NotificationResponseDto>> {
    @Override
    public List<NotificationResponseDto> convert(Notification notification) {
        List<NotificationResponseDto> dtos = new ArrayList<>();
        notification.getNotificationReceivers().forEach(nr -> {
            NotificationResponseDto dto = NotificationResponseDto.builder()
                .id(notification.getId())
                .action(notification.getAction())
                .objectId(notification.getObjectId())
                .objectName(notification.getObjectName())
                .objectType(notification.getObjectType())
                .creationDate(notification.getCreationDate())
                .status(nr.getStatus())
                .notificationType(notification.getNotificationType())
                .receiverId(nr.getReceiver().getId())
                .initiatorId(notification.getInitiator().getId())
                .initiatorName(notification.getInitiator().getName())
                .build();
            dto.setObjectLink(dto.getObjectType().getLinkBuilder().apply(dto));
            dtos.add(dto);
        });
        return dtos;
    }
}