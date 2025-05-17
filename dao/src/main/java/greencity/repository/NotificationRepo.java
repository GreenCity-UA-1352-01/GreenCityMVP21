package greencity.repository;

import greencity.entity.Notification;
import greencity.enums.NotificationObjectType;
import greencity.enums.NotificationType;
import java.util.List;
import greencity.enums.NotificationOrigin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface NotificationRepo extends JpaRepository<Notification, Long> {
    @Query("""
            SELECT n
            FROM Notification n
            RIGHT JOIN NotificationReceiver nr
                ON n.id = nr.notification.id
            WHERE nr.receiver.id = :receiverId
                AND n.origin = :origin
            ORDER BY n.creationDate DESC
        """)
    List<Notification> findNotificationsForUser(Long receiverId, NotificationOrigin origin);

    @Query("""
            SELECT COUNT(n) > 0
            FROM Notification n
            RIGHT JOIN NotificationReceiver nr
                ON n.id = nr.notification.id
            WHERE n.initiator.id = :initiatorId
                AND nr.receiver.id = :receiverId
                AND n.notificationType = :notificationType
                AND n.objectType = :objectType
                AND n.objectId = :objectId
            """)
    boolean existsLikeNotification(Long initiatorId,
                                   Long receiverId,
                                   NotificationType notificationType,
                                   NotificationObjectType objectType,
                                   Long objectId);

    @Query("""
            DELETE FROM Notification n
            WHERE n.initiator.id = :initiatorId
                AND n.notificationType = :notificationType
                AND n.objectType = :objectType
                AND n.objectId = :objectId
                AND EXISTS (
                    SELECT 1 FROM NotificationReceiver nr
                    WHERE nr.notification.id = n.id
                        AND nr.receiver.id = :receiverId
                )
        """)
    @Modifying
    @Transactional
    void deleteLikeNotification(Long initiatorId,
                                   Long receiverId,
                                   NotificationType notificationType,
                                   NotificationObjectType objectType,
                                   Long objectId);
}
