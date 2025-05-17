package greencity.dto.event;


import greencity.dto.eventdatetime.EventDateTimeLocationResponseDto;
import greencity.dto.eventimage.EventImageResponseDto;
import greencity.dto.tag.TagVO;
import lombok.*;

import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateEventDtoResponse {
    private Long id;
    private String title;
    private String description;

    private List<EventDateTimeLocationResponseDto> dateTimes;
    private EventImageResponseDto mainImage;
    private List<EventImageResponseDto> eventImages;

    private Set<TagVO> tags;
    private boolean isOpen;

}
