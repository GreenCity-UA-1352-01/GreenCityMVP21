package greencity.service;

import greencity.enums.FriendsStatus;
import jakarta.persistence.EntityNotFoundException;
import greencity.dto.PageableDto;
import greencity.dto.friend.EcoFriendProfileDto;
import greencity.dto.friend.EcoFriendsResponse;
import greencity.entity.User;
import greencity.entity.Friend;
import greencity.exception.exceptions.NotFoundException;
import greencity.mapping.EcoFriendProfileDtoMapper;
import greencity.mapping.EcoFriendsResponseMapper;
import greencity.mapping.records.FriendProfileData;
import greencity.mapping.records.FriendWithMutuals;
import greencity.repository.EcoNewsRepo;
import greencity.constant.AppConstant;
import greencity.dto.friend.SearchFriendDtoResponse;
import greencity.dto.user.UserVO;
import greencity.exception.exceptions.BadRequestException;
import greencity.mapping.SearchFriendDtoResponseMapper;
import greencity.projection.UserWithMutualFriendsProjection;
import greencity.repository.FriendRepo;
import greencity.repository.HabitAssignRepo;
import greencity.repository.UserRepo;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@AllArgsConstructor
public class FriendServiceImpl implements FriendService {
    private final FriendRepo friendRepository;
    private final SearchFriendDtoResponseMapper searchFriendDtoResponseMapper;
    private final UserRepo userRepository;
    private final HabitAssignRepo habitAssignRepo;
    private final EcoNewsRepo ecoNewsRepository;
    private final EcoFriendsResponseMapper ecoFriendsResponseMapper;
    private final EcoFriendProfileDtoMapper ecoFriendProfileDtoMapper;

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
     *
     * @author Rostyslav Zadyraichuk
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

    @Override
    @Transactional(readOnly = true)
    public PageableDto<EcoFriendsResponse> getAllFriendsForUser(Long userId, Pageable pageable) {
        Page<User> friendsPage = friendRepository.findAllFriendsByUserId(userId, pageable);

        List<EcoFriendsResponse> friendDtos = friendsPage.getContent().stream()
                .map(friend -> ecoFriendsResponseMapper.convert(
                        new FriendWithMutuals(friend, countMutualFriends(userId, friend.getId()))))
                .toList();

        return new PageableDto<>(
                friendDtos,
                friendsPage.getTotalElements(),
                friendsPage.getNumber(),
                friendsPage.getTotalPages()
        );
    }

    @Override
    public EcoFriendProfileDto getFriendProfile(Long userId, Long friendId) {
        User friend = userRepository.findById(friendId)
                .orElseThrow(() -> new NotFoundException("User with id " + friendId + " not found"));

        if (!friendRepository.isFriend(userId, friendId)) {
            throw new AccessDeniedException("User with id " + friendId + " is not your friend");
        }

        int habitsInProgress = habitAssignRepo.countHabitAssignsByUserIdAndAcquiredFalseAndCancelledFalse(friendId);
        int habitsAcquired = friendRepository.countHabitAssignsByUserFriendIdAndStatusAcquired(friendId);
        long newsPublished = ecoNewsRepository.getAmountOfPublishedNewsByUserId(friendId);

        return ecoFriendProfileDtoMapper.convert(
                new FriendProfileData(friend, habitsInProgress, habitsAcquired, newsPublished)
        );
    }

    private int countMutualFriends(Long currentUserId, Long friendId) {
        return friendRepository.countMutualFriends(currentUserId, friendId);
    }

    /**
     * Removes a bidirectional friendship between two users.
     * <p>
     * This method deletes both directions of the friendship:
     * user → friend and friend → user.
     * If no friendship records are found in either direction, it throws an {@link EntityNotFoundException}.
     * </p>
     *
     * @param userId   the ID of the user initiating the removal
     * @param friendId the ID of the friend to be removed
     * @throws EntityNotFoundException if no friendship exists between the two users
     */
    @Transactional
    public void removeFriend(Long userId, Long friendId) {

        int deletedCount = 0;

        deletedCount += friendRepository.deleteByUserIdAndFriendId(userId, friendId);
        deletedCount += friendRepository.deleteByUserIdAndFriendId(friendId, userId);

        if (deletedCount == 0) {
            throw new NotFoundException("Friendship not found between users");
        }
    }

    @Transactional
    public void acceptFriendRequest(Long currentUserId, Long requesterId) {
        Friend awaited = friendRepository.findByUserIdAndFriendIdAndStatus(currentUserId, requesterId, FriendsStatus.AWAITED)
                .orElseThrow(() -> new NotFoundException("Friend request not found"));

        Friend requested = friendRepository.findByUserIdAndFriendIdAndStatus(requesterId, currentUserId, FriendsStatus.REQUESTED)
                .orElseThrow(() -> new NotFoundException("Friend request not found"));

        awaited.setStatus(FriendsStatus.FRIEND);
        requested.setStatus(FriendsStatus.FRIEND);

        friendRepository.save(awaited);
        friendRepository.save(requested);
    }
}
