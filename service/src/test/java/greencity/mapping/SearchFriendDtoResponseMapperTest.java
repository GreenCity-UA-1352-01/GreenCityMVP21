package greencity.mapping;

import static org.junit.jupiter.api.Assertions.*;
import greencity.ModelUtils;
import greencity.dto.friend.SearchFriendDtoResponse;
import greencity.projection.UserWithMutualFriendsProjection;
import org.junit.jupiter.api.Test;

class SearchFriendDtoResponseMapperTest {

    private static final SearchFriendDtoResponseMapper MAPPER;
    private static final UserWithMutualFriendsProjection FRIENDS_PROJECTION;
    private static final SearchFriendDtoResponse FRIEND_DTO;

    static {
        MAPPER = new SearchFriendDtoResponseMapper();
        FRIENDS_PROJECTION = ModelUtils.getUserWithMutualFriendsProjection();
        FRIEND_DTO = ModelUtils.getSearchFriendDtoResponse();
    }

    @Test
    void convert() {
        SearchFriendDtoResponse actual = MAPPER.convert(FRIENDS_PROJECTION);

        assertNotNull(actual);
        assertEquals(FRIEND_DTO, actual);
    }
}