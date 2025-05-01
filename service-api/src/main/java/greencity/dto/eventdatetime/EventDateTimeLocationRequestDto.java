package greencity.dto.eventdatetime;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventDateTimeLocationRequestDto {
    private Long id;

    @NotNull(message = "The start date time of event can not be empty")
    private ZonedDateTime startDateTime;

    @NotNull(message = "The end date time of event can not be empty")
    private ZonedDateTime endDateTime;

    private String location;

    private String link;

    @AssertTrue(message = "Location or link must be provided")
    private boolean isLocationOrLinkProvided() {
        return (location != null && !location.isBlank())
                || (link != null && !link.isBlank());
    }
}
