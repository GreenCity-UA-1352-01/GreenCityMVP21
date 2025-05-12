package greencity.dto.notification;

import greencity.enums.NotificationStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.ZonedDateTime;
import lombok.*;

@AllArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode
public class NotificationRequestDto {
    @NotEmpty
    private String action;

    @NotEmpty
    private String objectName;

    private String objectLink;

    @NotNull
    private ZonedDateTime creationDate;

    @NotEmpty
    private NotificationStatus status;

    @NotNull
    @Min(1)
    private Long receiverId;

    @NotNull
    @Min(1)
    private Long initiatorId;
}
