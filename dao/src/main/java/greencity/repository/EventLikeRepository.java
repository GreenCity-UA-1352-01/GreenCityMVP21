package greencity.repository;

import greencity.entity.EventLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EventLikeRepository extends JpaRepository<EventLike, Long> {
    boolean existsByEventIdAndUserId(Long eventId, Long userId);
    Long countByEventId(Long eventId);
    Optional<EventLike> findByEventIdAndUserId(Long eventId, Long userId);


    void deleteByEventIdAndUserId(Long eventId, Long userId); // for unlike in the future
}
