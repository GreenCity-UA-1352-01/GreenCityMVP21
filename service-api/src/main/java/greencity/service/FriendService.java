package greencity.service;

import greencity.dto.PageableDto;
import greencity.dto.friend.EcoFriendProfileDto;
import greencity.dto.friend.EcoFriendsResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface FriendService {
    PageableDto<EcoFriendsResponse> getAllFriendsForUser(Long userId, Pageable pageable);

    EcoFriendProfileDto getFriendProfile(Long userId, Long friendId);
}
