package greencity.service;

import greencity.dto.friend.FriendCardDtoResponse;
import greencity.entity.User;
import greencity.repository.FriendRepo;
import lombok.AllArgsConstructor;
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

    @Transactional(readOnly = true)
    public List<FriendCardDtoResponse> getAllFriendsForUser(Long userId) {
        List<User> friends = friendRepository.findAllFriendsByUserId(userId);

        Set<Long> myFriendIds = friends.stream()
                .map(User::getId)
                .collect(Collectors.toSet());

        return friends.stream()
                .map(friend -> mapToDto(friend, myFriendIds, userId))
                .toList();
    }

    private FriendCardDtoResponse mapToDto(User user, Set<Long> myFriendIds, Long currentUserId) {
        int mutualFriendsCount = countMutualFriends(currentUserId, user.getId(), myFriendIds);

        return FriendCardDtoResponse.builder()
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
}
