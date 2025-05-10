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

    /**
     * Field represents the specific location in the codebase where the action
     * triggering this notification occurred. This could be a class name and
     * method name, an API endpoint, or any other identifiable code location
     * relevant to the action.
     */
    @Column(name = "action_source", nullable = false)
    private String actionSource;

    /**
     * Field represents the identifier of the specific object involved
     * in the action that triggered this notification.
     */
    @Column(name = "object_id", nullable = false)
    private Long objectId;

    /**
     * Field represents the specific entity class name
     * where the action triggering this notification occurred.
     */
    @Column(name = "object_name", nullable = false)
    private String objectName;

    /**
     * Field represents the hyperlink associated with the specific object
     * involved in the action that triggered this notification.
     */
    @Column(name = "object_link")
    private String objectLink;

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
