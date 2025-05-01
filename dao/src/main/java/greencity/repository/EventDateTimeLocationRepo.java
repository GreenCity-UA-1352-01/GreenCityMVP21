package greencity.repository;

import greencity.entity.EventDateTimeLocation;
import greencity.entity.Habit;
import greencity.entity.Language;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Provides an interface to manage {@link EventDateTimeLocation} entity.
 */

@Repository
public interface EventDateTimeLocationRepo extends JpaRepository<EventDateTimeLocation, Long> {
    /**
     * method, that returns {@link EventDateTimeLocation} that belong to Event .
     *
     * @param eventId id of the Event.
     * @return {@link EventDateTimeLocation} by Event id.
     */
    List<EventDateTimeLocation> findByEventId(Long eventId);

}
