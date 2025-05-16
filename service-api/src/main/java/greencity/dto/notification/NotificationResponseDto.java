package greencity.dto.notification;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import greencity.enums.NotificationOrigin;
import greencity.enums.NotificationStatus;
import java.time.ZonedDateTime;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode
public class NotificationResponseDto {
    Long id;
    private String action;
    private String objectName;
    private String objectLink;
    private ZonedDateTime creationDate;
    private NotificationStatus status;
    private Long receiverId;
    private Long initiatorId;
    private NotificationOrigin origin;
}
