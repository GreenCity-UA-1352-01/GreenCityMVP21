package greencity.dto.eventdatetime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventDateTimeLocationResponseDto {
    private Long id;
    private ZonedDateTime startDateTime;
    private ZonedDateTime endDateTime;
    private String location;
    private String link;
}
