package greencity.dto.event;

import greencity.dto.tag.TagVO;
import greencity.dto.user.UserVO;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode(exclude = {"dateTimes", "mainImage", "eventImages", "tags"})
@ToString(exclude = {"dateTimes", "mainImage", "eventImages", "tags"})
public class EventVO {
    private Long id;
    private String title;
    @Builder.Default
    private List<EventDateLocationDto> dateTimes = new ArrayList<>();
    private String description;
    private EventImageDto mainImage;
    private UserVO initiator;
    @Builder.Default
    private List<EventImageDto> eventImages = new ArrayList<>();
    @Builder.Default
    private Set<TagVO> tags = new HashSet<>();
    @Builder.Default
    private boolean isOpen = true;
}
