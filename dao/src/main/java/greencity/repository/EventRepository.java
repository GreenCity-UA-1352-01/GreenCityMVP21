package greencity.repository;

import greencity.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    @Query("SELECT e FROM Event e LEFT JOIN FETCH e.tags WHERE e.id = :id")
    Optional<Event> findById(Long id);
}
