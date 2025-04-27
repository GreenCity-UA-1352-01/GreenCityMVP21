package greencity.dto.event;

import greencity.dto.eventdatetime.EventDateTimeLocationRequestDto;
import greencity.dto.eventimage.EventImageRequestDto;
import greencity.dto.tag.TagUaEnDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateEventDtoRequest {
    @NotNull
    @Min(1)
    private Long id;
    @NotBlank(message = "The title of event can not be empty")
    @Size(max = 70, message = "The title of event can not be longer than 70 characters")
    private String title;

    @NotBlank(message = "The description of event can not be empty")
    @Size(min = 20, max = 63206, message = "The description is too short or too long")
    private String description;

    @NotEmpty(message = "The sessions of event can not be empty")
    @Valid
    private List<EventDateTimeLocationRequestDto> dateTimes;

    @NotBlank(message = "Image path can not be empty")
    @Pattern(
            regexp = ".*\\.(?i)(jpg|jpeg|png)$",
            message = "Invalid image format"
    )
    private String mainImage;

//    @Size(max = 5, message = "Maximum number of images is 5")
//    @Valid
//    private List<EventImageRequestDto> eventImages;

    @NotEmpty(message = "The tags of event can not be empty")
    private List<String> tags;
    private boolean isOpen;
}
