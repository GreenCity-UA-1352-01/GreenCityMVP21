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
@EqualsAndHashCode(exclude = {"user"})
@ToString(exclude = {"user"})
public class NotificationCounter {
    @Id
    @Column(name = "user_id")
    private Long userId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @Column(name = "count_of_notifications")
    private Integer countOfNotifications;
}
