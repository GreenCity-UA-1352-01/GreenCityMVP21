package greencity.dto.notification;

import java.util.HashSet;
import java.util.Set;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class NotificationsGroupedResponseDto extends BaseNotificationResponseDto {
    @Builder.Default
    private Set<Long> ids = new HashSet<>();
    @Builder.Default
    private Set<Long> initiatorIds = new HashSet<>();
    @Builder.Default
    private Set<String> initiatorNames = new HashSet<>();
}
