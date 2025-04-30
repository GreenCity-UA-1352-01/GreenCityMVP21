package greencity.entity;

import greencity.enums.NotificationStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.ZonedDateTime;

@Entity
@Table(name = "notifications")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode(exclude = {"receiver", "initiator"})
@ToString(exclude = {"receiver", "initiator"})
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String action;

    @Column(name = "object_name")
    private String objectName;

    @Column(name = "creation_date", nullable = false)
    private ZonedDateTime creationDate;

    @Column
    @Enumerated(EnumType.STRING)
    private NotificationStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", referencedColumnName = "id")
    private User receiver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "initiator_id", referencedColumnName = "id")
    private User initiator;
}
