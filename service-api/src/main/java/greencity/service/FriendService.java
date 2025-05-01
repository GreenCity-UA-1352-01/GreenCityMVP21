package greencity.service;

import greencity.dto.friend.EcoFriendProfileDto;
import greencity.dto.friend.EcoFriendsResponse;

import java.util.List;

public interface FriendService {
    List<EcoFriendsResponse> getAllFriendsForUser(Long userId);

    EcoFriendProfileDto getFriendProfile(Long userId, Long friendId);
}
