package greencity.mapping;

import greencity.dto.notification.NotificationResponseDto;
import greencity.dto.notification.NotificationsGroupedResponseDto;
import greencity.enums.NotificationStatus;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
public class NotificationsGroupedResponseDtoMapper
    extends AbstractConverter<List<NotificationResponseDto>, NotificationsGroupedResponseDto> {
    @Override
    public NotificationsGroupedResponseDto convert(List<NotificationResponseDto> notifications) {
        Set<Long> ids = new HashSet<>();
        Set<Long> initiatorIds = new HashSet<>();
        Set<String> initiatorNames = new HashSet<>();
        AtomicBoolean isAtLeastOneUnread = new AtomicBoolean(false);

        notifications = notifications.stream()
            .sorted(Comparator
                .comparing(NotificationResponseDto::getInitiatorId)
                .thenComparing(NotificationResponseDto::getCreationDate, Comparator.reverseOrder()))
            .peek(notification -> {
                ids.add(notification.getId());
                initiatorIds.add(notification.getInitiatorId());
                initiatorNames.add(notification.getInitiatorName());
                if (notification.getStatus() == NotificationStatus.UNREAD) {
                    isAtLeastOneUnread.set(true);
                }
            })
            .distinct()
            .toList();

        NotificationResponseDto notification = notifications.getFirst();
        NotificationsGroupedResponseDto dto = NotificationsGroupedResponseDto.builder()
            .ids(ids)
            .objectId(notification.getObjectId())
            .objectName(notification.getObjectName())
            .objectLink(notification.getObjectLink())
            .objectType(notification.getObjectType())
            .creationDate(notification.getCreationDate())
            .status(isAtLeastOneUnread.get() ? NotificationStatus.UNREAD : NotificationStatus.READ)
            .notificationType(notification.getNotificationType())
            .initiatorIds(initiatorIds)
            .initiatorNames(initiatorNames)
            .receiverId(notification.getReceiverId())
            .build();
        dto.setAction(dto.getNotificationType().getGroupedMessageConverter().apply(dto));
        return dto;
    }
}
