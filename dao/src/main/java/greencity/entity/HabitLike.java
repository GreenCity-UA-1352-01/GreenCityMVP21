package greencity.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Entity
@Table(name = "habit_likes", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"liked_by_id", "habit_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HabitLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "liked_by_id", nullable = false)
    private User likedBy;

    @ManyToOne
    @JoinColumn(name = "habit_id", nullable = false)
    private Habit habit;

    @Column(name = "liked_at", nullable = false)
    private ZonedDateTime likedAt;
}
