package greencity.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.*;

@Entity
@Table(name = "events")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode(exclude = {"dateTimes", "mainImage", "eventImages", "tags"})
@ToString(exclude = {"dateTimes", "mainImage", "eventImages", "tags"})
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @OneToMany(
        mappedBy = "event",
        cascade = CascadeType.ALL,
        fetch = FetchType.LAZY,
        orphanRemoval = true
    )
    private List<EventDateTimeLocation> dateTimes = new ArrayList<>();

    @Column(name = "description", nullable = false)
    private String description;

    @OneToOne(cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "main_image_id", nullable = false)
    private EventImage mainImage;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "initiator_id", nullable = false)
    private User initiator;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinTable(
        name = "events_images",
        joinColumns = @JoinColumn(name = "event_id"),
        inverseJoinColumns = @JoinColumn(name = "event_image_id")
    )
    private List<EventImage> eventImages = new ArrayList<>();

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
        name = "events_tags",
        joinColumns = @JoinColumn(name = "event_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>();

    @Column(name = "is_open", nullable = false)
    private boolean isOpen = true;

    @Column(name = "is_for_all_friends", nullable = false)
    private boolean isForAllFriends = true;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
        name = "events_users_invited",
        joinColumns = @JoinColumn(name = "event_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> invitedUsers = new ArrayList<>();
}
