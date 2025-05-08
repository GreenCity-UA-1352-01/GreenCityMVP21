package greencity.service;

import greencity.dto.PageableDto;
import greencity.dto.friend.EcoFriendProfileDto;
import greencity.dto.friend.EcoFriendsResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

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


    /**
     * Method to add a user friend.
     *
     * <p>
     * This method is idempotent, so if the friend is already added, then the method will do nothing.
     *
     * @param currentUserId the ID of the user who is adding a friend
     * @param friendId      the ID of the user to add as a friend
     */
    void addFriend(Long currentUserId, Long friendId);
    PageableDto<EcoFriendsResponse> getAllFriendsForUser(Long userId, Pageable pageable);

    EcoFriendProfileDto getFriendProfile(Long userId, Long friendId);
}
