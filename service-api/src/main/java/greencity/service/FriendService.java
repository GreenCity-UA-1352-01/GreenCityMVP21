package greencity.service;

import greencity.dto.PageableDto;
import greencity.dto.friend.SearchFriendDtoResponse;
import greencity.dto.user.UserVO;
import org.springframework.data.domain.Pageable;

public interface FriendService {
    /**
     * Method to search for users that are not friends of current user yet.
     *
     * @param name               ordered letters sequence of name of user to search
     * @param isTheSameCity      if true, limits the search to users from the same city as the requester
     * @param isFriendsOfFriends if true, limits the search to users who are friends of the requester's friends
     * @param user               current user
     *
     * @return PageableDto with SearchFriendDtoResponse objects
     */
    PageableDto<SearchFriendDtoResponse> searchNewFriends(String name,
                                                          Boolean isTheSameCity,
                                                          Boolean isFriendsOfFriends,
                                                          UserVO user,
                                                          Pageable pageable);
}
