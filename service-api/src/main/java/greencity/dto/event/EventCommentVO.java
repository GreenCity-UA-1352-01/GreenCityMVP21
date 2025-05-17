package greencity.dto.event;

import greencity.dto.user.UserVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventCommentVO {
    private Long id;
    private String text;
    private LocalDateTime createdDate;
    private UserVO user;
}
