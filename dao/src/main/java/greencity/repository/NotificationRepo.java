package greencity.repository;

import greencity.entity.Notification;
import greencity.enums.NotificationOrigin;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepo extends JpaRepository<Notification, Long> {
    @Query("SELECT n FROM Notification n WHERE n.receiver.id = :receiverId ORDER BY n.creationDate DESC")
    Page<Notification> findNotificationsForUser(Long receiverId, Pageable pageable);

    @Query("SELECT n FROM Notification n WHERE n.receiver.id = :userId AND n.origin = :origin")
    Page<Notification> findNotificationsForUser(Long userId, NotificationOrigin origin, Pageable pageable);
}
