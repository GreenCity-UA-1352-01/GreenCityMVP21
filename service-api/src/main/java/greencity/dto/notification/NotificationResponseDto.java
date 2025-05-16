package greencity.dto.notification;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import greencity.enums.NotificationOrigin;
import greencity.enums.NotificationStatus;
import java.time.ZonedDateTime;
import lombok.*;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class NotificationResponseDto extends BaseNotificationResponseDto {
    private Long id;
    private Long initiatorId;
    private String initiatorName;
}
