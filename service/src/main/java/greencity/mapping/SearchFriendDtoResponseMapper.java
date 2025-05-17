package greencity.mapping;

import greencity.dto.friend.SearchFriendDtoResponse;
import greencity.projection.UserWithMutualFriendsProjection;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
public class SearchFriendDtoResponseMapper extends
        AbstractConverter<UserWithMutualFriendsProjection, SearchFriendDtoResponse> {
    @Override
    public SearchFriendDtoResponse convert(UserWithMutualFriendsProjection projection) {
        return SearchFriendDtoResponse.builder()
            .id(projection.getId())
            .name(projection.getFirstName() + ' ' + projection.getName())
            .picture(projection.getProfilePicture())
            .city(projection.getCity())
            .rating(projection.getRating())
            .mutualFriends(projection.getMutualFriendsCount())
            .build();
    }
}
