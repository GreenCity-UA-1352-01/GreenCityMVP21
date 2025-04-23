package greencity.entity;

import jakarta.persistence.*;
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
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<EventDateTimeLocation> dateTimes;

    private String description;

    @OneToOne
    private EventImage mainImage;

    @ManyToMany
    @JoinTable(
        name = "events_images",
        joinColumns = @JoinColumn(name = "event_id"),
        inverseJoinColumns = @JoinColumn(name = "event_image_id"))
    private List<EventImage> eventImages;

    @ManyToMany
    @JoinTable(
        name = "events_tags",
        joinColumns = @JoinColumn(name = "event_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private Set<Tag> tags;

    private boolean isOpen;
}
