package greencity.repository.query;

public class FriendQueryProvider {
    public static final String FIND_NOT_FRIENDS_YET = """
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
                u.id, u.name, u.first_name, u.city, u.rating, u.profile_picture,
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
        """;

    public static final String FIND_FRIENDS_OF_FRIENDS = """
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
                u.id, u.name, u.first_name, u.city, u.rating, u.profile_picture,
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
        """;

    public static final String ADD_FRIEND = """
            INSERT INTO users_friends (user_id, friend_id, status)
            SELECT :currentUserId, :friendId, 'REQUESTED'
            WHERE NOT EXISTS (
                SELECT 1
                FROM users_friends
                WHERE (user_id = :currentUserId AND friend_id = :friendId)
                    OR (user_id = :friendId AND friend_id = :currentUserId)
            )
        """;

    private FriendQueryProvider() {
    }
}
