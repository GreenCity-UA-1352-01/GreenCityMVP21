package greencity.dto.friend;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@EqualsAndHashCode
public class SearchFriendDtoResponse {
    private Long id;
    private String name;
    private String picture;
    private String city;
    private Double rating;
    private Integer mutualFriends;
}
