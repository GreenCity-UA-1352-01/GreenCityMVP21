package greencity.service;

import greencity.repository.FriendRepo;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class FriendServiceImpl implements FriendService {
    private final FriendRepo friendRepository;

    /**
     * Removes a bidirectional friendship between two users.
     * <p>
     * This method deletes both directions of the friendship:
     * user → friend and friend → user. If no such records exist,
     * the method completes silently without throwing an exception.
     * </p>
     *
     * @param userId   the ID of the user initiating the removal
     * @param friendId the ID of the friend to be removed
     */
    @Transactional
    public void removeFriend(Long userId, Long friendId) {

        friendRepository.deleteByUserIdAndFriendId(userId, friendId);
        friendRepository.deleteByUserIdAndFriendId(friendId, userId);

    }
}
