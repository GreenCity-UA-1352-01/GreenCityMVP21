package greencity.repository;

import greencity.entity.Friend;
import greencity.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FriendRepo extends JpaRepository<Friend, Long> {
    @Query("SELECT f.friend FROM Friend f WHERE f.user.id = :userId AND f.status = 'FRIEND'")
    Page<User> findAllFriendsByUserId(Long userId, Pageable pageable);

    @Query("SELECT f.friend FROM Friend f WHERE f.user.id = :userId AND f.friend.id = :friendId AND f.status = 'FRIEND'")
    Optional<User> findFriendByUserIdAndFriendId(Long userId, Long friendId);

    @Query(value = """
    SELECT COUNT(*) 
    FROM (
        SELECT DISTINCT 
            CASE 
                WHEN f1.user_id = :userId1 THEN f1.friend_id 
                ELSE f1.user_id 
            END AS mutual_friend_id
        FROM users_friends f1
        JOIN users_friends f2 
            ON (
                (f1.user_id = :userId1 OR f1.friend_id = :userId1)
                AND (f2.user_id = :userId2 OR f2.friend_id = :userId2)
                AND (
                    CASE 
                        WHEN f1.user_id = :userId1 THEN f1.friend_id 
                        ELSE f1.user_id 
                    END
                    =
                    CASE 
                        WHEN f2.user_id = :userId2 THEN f2.friend_id 
                        ELSE f2.user_id 
                    END
                )
            )
        WHERE f1.status = 'FRIEND' AND f2.status = 'FRIEND'
    ) AS mutuals
    """, nativeQuery = true)
    int countMutualFriends(@Param("userId1") Long userId1, @Param("userId2") Long userId2);

    @Query(value = "SELECT COUNT(ha.id) FROM HabitAssign ha "
            + "WHERE upper(ha.status) = 'ACQUIRED' AND ha.user.id = :userId")
    int countHabitAssignsByUserFriendIdAndStatusAcquired(@Param("userId") Long userId);

}
