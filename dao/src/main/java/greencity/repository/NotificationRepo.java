package greencity.repository;

import greencity.entity.Notification;
import greencity.enums.NotificationOrigin;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepo extends JpaRepository<Notification, Long> {
    @Query("SELECT n FROM Notification n WHERE n.receiver.id = :receiverId ORDER BY n.creationDate DESC")
    Page<Notification> findNotificationsForUser(Long receiverId, Pageable pageable);

    @Query("SELECT n FROM Notification n WHERE n.receiver.id = :userId AND n.origin = :origin")
    Page<Notification> findNotificationsForUser(Long userId, NotificationOrigin origin, Pageable pageable);

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
            "n.objectLink = :link")
    boolean existsByUsersAndLink(@Param("initiatorId") Long initiatorId,
                                                          @Param("receiverId") Long receiverId,
                                                          @Param("link") String objectLink);

    @Modifying
    @Query("DELETE FROM Notification n WHERE " +
            "n.initiator.id = :initiatorId AND " +
            "n.receiver.id = :receiverId AND " +
            "n.objectLink = :link")
    void deleteByUsersAndLink(@Param("initiatorId") Long initiatorId,
                                                       @Param("receiverId") Long receiverId,
                                                       @Param("link") String objectLink);
}
