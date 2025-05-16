package greencity.dto.event;

import greencity.annotations.UniqueEventDates;
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
@UniqueEventDates
public class CreateEventDto {

    @NotBlank
    @Size(max = 70, message = "Title must be no longer than 70 characters")
    private String title;

    @NotBlank
    @Size(min = 20, max = 63206, message = "Description must be between 20 and 63,206 characters")
    private String description;

    private String mainImage;

    private Boolean open;

    @NotNull
    @Size(min = 1, max = 7, message = "No more than 7 date/time entries allowed")
    private List<@Valid EventDateLocationDto> dates;

    private List<String> tags;

    private Boolean online;

    @NotNull
    @Size(min = 1, message = "At least one initiative type must be selected")
    private List<String> initiativeTypes;

}
