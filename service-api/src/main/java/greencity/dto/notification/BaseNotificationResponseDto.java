package greencity.dto.notification;

import greencity.enums.NotificationObjectType;
import greencity.enums.NotificationOrigin;
import greencity.enums.NotificationStatus;
import greencity.enums.NotificationType;
import java.time.ZonedDateTime;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@EqualsAndHashCode
public abstract class BaseNotificationResponseDto {
    private String action;
    private Long objectId;
    private String objectName;
    private String objectLink;
    private NotificationObjectType objectType;
    private ZonedDateTime creationDate;
    private NotificationStatus status;
    private NotificationType notificationType;
    private NotificationOrigin origin;
    private Long receiverId;
    private Long commentId;
}
