package greencity.mapping;

import greencity.dto.notification.NotificationForGroupingDto;
import greencity.dto.notification.NotificationsGroupedResponseDto;
import java.util.*;
import java.util.stream.Collectors;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
public class NotificationsGroupedResponseDtoMapper
    extends AbstractConverter<List<NotificationForGroupingDto>, NotificationsGroupedResponseDto> {
    @Override
    public NotificationsGroupedResponseDto convert(List<NotificationForGroupingDto> notifications) {
        if (notifications.isEmpty()) {
            throw new IllegalArgumentException("No notifications to group");
        }
        var sortedNotifications = new TreeSet<>(Comparator
            .comparing(NotificationForGroupingDto::getCreationDate)
            .thenComparing(NotificationForGroupingDto::getInitiatorId)
            .reversed());
        sortedNotifications.addAll(notifications);
        NotificationForGroupingDto notification = sortedNotifications.getFirst();

        NotificationsGroupedResponseDto dto = NotificationsGroupedResponseDto.builder()
            .ids(sortedNotifications.stream()
                .map(NotificationForGroupingDto::getId)
                .collect(Collectors.toSet()))
            .objectId(notification.getObjectId())
            .objectName(notification.getObjectName())
            .objectLink(notification.getObjectLink())
            .objectType(notification.getObjectType())
            .creationDate(notification.getCreationDate())
            .status(notification.getStatus())
            .notificationType(notification.getNotificationType())
            .initiatorIds(sortedNotifications.stream()
                .map(NotificationForGroupingDto::getInitiatorId)
                .collect(Collectors.toSet()))
            .initiatorNames(sortedNotifications.stream()
                .map(NotificationForGroupingDto::getInitiatorName)
                .collect(Collectors.toSet()))
            .receiverId(notification.getReceiverId())
            .build();
        dto.setAction(dto.getNotificationType().getGroupedMessageConverter().apply(dto));
        return dto;
    }
}
