package greencity.entity;

import jakarta.persistence.*;
import java.time.ZonedDateTime;
import lombok.*;

@Entity
@Table(name = "event_date_time_locations")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode(exclude = {"event"})
@ToString(exclude = {"event"})
public class EventDateTimeLocation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "start_date_time", nullable = false)
    private ZonedDateTime startDateTime;

    @Column(name = "end_date_time", nullable = false)
    private ZonedDateTime endDateTime;

    @Column(name = "location")
    private String location;

    @Column(name = "link")
    private String link;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;
}
