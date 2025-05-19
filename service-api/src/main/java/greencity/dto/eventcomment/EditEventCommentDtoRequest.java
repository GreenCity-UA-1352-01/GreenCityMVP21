package greencity.dto.eventcomment;

import greencity.annotations.ValidComment;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class EditEventCommentDtoRequest {
    @ValidComment
    private String newText;
}
