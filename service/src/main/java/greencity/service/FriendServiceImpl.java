package greencity.service;

import greencity.dto.friend.EcoFriendProfileDto;
import greencity.dto.friend.EcoFriendsResponse;
import greencity.entity.HabitAssign;
import greencity.entity.User;
import greencity.enums.HabitAssignStatus;
import greencity.repository.EcoNewsRepo;
import greencity.repository.FriendRepo;
import greencity.repository.HabitAssignRepo;
import lombok.AllArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class FriendServiceImpl implements FriendService {
    private final FriendRepo friendRepository;
    private final HabitAssignRepo habitAssignRepo;
    private final EcoNewsRepo ecoNewsRepository;

    @Transactional(readOnly = true)
    public List<EcoFriendsResponse> getAllFriendsForUser(Long userId) {
        List<User> friends = friendRepository.findAllFriendsByUserId(userId);

        Set<Long> myFriendIds = friends.stream()
                .map(User::getId)
                .collect(Collectors.toSet());

        return friends.stream()
                .map(friend -> buildEcoFriendsDto(friend, myFriendIds, userId))
                .toList();
    }

    private EcoFriendsResponse buildEcoFriendsDto(User user, Set<Long> myFriendIds, Long currentUserId) {
        int mutualFriendsCount = countMutualFriends(currentUserId, user.getId(), myFriendIds);

        return EcoFriendsResponse.builder()
                .id(user.getId())
                .name(user.getFirstName())
                .city(user.getCity())
                .rating(user.getRating())
                .profilePicturePath(user.getProfilePicturePath())
                .isOnline(user.getLastActivityTime() != null && user.getLastActivityTime().isAfter(LocalDateTime.now().minusMinutes(5)))
                .mutualFriends(mutualFriendsCount)
                .build();
    }

    private int countMutualFriends(Long currentUserId, Long friendId, Set<Long> myFriendIds) {
        List<User> friendFriends = friendRepository.findAllFriendsByUserId(friendId);

        Set<Long> friendFriendIds = friendFriends.stream()
                .map(User::getId)
                .collect(Collectors.toSet());

        friendFriendIds.retainAll(myFriendIds);

        friendFriendIds.remove(currentUserId);
        friendFriendIds.remove(friendId);

        return friendFriendIds.size();
    }

    @Override
    public EcoFriendProfileDto getFriendProfile(Long userId, Long friendId) {
        User user = friendRepository.findFriendByUserIdAndFriendId(userId, friendId)
                .orElseThrow(() -> new AccessDeniedException("User is not your friend"));

        List<HabitAssign> friendHabits = habitAssignRepo.findAllByUserId(friendId);
        int habitsInProgress = countHabitsByStatus(friendHabits, HabitAssignStatus.INPROGRESS);
        int habitsAcquired = countHabitsByStatus(friendHabits, HabitAssignStatus.ACQUIRED);
        long newsPublished = ecoNewsRepository.getAmountOfPublishedNewsByUserId(friendId);

        return buildEcoFriendProfileDto(user, habitsInProgress, habitsAcquired, newsPublished);
    }

    private int countHabitsByStatus(List<HabitAssign> habits, HabitAssignStatus status) {
        return (int) habits.stream()
                .filter(h -> h.getStatus() == status)
                .count();
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
