package greencity.dto.notification;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class NotificationResponseDto extends BaseNotificationResponseDto {
    private Long id;
    private Long initiatorId;
    private String initiatorName;
}
