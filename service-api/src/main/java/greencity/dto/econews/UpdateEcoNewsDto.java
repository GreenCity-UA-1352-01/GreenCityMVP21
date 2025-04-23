package greencity.dto.econews;

import greencity.constant.ServiceValidationConstants;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
public class UpdateEcoNewsDto {
    @NotNull
    @Pattern(regexp = "^[0-9]+$", message = "ID must contain only digits")
    private String id;

    @NotEmpty
    @Size(min = 1, max = 170)
    private String title;

    @NotEmpty
    @Size(min = 20, max = 63206)
    private String content;

    private String shortInfo;

    @NotEmpty(message = ServiceValidationConstants.MIN_AMOUNT_OF_TAGS)
    private List<String> tags;

    private String image;

    private String source;

    @Size(min = 20, max = 63206, message = "Text length must be between 20 and 63206 characters")
    private String text;


}
