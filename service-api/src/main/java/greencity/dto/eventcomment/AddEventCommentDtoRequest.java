package greencity.dto.eventcomment;

import greencity.annotations.ValidComment;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class AddEventCommentDtoRequest {
    @ValidComment
    private String text;
    @NotNull
    private Long parentCommentId;
}
