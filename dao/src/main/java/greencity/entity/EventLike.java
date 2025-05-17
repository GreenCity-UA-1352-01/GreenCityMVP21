package greencity.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.ZonedDateTime;

@Entity
@Table(name = "event_likes",
    uniqueConstraints = @UniqueConstraint(columnNames = {"event_id", "user_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "liked_at", nullable = false)
    private ZonedDateTime likedAt;
}
