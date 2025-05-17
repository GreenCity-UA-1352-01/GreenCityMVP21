package greencity.dto.notification;

import greencity.enums.NotificationObjectType;
import greencity.enums.NotificationType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.ZonedDateTime;
import greencity.enums.NotificationOrigin;
import java.util.Set;
import lombok.*;

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

    @NotEmpty
    private Set<@Min(1) Long> receiverIds;

    @NotNull
    @Min(1)
    private Long initiatorId;

    @NotNull
    private NotificationOrigin origin;
}
