package greencity.dto.event;

import greencity.dto.tag.TagUaEnDto;
import lombok.*;

import java.time.ZonedDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
@ToString
public class CreateEventDtoResponse {
    private Long eventId;
    private String title;
    private String description;
    private Boolean open;
    private List<String> tags;
    private List<EventDateLocationDto> dates;
    private List<String> images;
    private ZonedDateTime createdDateTime;
}
