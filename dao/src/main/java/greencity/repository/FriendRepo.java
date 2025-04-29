package greencity.repository;

import greencity.entity.Friend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FriendRepo extends JpaRepository<Friend, Long> {

    void deleteByUserIdAndFriendId(Long userId, Long friendId);

}
