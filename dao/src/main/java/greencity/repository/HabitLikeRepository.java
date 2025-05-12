package greencity.repository;

import greencity.entity.HabitLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HabitLikeRepository extends JpaRepository<HabitLike, Long> {
    boolean existsByHabitIdAndLikedById(Long habitId, Long likedById);

    void deleteByHabitIdAndLikedById(Long habitId, Long userId);
}
