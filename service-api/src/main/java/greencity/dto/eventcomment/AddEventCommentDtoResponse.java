package greencity.dto.eventcomment;

import java.time.ZonedDateTime;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class AddEventCommentDtoResponse {
    private Long id;
    private EventCommentAuthorDto author;
    private String text;
    private ZonedDateTime modifiedDate;
}
