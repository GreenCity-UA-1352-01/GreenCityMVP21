package greencity.mapping;

import greencity.dto.friend.EcoFriendsResponse;
import greencity.entity.User;
import greencity.mapping.records.FriendWithMutuals;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Component
public class EcoFriendsResponseMapper extends AbstractConverter<FriendWithMutuals, EcoFriendsResponse> {
    @Override
    public EcoFriendsResponse convert(FriendWithMutuals source) {
        User user = source.user();
        return EcoFriendsResponse.builder()
                .id(user.getId())
                .name(user.getFirstName())
                .city(user.getCity())
                .rating(user.getRating())
                .profilePicturePath(user.getProfilePicturePath())
                .isOnline(user.getLastActivityTime() != null
                    && user.getLastActivityTime().isAfter(LocalDateTime.now().minusMinutes(5)))
                .mutualFriends(source.mutualFriends())
                .build();
    }
}
