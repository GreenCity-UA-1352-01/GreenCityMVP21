package greencity.dto.search;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SearchEventsDto {
    private Long id;
    private String title;
    private List<String> tags;
    private boolean isOpen;
}
