package greencity.mapping;

import greencity.dto.friend.EcoFriendsResponse;
import greencity.entity.User;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class EcoFriendsResponseMapper {
    public EcoFriendsResponse convert(User user, int mutualFriendsCount) {
        return EcoFriendsResponse.builder()
                .id(user.getId())
                .name(user.getFirstName())
                .city(user.getCity())
                .rating(user.getRating())
                .profilePicturePath(user.getProfilePicturePath())
                .isOnline(user.getLastActivityTime() != null &&
                        user.getLastActivityTime().isAfter(LocalDateTime.now().minusMinutes(5)))
                .mutualFriends(mutualFriendsCount)
                .build();
    }
}
