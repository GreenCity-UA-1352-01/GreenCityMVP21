package greencity.entity;

import greencity.enums.EventAttenderStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "events_attenders")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class EventAttender {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "attender_id", nullable = false)
    private User attender;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "status", nullable = false)
    private EventAttenderStatus status;
}
