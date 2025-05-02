package greencity.service;

import greencity.dto.PageableDto;
import greencity.dto.friend.EcoFriendProfileDto;
import greencity.dto.friend.EcoFriendsResponse;
import greencity.entity.User;
import greencity.repository.EcoNewsRepo;
import greencity.repository.FriendRepo;
import greencity.repository.HabitAssignRepo;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class FriendServiceImpl implements FriendService {
    private final FriendRepo friendRepository;
    private final HabitAssignRepo habitAssignRepo;
    private final EcoNewsRepo ecoNewsRepository;

    @Override
    @Transactional(readOnly = true)
    public PageableDto<EcoFriendsResponse> getAllFriendsForUser(Long userId, Pageable pageable) {
        Page<User> friendsPage = friendRepository.findAllFriendsByUserId(userId, pageable);

        List<EcoFriendsResponse> friendDtos = friendsPage.getContent().stream()
                .map(friend -> {
                    int mutualFriendsCount = countMutualFriends(userId, friend.getId());
                    return buildEcoFriendsDto(friend, mutualFriendsCount);
                })
                .toList();

        return new PageableDto<>(
                friendDtos,
                friendsPage.getTotalElements(),
                friendsPage.getNumber(),
                friendsPage.getTotalPages()
        );

    }
    private EcoFriendsResponse buildEcoFriendsDto(User user, int mutualFriendsCount) {
        return EcoFriendsResponse.builder()
                .id(user.getId())
                .name(user.getFirstName())
                .city(user.getCity())
                .rating(user.getRating())
                .profilePicturePath(user.getProfilePicturePath())
                .isOnline(user.getLastActivityTime() != null &&
                        user.getLastActivityTime().isAfter(LocalDateTime.now().minusMinutes(5)))
                .mutualFriends(mutualFriendsCount)
                .build();
    }

    private int countMutualFriends(Long currentUserId, Long friendId) {
        return friendRepository.countMutualFriends(currentUserId, friendId);
    }

    @Override
    public EcoFriendProfileDto getFriendProfile(Long userId, Long friendId) {
        User user = friendRepository.findFriendByUserIdAndFriendId(userId, friendId)
                .orElseThrow(() -> new AccessDeniedException("User is not your friend"));

        int habitsInProgress = habitAssignRepo.countHabitAssignsByUserIdAndAcquiredFalseAndCancelledFalse(friendId);
        int habitsAcquired = friendRepository.countHabitAssignsByUserFriendIdAndStatusAcquired(friendId);
        long newsPublished = ecoNewsRepository.getAmountOfPublishedNewsByUserId(friendId);

        return buildEcoFriendProfileDto(user, habitsInProgress, habitsAcquired, newsPublished);
    }

    private EcoFriendProfileDto buildEcoFriendProfileDto(User user, int habitsInProgress, int habitsAcquired, long newsPublished) {

        return EcoFriendProfileDto.builder()
                .id(user.getId())
                .name(user.getFirstName())
                .profilePicturePath(user.getProfilePicturePath())
                .rating(user.getRating())
                .userCredo(user.getUserCredo())
                .isOnline(user.getLastActivityTime() != null &&
                        user.getLastActivityTime().isAfter(LocalDateTime.now().minusMinutes(5)))
                .habitsInProgress(habitsInProgress)
                .habitsAcquired(habitsAcquired)
                .newsPublished(newsPublished)
                .build();
    }
}
