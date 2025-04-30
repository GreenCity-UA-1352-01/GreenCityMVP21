package greencity.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "notification_counter")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class NotificationCounter {
    @Id
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @Column
    private Integer countOfNotifications;
}
