package greencity.repository;

import greencity.entity.Notification;
import greencity.enums.NotificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepo extends JpaRepository<Notification, Long> {
    @Modifying
    @Query("UPDATE Notification n SET n.status = :status WHERE n.id = :id")
    int updateStatusById(@Param("id") Long id, @Param("status") NotificationStatus status);

}
