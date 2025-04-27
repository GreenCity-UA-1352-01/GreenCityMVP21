package greencity.repository;

import greencity.entity.Friend;
import greencity.projection.UserWithMutualFriendsProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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
     *
     * @return page of users that are not friends with given user yet
     *
     * @author Rostyslav Zadyraichuk
     */
    @Query(nativeQuery = true, value = """
            WITH mutual_friends AS (
                SELECT
                    u.id AS user_id,
                    COUNT(*) AS mutual_friends_count
                FROM users u
                INNER JOIN friends f
                    ON f.user_id = :currentUserId
                        OR f.friend_id = :currentUserId
                INNER JOIN friends ff
                    ON (ff.user_id = u.id OR ff.friend_id = u.id)
                        AND (f.user_id = ff.user_id OR f.user_id = ff.friend_id
                            OR f.friend_id = ff.user_id OR f.friend_id = ff.friend_id)
                WHERE u.id != :currentUserId
                    AND f.user_id != u.id
                    AND f.friend_id != u.id
                    AND ff.user_id != :currentUserId
                    AND ff.friend_id != :currentUserId
                GROUP BY u.id
            )
            SELECT
                u.id, u.name, u.first_name, u.city, u.rating, u.profile_picture
                COALESCE(mf.mutual_friends_count, 0) AS mutual_friends_count
            FROM users u
            LEFT JOIN friends f
                ON (u.id = f.friend_id AND f.user_id = :currentUserId)
                OR (u.id = f.user_id AND f.friend_id = :currentUserId)
            LEFT JOIN mutual_friends mf
                ON u.id = mf.user_id
            WHERE f.id IS NULL
                AND (:city IS NULL OR u.city = :city)
                AND (u.name LIKE :namePattern OR u.first_name LIKE :namePattern)
                AND u.id != :currentUserId
            ORDER BY u.first_name;
        """)
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
     *
     * @return a page of users who are friends of the friends of the user with the given ID
     *
     * @author Rostyslav Zadyraichuk
     */
    @Query(nativeQuery = true, value = """
            WITH mutual_friends AS (
                SELECT
                    u.id AS user_id,
                    COUNT(*) AS mutual_friends_count
                FROM users u
                INNER JOIN friends f
                    ON f.user_id = :currentUserId
                        OR f.friend_id = :currentUserId
                INNER JOIN friends ff
                    ON (ff.user_id = u.id OR ff.friend_id = u.id)
                        AND (f.user_id = ff.user_id OR f.user_id = ff.friend_id
                            OR f.friend_id = ff.user_id OR f.friend_id = ff.friend_id)
                WHERE u.id != :currentUserId
                    AND f.user_id != u.id
                    AND f.friend_id != u.id
                    AND ff.user_id != :currentUserId
                    AND ff.friend_id != :currentUserId
                GROUP BY u.id
            )
            SELECT DISTINCT
                u.id, u.name, u.first_name, u.city, u.rating, u.profile_picture
                COALESCE(mf.mutual_friends_count, 0) AS mutual_friends_count
            FROM users u
            JOIN users_friends f
                ON f.user_id = :currentUserId OR f.friend_id = :currentUserId
            JOIN users_friends ff
                ON ff.user_id = CASE
                    WHEN f.user_id = :currentUserId
                    THEN f.friend_id
                    ELSE f.user_id
                    END
                OR ff.friend_id = CASE
                    WHEN f.user_id = :currentUserId
                    THEN f.friend_id
                    ELSE f.user_id
                    END
            LEFT JOIN mutual_friends mf
                ON u.id = mf.user_id
            WHERE u.id = CASE
                    WHEN ff.user_id = CASE
                        WHEN f.user_id = :currentUserId
                        THEN f.friend_id
                        ELSE f.user_id
                        END
                    THEN ff.friend_id
                    ELSE ff.user_id
                    END
                AND (:city IS NULL OR u.city = :city)
                AND (u.name LIKE :namePattern OR u.first_name LIKE :namePattern)
                AND u.id != :currentUserId
            ORDER BY u.first_name;
        """)
    Page<UserWithMutualFriendsProjection> findFriendsOfFriends(@Param("currentUserId") Long currentUserId,
                                                               @Param("namePattern") String namePattern,
                                                               @Param("city") String city,
                                                               Pageable pageable);
}
