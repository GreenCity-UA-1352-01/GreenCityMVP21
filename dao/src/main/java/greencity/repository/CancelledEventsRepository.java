package greencity.repository;

import greencity.entity.CancelledEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CancelledEventsRepository extends JpaRepository<CancelledEvent, Long> {
    List<CancelledEvent> findByEventId(Long eventId);
}
