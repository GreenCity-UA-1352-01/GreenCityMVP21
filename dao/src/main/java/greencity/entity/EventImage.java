package greencity.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "event_images")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class EventImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "image_path", nullable = false)
    private String imagePath;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;
}
