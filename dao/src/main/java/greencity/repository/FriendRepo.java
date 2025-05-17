package greencity.repository;

import greencity.entity.Friend;
import greencity.enums.FriendsStatus;
import greencity.projection.UserWithMutualFriendsProjection;
import greencity.repository.query.FriendQueryProvider;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import greencity.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface FriendRepo extends JpaRepository<Friend, Long> {
    /**
     * Finds all users that are not friends with given user yet,
     * and filter them by name pattern and city (if present).
     *
     * @param currentUserId id of the user, that we are searching friends for
     * @param namePattern   pattern to search in name and first name of users
     * @param city          if not null, then search only in given city
     * @param pageable      pagination parameters
     * @return page of users that are not friends with given user yet
     * @author Rostyslav Zadyraichuk
     */
    @Query(nativeQuery = true, value = FriendQueryProvider.FIND_NOT_FRIENDS_YET)
    Page<UserWithMutualFriendsProjection> findNotFriendsYet(@Param("currentUserId") Long currentUserId,
                                                            @Param("namePattern") String namePattern,
                                                            @Param("city") String city,
                                                            Pageable pageable);

    /**
     * Finds users who are friends of the friends of the user with the given ID.
     * Does not include the user with the given ID and his direct friends.
     *
     * @param currentUserId ID of the user to find friends of friends for
     * @param namePattern   pattern to search for in the user's name and first name
     * @param city          city to limit the search to
     * @param pageable      pagination information
     * @return a page of users who are friends of the friends of the user with the given ID
     * @author Rostyslav Zadyraichuk
     */
    @Query(nativeQuery = true, value = FriendQueryProvider.FIND_FRIENDS_OF_FRIENDS)
    Page<UserWithMutualFriendsProjection> findFriendsOfFriends(@Param("currentUserId") Long currentUserId,
                                                               @Param("namePattern") String namePattern,
                                                               @Param("city") String city,
                                                               Pageable pageable);

    @Modifying
    @Query(nativeQuery = true, value = FriendQueryProvider.ADD_FRIEND)
    void addFriend(@Param("currentUserId") Long currentUserId, @Param("friendId") Long friendId);

    @Query("SELECT f.friend FROM Friend f WHERE f.user.id = :userId AND f.status = 'FRIEND'")
    Page<User> findAllFriendsByUserId(Long userId, Pageable pageable);

    @Query("""
            SELECT COUNT(f) > 0
            FROM Friend f
            WHERE f.user.id = :userId
                AND f.friend.id = :friendId
                AND f.status = 'FRIEND'
        """)
    boolean isFriend(@Param("userId") Long userId, @Param("friendId") Long friendId);

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

    int deleteByUserIdAndFriendId(Long userId, Long friendId);

    Optional<Friend> findByUserIdAndFriendIdAndStatus(Long userId, Long friendId, FriendsStatus status);
}
