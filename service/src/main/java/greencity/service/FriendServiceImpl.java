package greencity.service;

import greencity.constant.AppConstant;
import greencity.dto.PageableDto;
import greencity.dto.friend.SearchFriendDtoResponse;
import greencity.dto.user.UserVO;
import greencity.exception.exceptions.BadRequestException;
import greencity.mapping.SearchFriendDtoResponseMapper;
import greencity.projection.UserWithMutualFriendsProjection;
import greencity.repository.FriendRepo;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class FriendServiceImpl implements FriendService {
    private final FriendRepo friendRepository;
    private final SearchFriendDtoResponseMapper searchFriendDtoResponseMapper;

    /**
     * {@inheritDoc}
     *
     * @param name               ordered letters sequence of name of user to search
     * @param isTheSameCity      if true, limits the search to users from the same city as the requester
     * @param isFriendsOfFriends if true, limits the search to users who are friends of the requester's friends
     * @param user               current user
     * @param pageable           pagination object
     *
     * @return PageableDto with SearchFriendDtoResponse objects
     *
     * @author Rostyslav Zadyraichuk
     */
    @Override
    public PageableDto<SearchFriendDtoResponse> searchNewFriends(String name,
                                                                 Boolean isTheSameCity,
                                                                 Boolean isFriendsOfFriends,
                                                                 UserVO user,
                                                                 Pageable pageable) {
        if (name == null) {
            throw new BadRequestException("Name for search friends cannot be null");
        }
        String namePattern = "%" + String.join("%", name.split("")) + "%";
        String city = Boolean.TRUE.equals(isTheSameCity) ? user.getCity() : null;
        pageable = PageRequest.of(pageable.getPageNumber(), AppConstant.FRIENDS_RESPONSE_SIZE, pageable.getSort());

        Page<UserWithMutualFriendsProjection> potentialFriendsPage = Boolean.TRUE.equals(isFriendsOfFriends)
            ? friendRepository.findFriendsOfFriends(user.getId(), namePattern, city, pageable)
            : friendRepository.findNotFriendsYet(user.getId(), namePattern, city, pageable);

        return new PageableDto<>(
            potentialFriendsPage.getContent().stream()
                .map(searchFriendDtoResponseMapper::convert)
                .toList(),
            potentialFriendsPage.getTotalElements(),
            potentialFriendsPage.getNumber(),
            potentialFriendsPage.getTotalPages());
    }

    /**
     * Method to add a user friend.
     *
     * <p>
     * This method is idempotent, so if the friend is already added, then the method will do nothing.
     *
     * @param currentUserId the ID of the user who is adding a friend
     * @param friendId      the ID of the user to add as a friend
     */
    @Override
    @Transactional
    public void addFriend(Long currentUserId, Long friendId) {
        if (currentUserId == null || friendId == null) {
            throw new BadRequestException("User id and friend id cannot be null");
        }
        if (!currentUserId.equals(friendId)) {
            friendRepository.addFriend(currentUserId, friendId);
        }
    }
}
