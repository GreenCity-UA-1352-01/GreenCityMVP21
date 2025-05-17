package greencity.dto.event;

import greencity.annotations.ValidLocationOrOnlineLink;
import greencity.annotations.ValidTimeRange;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import java.time.ZonedDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
@ToString
@ValidTimeRange
@ValidLocationOrOnlineLink
public class EventDateLocationDto {
    private ZonedDateTime startDateTime;
    private ZonedDateTime endDateTime;
    private String location;
    @Pattern(
            regexp = "^(https?://).*",
            message = "Please add a link to the event. The link must start with http(s)://"
    )
    private String onlineLink;
}
