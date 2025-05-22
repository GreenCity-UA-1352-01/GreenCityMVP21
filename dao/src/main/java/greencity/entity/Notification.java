package greencity.entity;

import greencity.enums.NotificationObjectType;
import greencity.enums.NotificationType;
import greencity.enums.NotificationOrigin;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import lombok.*;
import java.time.ZonedDateTime;

@Entity
@Table(name = "notifications")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode(exclude = {"notificationReceivers"})
@ToString(exclude = {"notificationReceivers"})
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

    @Column(name = "notification_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private NotificationType notificationType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "initiator_id", referencedColumnName = "id")
    private User initiator;

    @OneToMany(fetch = FetchType.LAZY,
        mappedBy = "notification",
        cascade = CascadeType.ALL,
        orphanRemoval = true)
    @Builder.Default
    private List<NotificationReceiver> notificationReceivers = new ArrayList<>();

    @Column
    @Enumerated(EnumType.STRING)
    private NotificationOrigin origin;

    /**
     * Field represents the identifier of the comment
     * that is associated with this notification.
     * This is used for user mention notifications to redirect to the specific comment.
     */
    @Column(name = "comment_id")
    private Long commentId;
}
