package greencity.dto.friend;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EcoFriendProfileDto {
    private Long id;
    private String name;
    private String profilePicturePath;
    private Double rating;
    private String userCredo;
    private Boolean isOnline;
    private Integer habitsInProgress;
    private Integer habitsAcquired;
    private Long newsPublished;
}
