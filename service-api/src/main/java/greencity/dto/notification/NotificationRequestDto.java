package greencity.dto.notification;

import greencity.enums.NotificationObjectType;
import greencity.enums.NotificationType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.ZonedDateTime;
import java.util.List;
import java.time.ZonedDateTime;
import greencity.enums.NotificationOrigin;
import greencity.enums.NotificationStatus;
import greencity.enums.NotificationOrigin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.ZonedDateTime;

@AllArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode
public class NotificationRequestDto {
    @NotEmpty
    private String action;

    @NotNull
    @Min(1)
    private Long objectId;

    @NotEmpty
    private String objectName;

    @NotNull
    private NotificationObjectType objectType;

    @NotNull
    private ZonedDateTime creationDate;

    @NotNull
    private NotificationType notificationType;

    @NotNull
    @Min(1)
    private List<Long> receiverIds;

    @NotNull
    @Min(1)
    private Long initiatorId;

    @NotNull
    private NotificationOrigin origin;
}
