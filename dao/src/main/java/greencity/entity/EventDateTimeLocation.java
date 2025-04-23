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
public class EventDateTimeLocation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private ZonedDateTime startDateTime;

    private ZonedDateTime endDateTime;

    private String location;

    private String link;

    @ManyToOne(fetch = FetchType.LAZY)
    private Event event;
}
