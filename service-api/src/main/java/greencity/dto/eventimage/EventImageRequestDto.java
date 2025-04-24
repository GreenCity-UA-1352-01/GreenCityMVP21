package greencity.dto.eventimage;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventImageRequestDto {
    private Long id;

    @NotBlank(message = "Image path can not be empty")
    @Pattern(
            regexp = "https?://.*\\.(?i)(jpg|jpeg|png)$",
            message = "Invalid image format"
    )
    private String imagePath;

}
