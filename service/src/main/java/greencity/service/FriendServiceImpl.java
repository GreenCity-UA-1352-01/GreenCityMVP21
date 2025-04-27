package greencity.service;

import greencity.dto.PageableDto;
import greencity.dto.friend.SearchFriendDtoResponse;
import greencity.dto.user.UserVO;
import greencity.mapping.SearchFriendDtoResponseMapper;
import greencity.projection.UserWithMutualFriendsProjection;
import greencity.repository.FriendRepo;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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
        String namePattern = "%" + String.join("%", name.split("")) + "%";
        String city = Boolean.TRUE.equals(isTheSameCity) ? user.getCity() : null;

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
}
