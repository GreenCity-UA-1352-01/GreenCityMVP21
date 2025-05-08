package greencity.dto.event;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
@ToString
public class EventImageDto {
    @NotBlank
    private String imagePath;
    private Boolean isMainImage;
}
