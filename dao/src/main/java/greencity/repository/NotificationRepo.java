package greencity.repository;

import greencity.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepo extends JpaRepository<Notification, Long> {

    @Query("SELECT COUNT(n) > 0 FROM Notification n WHERE " +
            "n.initiator.id = :initiatorId AND " +
            "n.receiver.id = :receiverId AND " +
            "n.action = 'likes' AND " +
            "n.objectLink = :link")
    boolean existsLikeNotification(@Param("initiatorId") Long initiatorId,
                                   @Param("receiverId") Long receiverId,
                                   @Param("link") String objectLink);

    @Modifying
    void deleteByInitiatorIdAndReceiverIdAndActionAndObjectLink(
            Long initiatorId,
            Long receiverId,
            String action,
            String objectLink
    );
}
