package greencity.dto.notification;

import greencity.enums.NotificationStatus;
import java.time.ZonedDateTime;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@AllArgsConstructor
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

    @NotEmpty
    private String initiatorUsername;
}
