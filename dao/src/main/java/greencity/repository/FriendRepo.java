package greencity.repository;

import greencity.entity.Friend;
import greencity.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FriendRepo extends JpaRepository<Friend, Long> {
    @Query("SELECT f.friend FROM Friend f WHERE f.user.id = :userId AND f.status = 'FRIEND'")
    List<User> findAllFriendsByUserId(Long userId);
}
