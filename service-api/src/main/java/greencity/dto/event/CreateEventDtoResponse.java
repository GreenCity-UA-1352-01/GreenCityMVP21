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
    private Boolean place;
    private Boolean online;
    private List<String> initiativeTypes;
    private List<TagUaEnDto> tags;
    private List<EventDateLocationDto> dates;
    private List<String> images;
    private ZonedDateTime createdDateTime;
}
