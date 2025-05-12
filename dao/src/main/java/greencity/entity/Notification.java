package greencity.entity;

import greencity.enums.NotificationObjectType;
import greencity.enums.NotificationStatus;
import greencity.enums.NotificationType;
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

    /**
     * Field represents the identifier of the specific object involved
     * in the action that triggered this notification.
     */
    @Column(name = "object_id", nullable = false)
    private Long objectId;

    /**
     * Field represents the type of the specific object (Event, Habit, User etc.) involved
     * in the action that triggered this notification.
     */
    @Column(name = "object_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private NotificationObjectType objectType;

    /**
     * Field represents the specific name that identifies the object
     * where the action triggering this notification occurred.
     */
    @Column(name = "object_name", nullable = false)
    private String objectName;

    @Column(name = "creation_date", nullable = false)
    private ZonedDateTime creationDate;

    @Column
    @Enumerated(EnumType.STRING)
    private NotificationStatus status;

    @Column(name = "notification_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private NotificationType notificationType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", referencedColumnName = "id")
    private User receiver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "initiator_id", referencedColumnName = "id")
    private User initiator;
}
