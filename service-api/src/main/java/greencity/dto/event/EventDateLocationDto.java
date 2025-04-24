package greencity.dto.event;

import greencity.annotations.ValidTimeRange;
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
public class EventDateLocationDto {
    private ZonedDateTime startDateTime;
    private ZonedDateTime endDateTime;
    private String location;
    private String onlineLink;
    private boolean allDay;
}
