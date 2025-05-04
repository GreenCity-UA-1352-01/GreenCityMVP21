package greencity.service;

import greencity.exception.exceptions.NotFoundException;
import greencity.repository.FriendRepo;
import jakarta.persistence.EntityNotFoundException;
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
}
