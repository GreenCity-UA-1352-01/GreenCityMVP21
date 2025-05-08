package greencity.repository.query;

public class FriendQueryProvider {
    public static final String FIND_NOT_FRIENDS_YET = """
            WITH mutual_friends AS (
                SELECT
                    u.id AS user_id,
                    COUNT(f2.friend_id) AS mutual_friends_count
                FROM users u
                LEFT JOIN users_friends f1
                    ON f1.user_id = :currentUserId
                    AND f1.status = 'FRIEND'
                LEFT JOIN users_friends f2
                    ON f2.user_id = u.id
                    AND f2.friend_id = f1.friend_id
                    AND f2.status = 'FRIEND'
                WHERE u.id != :currentUserId
                GROUP BY u.id
            )
            SELECT
                u.id,
                u.name,
                u.first_name AS firstName,
                u.city,
                u.rating,
                u.profile_picture AS profilePicture,
                COALESCE(mf.mutual_friends_count, 0) AS mutualFriendsCount
            FROM users u
            LEFT JOIN users_friends f
                ON (
                    (f.user_id = :currentUserId AND f.friend_id = u.id)
                        OR
                    (f.user_id = u.id AND f.friend_id = :currentUserId)
                )
            LEFT JOIN mutual_friends mf
                ON u.id = mf.user_id
            WHERE f.id IS NULL
                AND (:city IS NULL OR u.city = :city)
                AND (u.name LIKE :namePattern OR u.first_name LIKE :namePattern)
                AND u.id != :currentUserId
            ORDER BY u.first_name;
        """;

    public static final String FIND_FRIENDS_OF_FRIENDS = """
            WITH mutual_friends AS (
                SELECT
                    u.id AS user_id,
                    COUNT(f2.friend_id) AS mutual_friends_count
                FROM users u
                LEFT JOIN users_friends f1
                    ON f1.user_id = :currentUserId
                    AND f1.status = 'FRIEND'
                LEFT JOIN users_friends f2
                    ON f2.user_id = u.id
                    AND f2.friend_id = f1.friend_id
                    AND f2.status = 'FRIEND'
                WHERE u.id != :currentUserId
                GROUP BY u.id
            )
            SELECT
                u.id,
                u.name,
                u.first_name AS firstName,
                u.city,
                COALESCE(mf.mutual_friends_count, 0) AS mutualFriendsCount
            FROM users u
            INNER JOIN users_friends f1
                ON f1.user_id = :currentUserId
                AND f1.status = 'FRIEND'
            INNER JOIN users_friends f2
                ON f2.user_id = f1.friend_id
                AND f2.friend_id = u.id
                AND f2.status = 'FRIEND'
            LEFT JOIN users_friends uf
                ON (
                    (uf.user_id = :currentUserId AND uf.friend_id = u.id)
                        OR
                    (uf.user_id = u.id AND uf.friend_id = :currentUserId)
                )
            LEFT JOIN mutual_friends mf
                ON u.id = mf.user_id
            WHERE uf.id IS NULL
                AND (:city IS NULL OR u.city = :city)
                AND (u.name LIKE :namePattern OR u.first_name LIKE :namePattern)
                AND u.id != :currentUserId
            ORDER BY u.first_name;
        """;

    public static final String ADD_FRIEND = """
            INSERT INTO users_friends (user_id, friend_id, status)
            SELECT user_id, friend_id, status
            FROM (
                SELECT
                    :currentUserId AS user_id,
                    :friendId AS friend_id,
                    'REQUESTED' AS status
                UNION ALL
                SELECT
                    :friendId AS user_id,
                    :currentUserId AS friend_id,
                    'AWAITED' AS status
            ) AS new_friendship
            WHERE NOT EXISTS (
                SELECT 1
                FROM users_friends
                WHERE (
                    (user_id = :currentUserId AND friend_id = :friendId)
                        OR
                    (user_id = :friendId AND friend_id = :currentUserId)
                )
            );
        """;

    private FriendQueryProvider() {
    }
}
