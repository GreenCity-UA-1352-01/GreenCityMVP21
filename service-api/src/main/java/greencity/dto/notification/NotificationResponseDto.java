package greencity.dto.notification;

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
public class NotificationResponseDto {
    @NotNull
    @Min(1)
    Long id;

    @NotEmpty
    private String action;

    @NotEmpty
    private String objectName;

    @NotEmpty
    private ZonedDateTime creationDate;

    @NotEmpty
    private String status;

    @NotNull
    @Min(1)
    private Long receiverId;

    @NotNull
    @Min(1)
    private Long initiatorId;
}
