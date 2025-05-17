package greencity.dto.friend;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EcoFriendsResponse {

    private Long id;
    private String name;
    private String city;
    private Double rating;
    private String profilePicturePath;
    private Integer mutualFriends;
    private Boolean isOnline;
}
