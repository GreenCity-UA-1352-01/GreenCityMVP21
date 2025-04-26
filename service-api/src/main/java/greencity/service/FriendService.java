package greencity.service;

import greencity.dto.friend.FriendCardDtoResponse;

import java.util.List;

public interface FriendService {
    List<FriendCardDtoResponse> getAllFriendsForUser(Long userId);

}
