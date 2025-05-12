package greencity.dto.notification;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@EqualsAndHashCode(of = "initiatorId", callSuper = false)
public class NotificationForGroupingDto extends BaseNotificationResponseDto {
    private Long id;
    private Long initiatorId;
    private String initiatorName;
}
