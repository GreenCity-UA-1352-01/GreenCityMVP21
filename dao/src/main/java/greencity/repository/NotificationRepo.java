package greencity.repository;

import greencity.entity.Notification;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepo extends JpaRepository<Notification, Long> {
    @Query("""
            SELECT n
            FROM Notification n
            RIGHT JOIN NotificationReceiver nr
                ON n.id = nr.notification.id
            WHERE nr.receiver.id = :receiverId ORDER BY n.creationDate DESC
        """)
    List<Notification> findNotificationsForUser(Long receiverId);
}
