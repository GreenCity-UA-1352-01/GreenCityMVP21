package greencity.dto.eventcomment;

import java.time.LocalDateTime;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class EventCommentDtoResponse {
    private Long id;
    private EventCommentAuthorDto author;
    private String text;
    private LocalDateTime modifiedDate;
    private Boolean isModified;
    private Boolean isDeleted;
}
