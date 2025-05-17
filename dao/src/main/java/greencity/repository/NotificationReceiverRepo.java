package greencity.repository;

import greencity.entity.NotificationReceiver;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface NotificationReceiverRepo extends JpaRepository<NotificationReceiver, Long> {
    @Query("""
        SELECT nr
        FROM NotificationReceiver nr
        WHERE nr.notification.id = :notificationId
            AND nr.receiver.id = :receiverId
        """)
    Optional<NotificationReceiver> findNotificationReceiver(Long notificationId, Long receiverId);
}
