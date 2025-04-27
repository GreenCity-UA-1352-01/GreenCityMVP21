package greencity.repository;

import greencity.entity.Friend;
import greencity.projection.UserWithMutualFriendsProjection;
import greencity.repository.query.FriendQueryProvider;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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
}
