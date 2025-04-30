package greencity.repository;

import greencity.entity.NotificationCounter;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationCounterRepo extends JpaRepository<NotificationCounter, Long> {
}
