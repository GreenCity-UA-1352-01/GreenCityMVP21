package greencity.repository;

import greencity.entity.EventComment;
import greencity.entity.EventCommentLike;
import greencity.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventCommentLikeRepository extends JpaRepository<EventCommentLike, Long> {
    boolean existsByEventCommentIdAndUserId(Long eventCommentId, Long userId);
    void deleteByEventCommentAndUser(EventComment eventComment, User user);
}
