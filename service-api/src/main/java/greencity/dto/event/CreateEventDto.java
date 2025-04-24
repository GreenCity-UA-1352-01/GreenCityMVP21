package greencity.dto.event;

import greencity.annotations.ValidEventType;
import greencity.dto.tag.TagUaEnDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
@ToString
@ValidEventType
public class CreateEventDto {
    private Long id;

    @NotBlank
    @Size(max = 70, message = "Title must be no longer than 70 characters")
    private String title;

    @Size(min = 20, max = 63206, message = "Description must be between 20 and 63,206 characters")
    private String description;

    @Size(max = 5)
    private List<EventImageDto> images;

    private String titleImage;

    private Boolean open;

    @NotNull
    @Size(min = 1, max = 7, message = "No more than 7 date/time entries allowed")
    private List<@Valid EventDateLocationDto> dates;

    private List<String> additionalImages;

    private List<TagUaEnDto> tags;

    private Boolean place;
    private Boolean online;

    @NotNull
    @Size(min = 1, message = "At least one initiative type must be selected")
    private List<String> initiativeTypes;
}
